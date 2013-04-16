package net.retorx.audio
import java.io.InputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.AudioFormat
import java.io.BufferedInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class SampleStream(inputStream : InputStream) {
	val originalAIS = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream))
	val originalFormat = originalAIS.getFormat
	println("Original format is: " + originalFormat)
	
	val decodedFormat = new AudioFormat(
			AudioFormat.Encoding.PCM_SIGNED,
			originalFormat.getSampleRate,
			16,
			originalFormat.getChannels,
			originalFormat.getChannels * 2,
			originalFormat.getSampleRate,
			false
	    )
	
	val decodedAIS = AudioSystem.getAudioInputStream(decodedFormat, originalAIS)

	val bytesPerFrame = decodedFormat.getFrameSize
	println("bytesPerFrame: " + bytesPerFrame)

	val framesPerRead = 1024

	val buf = new Array[Byte](framesPerRead * bytesPerFrame)
	var byteBuf : ByteBuffer = null

	var bytesInCurrentBuf = -1
	var posInCurrentBuf = -1
	
	def sampleRate = decodedFormat.getSampleRate
	def channels = decodedFormat.getChannels
			
	def nextSample : List[Short] = {
	  if (posInCurrentBuf == -1 || posInCurrentBuf >= bytesInCurrentBuf) {
		bytesInCurrentBuf = decodedAIS.read(buf)
		if (bytesInCurrentBuf > 0) {
			posInCurrentBuf = 0;
			byteBuf = ByteBuffer.wrap(buf,0,bytesInCurrentBuf)
			if ( ! decodedFormat.isBigEndian ) {
				byteBuf.order(ByteOrder.LITTLE_ENDIAN)
			}
		} else {
		  return List[Short]()
		}
	  }
	  
	  val sampleList = new Array[Short](decodedFormat.getChannels)
	  for (i <- 0 until decodedFormat.getChannels) {
	    try {
		    sampleList(i) = byteBuf.getShort
			posInCurrentBuf += 2 // I dunno, we've hard-coded 16 around, I'm confused.
	    } catch {
	      case e : Exception => {
	        println("Something bad happened reading from the byte buffer.")
	        e.printStackTrace
	        return List[Short]()
	      }
	    }
	  }
	  
	  List.fromArray(sampleList)
	}
}

