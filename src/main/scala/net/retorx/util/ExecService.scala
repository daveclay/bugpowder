package net.retorx.util

import java.io.IOException
import scala.io.Source

object ExecService {

    val simpleLinePrintingFunc: String => Boolean = { line =>
        println(line)
        true
    }

}

class ExecService(directory:String) {
    // Borrowed from
    // http://www.jroller.com/thebugslayer/entry/executing_external_system_commands_in

    //Execute command with user function to process each line of output.
    def exec(cmd : String)(func : String=>Boolean) : Boolean = exec(cmd.split(" "))(func)

    def execAsString(cmd: Array[String]) : String = {
        val proc = newProc(cmd)
        val string = Source.fromInputStream(proc.getInputStream).mkString
        proc.waitFor
        string
    }

    private def newProc(cmd: Array[String]) = {
        new ProcessBuilder(cmd: _*).redirectErrorStream(true).directory(new java.io.File(directory)).start()
    }

    def exec(cmd : Array[String])(func : String=>Boolean) : Boolean = {
        try {
            println(this + " running " + cmd.reduceLeft(_+" " +_) + " in " + directory)
            val proc = newProc(cmd)

            val ins = new java.io.BufferedReader(new java.io.InputStreamReader(proc.getInputStream))
            //spin off a thread to read process output.
            var success = true;
            val outputReaderThread = new Thread(new Runnable(){
                def run() {
                    var ln : String = null
                    while({ln = ins.readLine; ln != null}) {
                        println(directory + ": " + ln)
                        if (!func(ln)) {
                            success = false;
                            proc.destroy()
                            return
                        }
                    }
                }
            })
            outputReaderThread.start()

            //suspense this main thread until sub process is done.
            val exitStatus = proc.waitFor
            println("Exit status was " + exitStatus)
            success = exitStatus == 0

            //wait until output is fully read/completed.
            outputReaderThread.join()

            ins.close()

            success
        } catch {
            case e:IOException => {
                println(this + " Failed executing " + cmd.reduceLeft(_+" " +_) + " in " + directory + ": " + e.getMessage)
                throw e
            }
        }
    }

    //Execute command with list of output string
    def execResultAsList(cmd : String) : List[String] = execResultAsList(cmd.split(" "))

    def execResultAsList(cmd : Array[String]) : List[String] = {
        var ls : List[String] = Nil
        exec(cmd){ ln =>
            ls = ln :: ls
            true
        }
        ls
    }
}
