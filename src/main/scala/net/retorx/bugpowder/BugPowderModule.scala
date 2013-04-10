package net.retorx.bugpowder

import com.google.inject.AbstractModule
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import net.retorx.web.JacksonScalaContextResolver

class BugPowderModule extends AbstractModule {

    def configure() {
        bind(classOf[JacksonScalaContextResolver])
        bind(classOf[JacksonJaxbJsonProvider])
        bind(classOf[FearService])
        bind(classOf[CNN])
    }
}
