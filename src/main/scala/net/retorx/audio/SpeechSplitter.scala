package net.retorx.audio

import java.io.BufferedInputStream
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Arrays
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine
import java.io.DataOutputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.InputStream
import scala.util.Random

class SpeechSplitter(inputStream:InputStream, fileNameBase:String) {
  
	val random = new Random()
	
    var nextFileTag = random.nextInt.toHexString
    var outputStream : DataOutputStream = null;
      
	var quietSamples = 0
	var loudSamples = 0;
    
    val silenceThreshold = 1500
    val secondsOfSilence = 0.1
    
    var writingToFile = false
  
    def split() {
		swapOutputFile()
		
		var ais:AudioInputStream = null
		try {
			ais = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream))

			val format = ais.getFormat
			println("format is: " + format)
			
			val decodedFormat = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED,
					format.getSampleRate,
					16,
					format.getChannels,
					format.getChannels * 2,
					format.getSampleRate,
					false
			    )
			
			val decodedAis = AudioSystem.getAudioInputStream(decodedFormat, ais)

			val bytesPerFrame = decodedFormat.getFrameSize
			println("bytesPerFrame: " + bytesPerFrame)
			
			
			val framesPerRead = 1024
			
			val framesOfSilence = (decodedFormat.getFrameRate() * secondsOfSilence)
			println("Waiting for " + secondsOfSilence + "s of silence, which is " + framesOfSilence + " frames at " + decodedFormat.getFrameRate() + " Hz")
			
			val buf = new Array[Byte](framesPerRead * bytesPerFrame)
			var totalFramesRead = 0
			var numBytesRead = 0

			do {
				numBytesRead = decodedAis.read(buf)
				if (numBytesRead > 0) {
					val numFramesRead = numBytesRead / bytesPerFrame
					totalFramesRead += numFramesRead
					
					var i = 0
					while (i < numBytesRead) {
						val byteBuf = ByteBuffer.allocate(2)
						if ( ! decodedFormat.isBigEndian ) {
							byteBuf.order(ByteOrder.LITTLE_ENDIAN)
						}
						
						val bytesToProcess = bytesPerFrame / decodedFormat.getChannels
						for (j <- 0 until bytesToProcess) {
							byteBuf.put(buf(i + j))
						}
						
						val sample = byteBuf.getShort(0)
						
						if (Math.abs(sample) < silenceThreshold) {
						  quietSamples += 1
						} else {
						  if (loudSamples == 0) {
						    writingToFile = true
						  }
						  loudSamples += 1
						  quietSamples = 0
						}
						
						if (quietSamples > framesOfSilence) {
						  if (loudSamples > 0) {
						  	swapOutputFile()
						  } else {
						    quietSamples = 0
						  }
						}

						if (writingToFile)
							outputStream.writeShort(sample)

						i += bytesPerFrame
					}
					
				}
			} while (numBytesRead != -1)
		}
      
    }
    
	private def swapOutputFile() {

	  if (outputStream != null) {
	    outputStream.close();
	  }

	  val newFileName = fileNameBase + nextFileTag + ".raw"
	  println("Opening " + newFileName + " after " + loudSamples + " loud samples and " + quietSamples + " quiet samples")
	  outputStream = new DataOutputStream(new FileOutputStream(newFileName))
      nextFileTag = random.nextInt.toHexString
      quietSamples = 0
      loudSamples = 0
      writingToFile = false

	}
	
  
  
}

object SpeechSplitter extends App {
  
	override def main(args:Array[String]) {
	
		val fileName = args(0)
		
		val is = new FileInputStream(fileName)
		if (is == null) {
			println("Could not find file '" + fileName + "'.")
			System.exit(1)
		}
		
		val speechSplitter = new SpeechSplitter(is,args(1))
		
		speechSplitter.split()
						
	}
	
}