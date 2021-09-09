//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.actionsoft.bpms.api.common;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import java.io.IOException;
import java.io.OutputStream;

public class JsonMarshaller implements ApiMarshaller {
    private static ObjectMapper objectMapper;

    public JsonMarshaller() {
    }

    public void marshaller(Object object, OutputStream outputStream) {
        try {
            JsonGenerator jsonGenerator = getObjectMapper().getFactory().createGenerator(outputStream, JsonEncoding.UTF8);
            getObjectMapper().writeValue(jsonGenerator, object);
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }
    }

    private static ObjectMapper getObjectMapper() throws IOException {
        if (objectMapper == null) {
            ObjectMapper tmp = new ObjectMapper();
            AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(tmp.getTypeFactory());
            SerializationConfig serializationConfig = tmp.getSerializationConfig();
            serializationConfig = serializationConfig.without(SerializationFeature.WRAP_ROOT_VALUE).with(SerializationFeature.INDENT_OUTPUT).withInsertedAnnotationIntrospector(introspector);
            tmp.setConfig(serializationConfig);
            objectMapper = tmp;
        }

        return objectMapper;
    }

    public <T> T unmarshaller(String str, Class<T> apiResponse) {
        try {
            return getObjectMapper().readValue(str, apiResponse);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    public <T> T unmarshaller(String str, JavaType t) {
        try {
            return getObjectMapper().readValue(str, t);
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }
}
