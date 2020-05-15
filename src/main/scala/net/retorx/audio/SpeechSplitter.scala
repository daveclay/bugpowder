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
import org.kohsuke.args4j.CmdLineException
import scala.jdk.CollectionConverters._

class Clip(val audioStream: AudioInputStream, val positionInOriginal: Long) {

}

trait ClipHandler {
  def handleClip(clip: Clip)
}

class SpeechSplitter(inputStream: InputStream, silencePercentage: Double, secondsOfSilence: Double, clipHandler: ClipHandler) {

  val sampleStream = new SampleStream(inputStream)

  var outputStream: DataOutputStream = null
  var outputBuffer: ByteArrayOutputStream = null

  var samplesRead = 0L
  var currentClipStartPosInOriginalStream = 0L

  var quietSamples = 0
  var loudSamples = 0

  var writingToClip = false

  def split() {
    closeClip()

    try {

      val silenceThreshold = pow(2, sampleStream.sampleSizeInBits - 1) * silencePercentage
      val samplesOfSilence = (sampleStream.sampleRate * secondsOfSilence)

      println("Waiting for " + secondsOfSilence + "s of silence, which is " + samplesOfSilence + " samples at " + sampleStream.sampleRate + " Hz")
      println("Silence threshold is " + silencePercentage + " as a percentage of max, or " + silenceThreshold + " amplitude for signed " + sampleStream.sampleSizeInBits + " bit audio.")

      var sampleList: List[Short] = null
      do {
        sampleList = sampleStream.nextSample

        if (sampleList != null && sampleList.size > 0) {

          val channelOneSample = sampleList(0)

          if (Math.abs(channelOneSample) < silenceThreshold) {
            quietSamples += 1
          } else {
            if (loudSamples == 0) {
              currentClipStartPosInOriginalStream = samplesRead
              writingToClip = true
            }
            loudSamples += 1
            quietSamples = 0
          }

          if (quietSamples > samplesOfSilence) {
            if (loudSamples > 0) {
              closeClip()
            } else {
              quietSamples = 0
            }
          }

          if (writingToClip) {
            for (sample <- sampleList)
              outputStream.writeShort(sample)
          }
        }
        samplesRead += 1
      } while (sampleList != null && sampleList.size > 0)

    }

  }

  private def closeClip() {

    println("Closing clip after " + loudSamples + " loud samples and " + quietSamples + " quiet samples")

    if (outputBuffer != null) {
      val bais = new ByteArrayInputStream(outputBuffer.toByteArray())

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

      clipHandler.handleClip(new Clip(processedAudioInputStream, currentClipStartPosInOriginalStream / (sampleStream.sampleRate / 1000).toLong))

    }

    outputBuffer = new ByteArrayOutputStream()
    outputStream = new DataOutputStream(outputBuffer)

    quietSamples = 0
    loudSamples = 0
    writingToClip = false
  }
}

class SpeechSplitterBuilder {

  @Argument
  val arguments = new ArrayList[String]

  @Option(name = "-t", usage = "seconds of silence to trigger a split")
  var silenceTime = 0.125

  @Option(name = "-a", usage = "amplitude to serve as upper threshold of 'silence', as a percentage of max")
  var silenceAmplitudePercentage = 0.035

  @Option(name = "-o", usage = "output file basename")
  var outputFileBaseName: String = null

  def build: SpeechSplitter = {
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
      if (fileName.indexOf(".", startIndex) > -1) {
        endIndex = fileName.indexOf(".", startIndex)
      }
      outputFileBaseName = fileName.substring(startIndex, endIndex) + "-"
    }

    new SpeechSplitter(is, silenceAmplitudePercentage, silenceTime, new DiskWritingCompressingClipHandler(outputFileBaseName, "."))
  }
}

object SpeechSplitter extends App {

  val speechSplitterBuilder = new SpeechSplitterBuilder

  val cmdLineParser = new CmdLineParser(speechSplitterBuilder)
  try {
    cmdLineParser.parseArgument(args: _*)
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