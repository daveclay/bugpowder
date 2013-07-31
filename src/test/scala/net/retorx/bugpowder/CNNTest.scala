package net.retorx.bugpowder

import org.junit.runner.RunWith
import org.specs._
import mock.Mockito
import org.scalatest.{OneInstancePerTest, FlatSpec}
import specification.DefaultExampleExpectationsListener
import java.io.File
import org.scalatest.junit.JUnitRunner
import java.util.Date
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class CNNTest extends FlatSpec with DefaultExampleExpectationsListener with Mockito with ShouldMatchers with OneInstancePerTest {

    val cnn = new Insanity()

    "The FearSource " should "grab some images" in {
        cnn.getImages.foreach(img => {
            println(img)
        })
    }

}
