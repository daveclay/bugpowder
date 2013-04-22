package net.retorx.bugpowder
import scala.xml.XML
import java.net.URL
import net.retorx.audio.SpeechSplitter
import java.text.DecimalFormat
import java.util.Calendar
import net.retorx.audio.DiskWritingCompressingClipHandler
import java.io.File
import com.google.inject.Singleton
import net.retorx.audio.AudioCompressor
import scala.io.Source
import java.io.PrintWriter
import java.io.FileOutputStream

@Singleton
class EBCSFreshener(audioClipDirectory : String) {
  
	var fresheningThread : Thread = null
  
    def freshen() {
	  if (status == "Idle") {
		  fresheningThread = new Thread() {
	        override def run() {
	          doFreshen()
	          fresheningThread = null
	        }
	      }
		  fresheningThread.start()
	  }
	}
	
	def status() : String = {
	  if (fresheningThread == null) {
	    return "Idle";
	  } else if (fresheningThread.isAlive()) {
	    return "Running";
	  } else {
	    return "Something Odd";
	  }
	}
    
	
    private def doFreshen() {
    	if (!validateOutputDirectory(audioClipDirectory)) {
    	    println("Audio clip directory '" + audioClipDirectory + " is not an existing, writable directory. Not bothering to download audio.")
    	    return
    	}
    	
    	compressStragglingWavFiles()
    	
    	val freshenerHistoryPath = audioClipDirectory + "/" + ".freshener-history"
    	val historyFile = new File(freshenerHistoryPath)
    	if (!historyFile.exists()) {
    	  println("Creating new history file.")
    	  historyFile.createNewFile()
    	}
    	val historyFileWriter = new PrintWriter(new FileOutputStream(historyFile,true))
    	
    	val processedURLs = Source.fromFile(freshenerHistoryPath).getLines.toList
    	    	
        val foxNewsMP3URLs =
          (XML.load(new URL("http://feeds.foxnewsradio.com/foxnewsradiocom"))
              \\ "enclosure" \\ "@url").filter( url => url.text.endsWith("3") )
              
        foxNewsMP3URLs.foreach( node => {
            try {
            	val urlString = node.text
            	if ( ! processedURLs.contains(urlString) ) {
	        	    val speechSplitter = new SpeechSplitter(new URL(urlString).openStream,0.035,0.125,new DiskWritingCompressingClipHandler("foxnews-",audioClipDirectory))
	        	    speechSplitter.split()
 	        	    historyFileWriter.println(urlString)
 	        	    historyFileWriter.flush()
            	} else {
            		println("Already processed " + urlString + ".")
            	}
            } catch {
                case e : Exception => e.printStackTrace()
            }
        })

        
        val twodigitformat = new DecimalFormat("00")
        var cal = Calendar.getInstance()

        for (i <- 0 until 7) {
            val url = "http://gandalf.ddo.jp/mp3/" + cal.get(Calendar.YEAR).toString.substring(2) + twodigitformat.format(cal.get(Calendar.MONTH)+1) + twodigitformat.format(cal.get(Calendar.DAY_OF_MONTH)) + ".mp3"
            try {
            	if ( ! processedURLs.contains(url) ) {
	        	    val speechSplitter = new SpeechSplitter(new URL(url).openStream,0.035,0.125,new DiskWritingCompressingClipHandler("VOA-",audioClipDirectory))
	        	    speechSplitter.split()
 	        	    historyFileWriter.println(url)
 	        	    historyFileWriter.flush()
            	} else {
            		println("Already processed " + url + ".")
            	}
            } catch {
                case e : Exception => e.printStackTrace()
            }
            cal.roll(Calendar.DAY_OF_MONTH,-1)
        }
        
        cal = Calendar.getInstance()
        var successfullyProcessed = 0
        var attempted = 0
        while (successfullyProcessed < 1 && attempted < 7) {
            attempted += 1
            val url = "http://traffic.libsyn.com/democracynow/dn" + cal.get(Calendar.YEAR) + "-" + twodigitformat.format(cal.get(Calendar.MONTH)+1) + twodigitformat.format(cal.get(Calendar.DAY_OF_MONTH)) + "-1.mp3"
            try {
            	if ( ! processedURLs.contains(url) ) {
	        	    val speechSplitter = new SpeechSplitter(new URL(url).openStream,0.035,0.125,new DiskWritingCompressingClipHandler("DemocracyNow-",audioClipDirectory))
	        	    speechSplitter.split()
	        	    successfullyProcessed += 1
 	        	    historyFileWriter.println(url)
 	        	    historyFileWriter.flush()
            	} else {
            		println("Already processed " + url + ".")
	        	    successfullyProcessed += 1
            	}
           } catch {
                case e : Exception => e.printStackTrace()
            }
            cal.roll(Calendar.DAY_OF_MONTH,-1)
        }
        historyFileWriter.close()
    }
    
    private def validateOutputDirectory(directory : String) : Boolean = {
        val directoryFile = new File(directory)
        directoryFile.exists() && directoryFile.isDirectory() && directoryFile.canWrite()
    }
    
    private def compressStragglingWavFiles() {
	  	val fullDirectoryWavListing = new File(audioClipDirectory).list().toList.filter( fileName => fileName.endsWith(".wav") )
	  	fullDirectoryWavListing.foreach( wavFileName =>
	  	  new AudioCompressor(audioClipDirectory, wavFileName).attemptCompression()
	  	)
    }

}
