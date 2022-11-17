package com.jwt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

//     @Override
//     public void addResourceHandlers(ResourceHandlerRegistry registry) {
//         registry.addResourceHandler("/static/**")
//                 .addResourceLocations("classpath:/static/")
//                 .setCachePeriod(0);
//         registry.addResourceHandler("/bootstrap/js/**")
//                 .addResourceLocations("classpath:/static/bootstrap/js/")
//                 .setCachePeriod(0);
//         registry.addResourceHandler("/bootstrap/css/**")
//                 .addResourceLocations("classpath:/static/bootstrap/css/")
//                 .setCachePeriod(0);
//         registry.addResourceHandler("/bootstrap/assets/**")
//                 .addResourceLocations("classpath:/static/bootstrap/assets/")
//                 .setCachePeriod(0);
//         registry.addResourceHandler("/templates/**")
//                 .addResourceLocations("classpath:/templates/")
//                 .setCachePeriod(0);
//     }
}
