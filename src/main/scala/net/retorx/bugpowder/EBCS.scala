package net.retorx.bugpowder

import com.google.inject.{Inject, Singleton}
import java.io.File
import scala.util.Random
import java.io.InputStream
import java.io.BufferedInputStream
import java.io.FileInputStream

@Singleton
class EBCS(audioClipDirectory : String) {

	val clipsPerGet = 40
    val random = new Random
    
    def getAudioClips : List[String] = {
	  	val audioDirectory = new File(audioClipDirectory);
	  	if ( ! audioDirectory.exists() || ! audioDirectory.isDirectory()) {
	  	  println("Audio directory " + audioClipDirectory + " does not exist or is not a directory. This will never work.");
	  	  return List()
	  	}
	  	
	  	val fullDirectoryListing = audioDirectory.list().toList.filter( fileName => fileName.endsWith(".mp3")).map(fileName => fileName.substring(0,fileName.lastIndexOf(".mp3")))
	  	val returnVals = new Array[String](clipsPerGet)
	  	for (i <- 0 until clipsPerGet) {
	  		returnVals(i) = "api/fear/audioClip/" + fullDirectoryListing(random.nextInt(fullDirectoryListing.length))
	  	}
	  	
	  	returnVals.toList
	}
    
    def getAudioClip(clipPath : String) : InputStream = {
      val audioFile = new File(audioClipDirectory + "/" + clipPath)
      return new BufferedInputStream(new FileInputStream(audioFile))
    }
    
}
