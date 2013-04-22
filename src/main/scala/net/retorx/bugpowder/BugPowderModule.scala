package net.retorx.bugpowder

import com.google.inject.AbstractModule
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import net.retorx.web.JacksonScalaContextResolver
import com.google.inject.Provides

class BugPowderModule extends AbstractModule {
  
	val ebcsAudioClipPath = "/tmp/EBCS-audioClips"

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
