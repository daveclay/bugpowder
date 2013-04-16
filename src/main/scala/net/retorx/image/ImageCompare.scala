package net.retorx.image

import java.io.FileInputStream

object ImageCompare extends App {
	override def main(args:Array[String]) {
	  var imagePath1 = args(0)
	  var imagePath2 = args(1)
	  
	  var imageIS1 = new FileInputStream(imagePath1)
	  var imageIS2 = new FileInputStream(imagePath2)
	  
	  var hasher = new ImagePHash()
	  
	  var imageHash1 = hasher.getHash(imageIS1)
	  var imageHash2 = hasher.getHash(imageIS2)
	  
	  var distance = hasher.distance(imageHash1,imageHash2)
	  
	  println("Hamming distance is " + distance)
	}
}