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
        	val speechSplitter = new SpeechSplitter(new URL(node.text).openStream,"foxnews",0.035,0.125,audioDirectoryPath)
        	speechSplitter.split()
        })
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
