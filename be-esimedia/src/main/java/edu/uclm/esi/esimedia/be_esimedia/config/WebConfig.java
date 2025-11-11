package edu.uclm.esi.esimedia.be_esimedia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.File;
import java.io.IOException;

/**
 * Configuración para servir la aplicación Angular desde Spring Boot.
 * Redirige todas las rutas no encontradas al index.html para manejar el enrutamiento de Angular.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String PUBLIC_DIR = "/app/public/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Servir archivos estáticos desde el sistema de archivos
        registry.addResourceHandler("/**")
                .addResourceLocations("file:" + PUBLIC_DIR)
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        
                        // Si el recurso existe, devolverlo
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }
                        
                        // Si no existe y no es una llamada a la API, devolver index.html para Angular routing
                        if (!resourcePath.startsWith("api/")) {
                            File indexFile = new File(PUBLIC_DIR + "index.html");
                            if (indexFile.exists()) {
                                return new FileSystemResource(indexFile);
                            }
                        }
                        
                        return null;
                    }
                });
    }
}
