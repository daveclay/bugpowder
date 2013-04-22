package net.retorx.audio
import net.retorx.util.ExecService
import java.io.File

class AudioCompressor(outputDirectory : String, wavFileName : String) {
  val lameAvailable = attemptToRunCommandLine("lame --help")
  val oggencAvailable = attemptToRunCommandLine("oggenc -h")

  def attemptCompression() {
	    var success = false
	    if (lameAvailable) {
		    try {
			    success = new ExecService(outputDirectory,false).exec(Array("lame",wavFileName)){ line =>
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
			    success = new ExecService(outputDirectory,false).exec(Array("oggenc",wavFileName)){ line =>
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
	      new File(outputDirectory + "/" + wavFileName).delete()
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