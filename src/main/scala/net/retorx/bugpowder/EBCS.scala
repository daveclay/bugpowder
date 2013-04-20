package net.retorx.bugpowder

import com.google.inject.{Inject, Singleton}
import java.io.File
import scala.util.Random
import java.io.InputStream
import java.io.BufferedInputStream
import java.io.FileInputStream
import scala.xml.XML
import net.retorx.audio.SpeechSplitter
import java.net.URL
import java.util.Calendar
import java.text.DecimalFormat

@Singleton
class EBCS {
    val audioDirectoryPath = "/tmp/EBCS-audioClips"
    val clipsPerGet = 40
    val random = new Random
    
    new Thread() {
      override def run() {

        val foxNewsMP3URLs =
          (XML.load(new URL("http://feeds.foxnewsradio.com/foxnewsradiocom"))
              \\ "enclosure" \\ "@url").filter( url => url.text.endsWith("3") )
              
        foxNewsMP3URLs.foreach( node => {
            try {
        	    val speechSplitter = new SpeechSplitter(new URL(node.text).openStream,"foxnews-",0.035,0.125,audioDirectoryPath)
        	    speechSplitter.split()
            } catch {
                case e : Exception => e.printStackTrace()
            }
        })

        
        val twodigitformat = new DecimalFormat("00")
        var cal = Calendar.getInstance()

        for (i <- 0 until 7) {
            val url = "http://gandalf.ddo.jp/mp3/" + cal.get(Calendar.YEAR).toString.substring(2) + twodigitformat.format(cal.get(Calendar.MONTH)) + twodigitformat.format(cal.get(Calendar.DAY_OF_MONTH)) + ".mp3"
            try {
        	    val speechSplitter = new SpeechSplitter(new URL(url).openStream,"VOA-",0.035,0.125,audioDirectoryPath)
        	    speechSplitter.split()
            } catch {
                case e : Exception => e.printStackTrace()
            }
            cal.roll(Calendar.DAY_OF_MONTH,-1)
        }
        
        cal = Calendar.getInstance()
        var successfullyProcessed = 0
        var attempted = 0
        while (successfullyProcessed < 2 && attempted < 7) {
            attempted += 1
            val url = "http://traffic.libsyn.com/democracynow/dn" + cal.get(Calendar.YEAR) + "-" + twodigitformat.format(cal.get(Calendar.MONTH)) + twodigitformat.format(cal.get(Calendar.DAY_OF_MONTH)) + "-1.mp3"
            try {
        	    val speechSplitter = new SpeechSplitter(new URL(url).openStream,"DemocracyNow-",0.035,0.125,audioDirectoryPath)
        	    speechSplitter.split()
        	    successfullyProcessed += 1
            } catch {
                case e : Exception => e.printStackTrace()
            }
            cal.roll(Calendar.DAY_OF_MONTH,-1)
        }
        
      }
    }.start()

    def getAudioClips : List[String] = {
	  	val audioDirectory = new File(audioDirectoryPath);
	  	if ( ! audioDirectory.exists() || ! audioDirectory.isDirectory()) {
	  	  println("Audio directory " + audioDirectoryPath + " does not exist or is not a directory. This will never work.");
	  	  return List()
	  	}
	  	
	  	val fullDirectoryListing = audioDirectory.list().toList
	  	val returnVals = new Array[String](clipsPerGet)
	  	for (i <- 0 until clipsPerGet) {
	  		returnVals(i) = "api/fear/audioClip/" + fullDirectoryListing(random.nextInt(fullDirectoryListing.length))
	  	}
	  	
	  	returnVals.toList
	}
    
    def getAudioClip(clipPath : String) : InputStream = {
      val audioFile = new File(audioDirectoryPath + "/" + clipPath)
      return new BufferedInputStream(new FileInputStream(audioFile))
    }
    
}
