package net.retorx.bugpowder

import org.scalatest.OneInstancePerTest
import org.scalatest.flatspec.AnyFlatSpec

class CNNTest extends AnyFlatSpec with OneInstancePerTest {

    val cnn = new Rueters()

    "The FearSource " should "grab some images" in {
        cnn.getImages.foreach(img => {
            println(img)
        })
    }
}
