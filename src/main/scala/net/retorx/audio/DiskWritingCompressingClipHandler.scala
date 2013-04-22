package net.retorx.audio
import scala.util.Random
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import net.retorx.util.ExecService
import javax.sound.sampled.AudioFileFormat
import java.io.File

class DiskWritingCompressingClipHandler(fileNameBase : String, outputDirectory : String = ".") extends ClipHandler {
  val random = new Random()
  
  override def handleClip(clip : Clip) {
	    val nextFileTag = random.nextInt.toHexString
	    val newFileName = outputDirectory + "/" + fileNameBase + nextFileTag + ".wav"
	    println("Writing " + newFileName + ", from the " + clip.positionInOriginal + "th millisecond.");
	    val outputFile = new File(newFileName)
	    
	    AudioSystem.write(clip.audioStream, AudioFileFormat.Type.WAVE, outputFile)
	    
	    new AudioCompressor(outputDirectory,newFileName).attemptCompression()
  }
 
}


