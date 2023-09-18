package com.xforceplus.wapp.swagger;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import io.swagger.models.Swagger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponents;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static springfox.documentation.swagger.common.HostNameProvider.componentsFrom;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2022-01-25 20:56
 **/
@Controller
@ApiIgnore
@ConditionalOnProperty(value = "swagger.base.ui-path")
@RequestMapping("${swagger.base.ui-path}")
@Slf4j
public class CustomerApiResourceController {


    @Autowired(required = false)
    private SecurityConfiguration securityConfiguration;
    @Autowired(required = false)
    private UiConfiguration uiConfiguration;

    private final SwaggerResourcesProvider swaggerResources;
        private static final String HAL_MEDIA_TYPE = "application/hal+json";

        private final String hostNameOverride;
        private final DocumentationCache documentationCache;
        private final ServiceModelToSwagger2Mapper mapper;
        private final JsonSerializer jsonSerializer;

    @Autowired
    public CustomerApiResourceController(SwaggerResourcesProvider swaggerResources
       ,
            Environment environment,
            DocumentationCache documentationCache,
            ServiceModelToSwagger2Mapper mapper,
            JsonSerializer jsonSerializer
    ,@Value("${swagger.base.ui-path}") String basePath
    ) {
                    this.swaggerResources = swaggerResources;
                    this.hostNameOverride =
                            environment.getProperty(
                                    "springfox.documentation.swagger.v2.host",
                                    "DEFAULT");
                    this.documentationCache = documentationCache;
                    this.mapper = mapper;
                    this.jsonSerializer = jsonSerializer;
    }

    @RequestMapping(value = "/swagger-resources/configuration/security")
    @ResponseBody
    public ResponseEntity<SecurityConfiguration> securityConfiguration() {
        return new ResponseEntity<SecurityConfiguration>(
                Optional.fromNullable(securityConfiguration).or(SecurityConfigurationBuilder.builder().build()), HttpStatus.OK);
    }

    @RequestMapping(value = "/swagger-resources/configuration/ui")
    @ResponseBody
    public ResponseEntity<UiConfiguration> uiConfiguration() {
        return new ResponseEntity<UiConfiguration>(
                Optional.fromNullable(uiConfiguration).or(UiConfigurationBuilder.builder().build()), HttpStatus.OK);
    }

    @RequestMapping
    @ResponseBody
    public ResponseEntity<List<SwaggerResource>> swaggerResources() {
        return new ResponseEntity<List<SwaggerResource>>(swaggerResources.get(), HttpStatus.OK);
    }


        @RequestMapping(
                value = "${swagger.base.ui-path}/v2/api-docs",
                method = RequestMethod.GET,
                produces = { APPLICATION_JSON_VALUE, HAL_MEDIA_TYPE })
        @ResponseBody
        public ResponseEntity<Json> getDocumentation(
                @RequestParam(value = "group", required = false) String swaggerGroup,
                HttpServletRequest servletRequest) {

                String groupName = Optional.fromNullable(swaggerGroup).or(Docket.DEFAULT_GROUP_NAME);
                Documentation documentation = documentationCache.documentationByGroup(groupName);
                if (documentation == null) {
                        log.warn("Unable to find specification for group {}", groupName);
                        return new ResponseEntity<Json>(HttpStatus.NOT_FOUND);
                }
                Swagger swagger = mapper.mapDocumentation(documentation);
                UriComponents uriComponents = componentsFrom(servletRequest, swagger.getBasePath());
                swagger.basePath(Strings.isNullOrEmpty(uriComponents.getPath()) ? "/" : uriComponents.getPath());
                if (isNullOrEmpty(swagger.getHost())) {
                        swagger.host(hostName(uriComponents));
                }
                return new ResponseEntity<Json>(jsonSerializer.toJson(swagger), HttpStatus.OK);
        }

        private String hostName(UriComponents uriComponents) {
                if ("DEFAULT".equals(hostNameOverride)) {
                        String host = uriComponents.getHost();
                        int port = uriComponents.getPort();
                        if (port > -1) {
                                return String.format("%s:%d", host, port);
                        }
                        return host;
                }
                return hostNameOverride;
        }

}
