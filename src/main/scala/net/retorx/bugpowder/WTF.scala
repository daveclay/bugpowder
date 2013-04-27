package net.retorx.bugpowder

import net.retorx.util.ExecService


object WTF {

    def main(args: Array[String]) {
        val hi = new ExecService(".", true)
        hi.exec("lame --help"){ line => true }
    }
}