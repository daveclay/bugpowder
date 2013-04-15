package net.retorx.web

import javax.servlet.{ServletContext, ServletContextEvent, ServletContextListener}
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap

import org.jboss.resteasy.spi.Registry
import org.jboss.resteasy.spi.ResteasyProviderFactory
import net.retorx.bugpowder.{CNN, BugPowderModule}

class StartupListener extends ResteasyBootstrap with ServletContextListener {

    var servletContextOpt:Option[ServletContext] = None

	override def contextDestroyed(servletContextEvent: ServletContextEvent) {}

	override def contextInitialized(servletContextEvent: ServletContextEvent) {
        servletContextOpt = Some(servletContextEvent.getServletContext)
        super.contextInitialized(servletContextEvent)

        println("Time to start this shit up")

        val context = servletContextOpt.get
        val registry = context.getAttribute(classOf[Registry].getName).asInstanceOf[Registry]
        val providerFactory = context.getAttribute(classOf[ResteasyProviderFactory].getName).asInstanceOf[ResteasyProviderFactory]
        val processor = new ModuleProcessor(registry, providerFactory)

        val module = new BugPowderModule()
        val injector = processor.process(module)
        //val fuckshit = injector.getInstance(classOf[CNN])

        println("done!")
	}

    def getInitParameter(name:String) = {
        servletContextOpt match {
            case servletContext:Some[ServletContext] => servletContext.get.getInitParameter("content.dir")
            case None => throw new IllegalStateException("No ServletContext available yet!")
        }
    }
}
