package com.project.config;

import java.io.File;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebRootConfig implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        File webRoot = new File("web");
        if (webRoot.isDirectory()) {
            factory.setDocumentRoot(webRoot);
        }
    }
}
