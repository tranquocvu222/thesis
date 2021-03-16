package ces.riccico.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.TagsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	 private ApiInfo apiInfo() {
		 return new ApiInfoBuilder()
		            .title("Sig-Predict REST API Document")
		            .description("work in progress")
		            .termsOfServiceUrl("localhost")
		            .version("1.0")
		            .build();
	    }
	 
	 private ApiKey apiKey() {
		    return new ApiKey("jwtToken", "Authorization", "header");
		}

	    @Bean
	    public Docket api() {
	        return new Docket(DocumentationType.SWAGGER_2)
	        	     .select()
	                 .apis(RequestHandlerSelectors.any())
	                 .paths(PathSelectors.any())
	                 .build()
	                 .apiInfo(apiInfo())
	                 .securitySchemes(Arrays.asList(apiKey()));
	    }

	    /**
	     * SwaggerUI information
	     */

	    @Bean
	    UiConfiguration uiConfig() {
	        return UiConfigurationBuilder.builder()
	                .deepLinking(true)
	                .displayOperationId(false)
	                .defaultModelsExpandDepth(1)
	                .defaultModelExpandDepth(1)
	                .defaultModelRendering(ModelRendering.EXAMPLE)
	                .displayRequestDuration(false)
	                .docExpansion(DocExpansion.NONE)
	                .filter(false)
	                .maxDisplayedTags(null)
	                .operationsSorter(OperationsSorter.ALPHA)
	                .showExtensions(false)
	                .tagsSorter(TagsSorter.ALPHA)
	                .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
	                .validatorUrl(null)
	                .build();
	    }

//	    @Bean
//	    public EmailAnnotationPlugin emailPlugin() {
//	        return new EmailAnnotationPlugin();
//	    }

}
