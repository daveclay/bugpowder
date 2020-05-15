package net.retorx.bugpowder

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.OneInstancePerTest

class MegaphoneTest extends AnyFlatSpec with OneInstancePerTest {

    val megaphone = new Megaphone()

    "The megaphone" should "grab some shit" in {
        println(megaphone.getCutups)
    }
}
