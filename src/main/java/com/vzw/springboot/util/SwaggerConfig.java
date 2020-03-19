/*
Copyright Verizon Inc. 
Licensed under the terms of the Apache License 2.0 license.  See LICENSE file in project root for terms.
*/
package com.vzw.springboot.util;
 
// The static import is used for the regex(..) method.
import static springfox.documentation.builders.PathSelectors.regex;
 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
 
@Configuration
// This annotation enables the Swagger support in the application.
@EnableSwagger2
public class SwaggerConfig {
 
    @Bean
    public Docket postsApi() {
        //return new Docket(DocumentationType.SWAGGER_2).apiInfo(metadata()).select().paths(regex("/echo.*")).build();
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(metadata()).select().paths(PathSelectors.any()).build();

    }
 
    @SuppressWarnings("deprecation")
    private ApiInfo metadata() {
        return new ApiInfoBuilder().title("YANG Transformer").description("API reference guide for developers").build();  
    }
}
