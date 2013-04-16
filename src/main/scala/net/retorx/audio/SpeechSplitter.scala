package net.retorx.audio

import java.io.FileInputStream
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import java.io.DataOutputStream
import java.io.InputStream
import scala.util.Random
import java.net.URL
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.AudioFileFormat

class SpeechSplitter(inputStream:InputStream, fileNameBase:String) {
  
	val random = new Random()
	
	val sampleStream = new SampleStream(inputStream)
	
    var outputStream : DataOutputStream = null
	var outputBuffer : ByteArrayOutputStream = null
	
	var quietSamples = 0
	var loudSamples = 0;
    
    val silenceThreshold = 1500
    val secondsOfSilence = 0.1
    
    var writingToFile = false
      
    def split() {
		swapOutputFile()

		try {
			val samplesOfSilence = (sampleStream.sampleRate * secondsOfSilence)
			println("Waiting for " + secondsOfSilence + "s of silence, which is " + samplesOfSilence + " samples at " + sampleStream.sampleRate + " Hz")
			
			var sampleList : List[Short] = null
			do {
				sampleList = sampleStream.nextSample
				
				if (sampleList != null && sampleList.size > 0) {
				
					val channelOneSample = sampleList(0)
					
					if (Math.abs(channelOneSample) < silenceThreshold) {
					  quietSamples += 1
					} else {
					  if (loudSamples == 0) {
					    writingToFile = true
					  }
					  loudSamples += 1
					  quietSamples = 0
					}
					
					if (quietSamples > samplesOfSilence) {
					  if (loudSamples > 0) {
					  	swapOutputFile()
					  } else {
					    quietSamples = 0
					  }
					}
	
					if (writingToFile) {
					    for (sample <- sampleList)
					    	outputStream.writeShort(sample)
					}
				}
			} while (sampleList != null && sampleList.size > 0)
			
		}
      
    }
    
	private def swapOutputFile() {

	  if (outputBuffer != null) {	    
	    val bais = new ByteArrayInputStream(outputBuffer.toByteArray())

	    val nextFileTag = random.nextInt.toHexString
	    val newFileName = fileNameBase + nextFileTag + ".wav"
	    println("Opening " + newFileName + " after " + loudSamples + " loud samples and " + quietSamples + " quiet samples")
	    val outputFile = new File(newFileName)

		val baisFormat = new AudioFormat(
				AudioFormat.Encoding.PCM_SIGNED,
				sampleStream.sampleRate,
				16,
				sampleStream.channels,
				sampleStream.channels * 2,
				sampleStream.sampleRate,
				true
		    )
	    
	    val processedAudioInputStream = new AudioInputStream(bais, baisFormat, bais.available() / baisFormat.getFrameSize)
	    
	    AudioSystem.write(processedAudioInputStream, AudioFileFormat.Type.WAVE, outputFile)

	  }

	  outputBuffer = new ByteArrayOutputStream()
	  outputStream = new DataOutputStream(outputBuffer)

      quietSamples = 0
      loudSamples = 0
      writingToFile = false

	}
}

object SpeechSplitter extends App {
  
	override def main(args:Array[String]) {
	
		var fileName = args(0)
		val is =
			if (fileName.indexOf(":") == -1)
				new FileInputStream(fileName)
			else
			  	new URL(fileName).openStream()

		if (is == null) {
			println("Could not find file '" + fileName + "'.")
			System.exit(1)
		}
		
		val speechSplitter = new SpeechSplitter(is,args(1))
		
		speechSplitter.split()
						
	}
	
}