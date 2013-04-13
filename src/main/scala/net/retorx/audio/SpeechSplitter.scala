package net.retorx.audio

import java.io.BufferedInputStream
import java.io.FileInputStream

import java.nio.ByteBuffer
import java.nio.ByteOrder

import java.util.Arrays

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;


object SpeechSplitter extends App {
	override def main(args:Array[String]) {
	
		val fileName = args(0)
		
		val is = new FileInputStream(fileName)
		if (is == null) {
			println("Could not find file '" + fileName + "'.")
			System.exit(1)
		}
		
		var ais:AudioInputStream = null
		try {
			ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is))

			val format = ais.getFormat
			println("format is: " + format)

			val bytesPerFrame = ais.getFormat.getFrameSize
			println("bytesPerFrame: " + bytesPerFrame)
			
			
			val framesPerRead = 1024
			
			val buf = new Array[Byte](framesPerRead * bytesPerFrame)
			var totalFramesRead = 0
			var numBytesRead = 0
			do {
				numBytesRead = ais.read(buf)
				println("Read " + numBytesRead + " bytes")
				if (numBytesRead > 0) {
					val numFramesRead = numBytesRead / bytesPerFrame
					totalFramesRead += numFramesRead
					println("Read " + numFramesRead + " frames.")
					
					var i = 0
					while (i < numBytesRead) {
						val byteBuf = ByteBuffer.allocate(2)
						if ( ! format.isBigEndian ) {
							byteBuf.order(ByteOrder.LITTLE_ENDIAN)
						}
						for (j <- 0 until bytesPerFrame) {
							byteBuf.put(buf(i + j))
						}
						
						val sample = byteBuf.getShort	(0)
						
						println("  " + sample)

						i += bytesPerFrame
					}
					
				}
			} while (numBytesRead != -1)
		}
				
	}
	
}