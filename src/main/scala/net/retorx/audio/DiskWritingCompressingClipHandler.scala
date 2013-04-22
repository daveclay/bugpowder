package net.retorx.audio
import scala.util.Random
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import net.retorx.util.ExecService
import javax.sound.sampled.AudioFileFormat
import java.io.File

class DiskWritingCompressingClipHandler(fileNameBase : String, outputDirectory : String = ".") extends ClipHandler {
  val random = new Random()
  val lameAvailable = attemptToRunCommandLine("lame --help")
  val oggencAvailable = attemptToRunCommandLine("oggenc -h")
  
  override def handleClip(clip : Clip) {
	    val nextFileTag = random.nextInt.toHexString
	    val newFileName = outputDirectory + "/" + fileNameBase + nextFileTag + ".wav"
	    println("Writing " + newFileName + ", from the " + clip.positionInOriginal + "th millisecond.");
	    val outputFile = new File(newFileName)
	    
	    AudioSystem.write(clip.audioStream, AudioFileFormat.Type.WAVE, outputFile)

	    var success = false
	    if (lameAvailable) {
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
  		}
	    
	    if (oggencAvailable) {
		    try {
			    success = new ExecService(outputDirectory,false).exec(Array("oggenc",newFileName)){ line =>
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
	    }

	    if (lameAvailable && oggencAvailable && success) {
	      println("Successfully compressed MP3 and/or OGG.")
	      outputFile.delete()
	    } else {
	      println("FAILED MP3 and/or OGG COMPRESSION")
	    }

	    
  }
  
  private def attemptToRunCommandLine(command : String) : Boolean = {
    val execService = new ExecService(".",false)
    var success = false
    try {
      success = execService.exec(command){ line => true }
    } catch {
      case e : Exception => e.printStackTrace()
    }
    success
  }
}


