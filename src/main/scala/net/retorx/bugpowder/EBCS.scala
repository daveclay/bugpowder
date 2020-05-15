package net.retorx.bugpowder

import java.io.File

import scala.util.Random
import java.io.InputStream
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.FilenameFilter

import javax.servlet.ServletContext
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.stereotype.Service
import org.springframework.web.context.support.ServletContextResource

@Service
class EBCS(@Value("${ebcsAudioClipPath}") audioClipDirectory: String) {
    val AUDIO_CONTEXT_PATH = "audio/"
	val clipsPerGet = 40
    val random = new Random
    
    def getAudioClips : List[AudioClipSpec] = {
	  	val audioDirectory = new File(audioClipDirectory);
	  	if ( ! audioDirectory.exists() || ! audioDirectory.isDirectory()) {
	  	  println("Audio directory " + audioClipDirectory + " does not exist or is not a directory. This will never work.");
	  	  return List()
	  	}
	  	
	  	val fullDirectoryListing = audioDirectory.list().toList.filter( fileName => (fileName.endsWith(".ogg") || fileName.endsWith(".wav") || fileName.endsWith("mp3"))).map(fileName => fileName.substring(0,fileName.lastIndexOf("."))).distinct
	  	val returnVals = new Array[AudioClipSpec](clipsPerGet)
	  	for (i <- 0 until clipsPerGet) {
	  	    val fileName = fullDirectoryListing(random.nextInt(fullDirectoryListing.length))
            val formats = getFormats(fileName)
            val urlToAudio = AUDIO_CONTEXT_PATH + fileName
            returnVals(i) = AudioClipSpec(formats, urlToAudio)
	  	}
	  	
	  	returnVals.toList
	}
    
    def getAudioClip(clipPath : String) : InputStream = {
      val audioFile = new File(audioClipDirectory + "/" + clipPath)
      return new BufferedInputStream(new FileInputStream(audioFile))
    }
    
    private def getFormats(fileName : String) : List[String] = {
	  	val audioDirectory = new File(audioClipDirectory)
	  	audioDirectory.listFiles(new FilenameFilter() {
	  	  override def accept(dir : File, name : String) = name.startsWith(fileName)
	  	}).map(file => file.getName().substring(file.getName().lastIndexOf(".") + 1)).toList
    }
    
}

case class AudioClipSpec(formats : List[String], file : String)
