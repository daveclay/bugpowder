package net.retorx.bugpowder

import org.junit.runner.RunWith
import org.specs._
import mock.Mockito
import org.scalatest.{OneInstancePerTest, FlatSpec}
import specification.DefaultExampleExpectationsListener
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class MegaphoneTest extends FlatSpec with DefaultExampleExpectationsListener with Mockito with ShouldMatchers with OneInstancePerTest {

    val megaphone = new Megaphone()

    "The megaphone" should "grab some shit" in {
        println(megaphone.getCutups)
    }
}
