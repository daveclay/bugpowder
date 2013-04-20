package net.retorx.bugpowder

import com.google.inject.{Inject, Singleton}
import java.io.File
import scala.util.Random

@Singleton
class EBCS {
    val audioDirectoryPath = "./src/main/webapp/ebcs"
    val clipsPerGet = 20
    val random = new Random

    def getAudioClips : List[String] = {
	  	val audioDirectory = new File(audioDirectoryPath);
	  	if ( ! audioDirectory.exists() || ! audioDirectory.isDirectory()) {
	  	  println("Audio directory " + audioDirectoryPath + " does not exist or is not a directory. This will never work.");
	  	  return List()
	  	}
	  	
	  	val fullDirectoryListing = audioDirectory.list().toList
	  	val returnVals = new Array[String](20)
	  	for (i <- 0 until clipsPerGet) {
	  		returnVals(i) = "ebcs/" + fullDirectoryListing(random.nextInt(fullDirectoryListing.length))
	  	}
	  	
	  	returnVals.toList
	}
}
