package net.retorx.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.module.scala.DefaultScalaModule;
import com.google.inject.Singleton;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

@Singleton
@Provider
@Consumes({MediaType.APPLICATION_JSON, "application/*+json", "text/json"})
@Produces({MediaType.APPLICATION_JSON, "application/*+json", "text/json"})
public class JacksonScalaContextResolver extends JacksonJaxbJsonProvider {

    public JacksonScalaContextResolver() {
        super(getObjectMapper(), DEFAULT_ANNOTATIONS);
    }

    public static ObjectMapper getObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new DefaultScalaModule());
        return mapper;
	}
}
