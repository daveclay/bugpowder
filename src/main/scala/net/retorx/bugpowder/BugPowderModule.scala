package net.retorx.bugpowder

import com.google.inject.AbstractModule
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import net.retorx.web.JacksonScalaContextResolver
import com.google.inject.Provides
import BugPowder._

class BugPowderModule extends AbstractModule {

    def configure() {
        bind(classOf[JacksonScalaContextResolver])
        bind(classOf[JacksonJaxbJsonProvider])
        bind(classOf[FearService])
        bind(classOf[CNN])
    }

	@Provides
	def provideEBCS = new EBCS(ebcsAudioClipPath)
	
	@Provides
	def provideEBCSFreshener = new EBCSFreshener(ebcsAudioClipPath)

}

object BugPowder {

    val ebcsAudioClipPath = "/tmp/EBCS-audioClips"

}
