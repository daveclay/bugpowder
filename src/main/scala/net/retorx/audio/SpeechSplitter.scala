package net.retorx.audio

import java.io.FileInputStream
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import java.io.DataOutputStream
import java.io.InputStream
import scala.math._
import scala.util.Random
import java.net.URL
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import java.io.File
import javax.sound.sampled.AudioFileFormat
import java.util.ArrayList
import org.kohsuke.args4j.Argument
import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import java.util.Arrays
import collection.JavaConversions._
import org.kohsuke.args4j.CmdLineException

class SpeechSplitter(inputStream:InputStream, fileNameBase:String, silencePercentage: Double, secondsOfSilence: Double) {
  
	val random = new Random()
	
	val sampleStream = new SampleStream(inputStream)
	
    var outputStream : DataOutputStream = null
	var outputBuffer : ByteArrayOutputStream = null
	
	var quietSamples = 0
	var loudSamples = 0;
    
    var writingToFile = false
      
    def split() {
		swapOutputFile()

		try {
		  
			val silenceThreshold = pow(2,sampleStream.sampleSizeInBits - 1) * silencePercentage
			val samplesOfSilence = (sampleStream.sampleRate * secondsOfSilence)
			
			println("Waiting for " + secondsOfSilence + "s of silence, which is " + samplesOfSilence + " samples at " + sampleStream.sampleRate + " Hz")
			println("Silence threshold is " + silencePercentage + " as a percentage of max, or " + silenceThreshold + " amplitude for signed " + sampleStream.sampleSizeInBits + " bit audio.")
			
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

class SpeechSplitterBuilder {

	@Argument
	val arguments = new ArrayList[String]
    
    @Option(name="-t",usage="seconds of silence to trigger a split")
    var silenceTime = 0.125
    
    @Option(name="-a",usage="amplitude to serve as upper threshold of 'silence', as a percentage of max")
    var silenceAmplitudePercentage = 0.035
    
    @Option(name="-o",usage="output file basename")
    var outputFileBaseName : String = null
    
    def build : SpeechSplitter = {
      println("silenceAmplitudePercentage is " + silenceAmplitudePercentage)
      println("silenceTime is " + silenceTime)
      println("Arguments are: " + arguments)
      
		var fileName = arguments.get(0)
		val is =
			if (fileName.indexOf(":") == -1)
				new FileInputStream(fileName)
			else
			  	new URL(fileName).openStream()

		if (is == null) {
			println("Could not find file '" + fileName + "'.")
			System.exit(1)
		}

      	if (outputFileBaseName == null) {
      	  var startIndex = 0
      	  if (fileName.indexOf("/") > -1) {
      	    startIndex = fileName.lastIndexOf("/") + 1
      	  }
      	  var endIndex = fileName.length
      	  if (fileName.indexOf(".",startIndex) > -1) {
      	    endIndex = fileName.indexOf(".",startIndex)
      	  }
      	  outputFileBaseName = fileName.substring(startIndex,endIndex) + "-"
      	}
      
		new SpeechSplitter(is,outputFileBaseName,silenceAmplitudePercentage,silenceTime)
	}
}

object SpeechSplitter extends App {
  
	override def main(args:Array[String]) {
      
      val speechSplitterBuilder = new SpeechSplitterBuilder

      val cmdLineParser = new CmdLineParser(speechSplitterBuilder)
      try {
	      cmdLineParser.parseArgument(args.toSeq)
      } catch {
        case e: CmdLineException => {
          println(e.getMessage)
          cmdLineParser.printUsage(System.out)
          sys.exit
        }
      }
		
      val speechSplitter = speechSplitterBuilder.build
  	  speechSplitter.split()
						
	}
	
}