package net.retorx.audio
import scala.util.Random
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import net.retorx.util.ExecService
import javax.sound.sampled.AudioFileFormat
import java.io.File

class DiskWritingCompressingClipHandler(fileNameBase : String, outputDirectory : String = ".") extends ClipHandler {
  val random = new Random()
	
  override def handleClip(clip : AudioInputStream) {
	    val nextFileTag = random.nextInt.toHexString
	    val newFileName = outputDirectory + "/" + fileNameBase + nextFileTag + ".wav"
	    println("Writing " + newFileName + ".");
	    val outputFile = new File(newFileName)
	    
	    AudioSystem.write(clip, AudioFileFormat.Type.WAVE, outputFile)

	    var success = false
	    try {
		    success = new ExecService(outputDirectory,false).exec(Array("lame",newFileName)){ line =>
		      if (line.indexOf("not found") > -1) {
		        false
		      } else {
		        true
		      }
		    }
	    } catch {
	      case e : Exception => {
	        e.printStackTrace()
	      	success = false
	      }
	    }
	    
	    var finalFileName = newFileName
	    if (success) {
	      println("Successfully compressed MP3.")
	      outputFile.delete()
	      finalFileName = fileNameBase + nextFileTag + ".mp3"
	    } else {
	      println("FAILED MP3 COMPRESSION")
	    }

	    
  }
}


