package net.retorx.bugpowder

import com.sun.syndication.io.{XmlReader, SyndFeedInput}
import com.sun.syndication.feed.synd.{SyndEntry, SyndFeedImpl}
import java.net.URL
import collection.JavaConversions._

object Main {

    def main(args: Array[String]) {
        val feedSource = new URL("http://rss.cnn.com/rss/cnn_topstories.rss")
        val feedInput = new SyndFeedInput()
        val synfeed = feedInput.build(new XmlReader(feedSource))
        val entries: Seq[SyndEntry] = synfeed.getEntries.asInstanceOf[java.util.ArrayList[SyndEntry]]
        entries.foreach(entry => {
            println(entry.getTitle)
        })

    }
}