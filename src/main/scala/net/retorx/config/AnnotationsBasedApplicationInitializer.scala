package net.retorx.config

import org.springframework.web.context.AbstractContextLoaderInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

class AnnotationsBasedApplicationInitializer extends AbstractContextLoaderInitializer {
  override def createRootApplicationContext(): WebApplicationContext = {
    new AnnotationConfigWebApplicationContext()
  }
}
