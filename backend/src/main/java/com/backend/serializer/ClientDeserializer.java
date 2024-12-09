package com.backend.serializer;

import com.backend.entity.Client;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class ClientDeserializer extends JsonDeserializer<Client> {
    @Override
    public Client deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Integer id = p.getIntValue();
        return Client.builder().id(id).build();
    }
}
