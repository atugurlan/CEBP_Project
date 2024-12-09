package com.backend.serializer;

import com.backend.entity.Offer;
import com.backend.entity.Client;
import com.backend.entity.OfferStatus;
import com.backend.entity.OfferType;
import com.backend.entity.StockType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class OfferDeserializer extends JsonDeserializer<Offer> {

    @Override
    public Offer deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);

        // Extract fields
        Integer clientId = node.get("client").asInt();
        String stockTypeStr = node.get("stockType").asText();
        Integer noOfStocks = node.get("noOfStocks").asInt();
        Integer pricePerStock = node.get("pricePerStock").asInt();
        String offerTypeStr = node.get("offerType").asText();

        return Offer.builder()
                .client(Client.builder().id(clientId).build())
                .stockType(StockType.valueOf(stockTypeStr))
                .noOfStocks(noOfStocks)
                .pricePerStock(pricePerStock)
                .offerType(OfferType.valueOf(offerTypeStr))
                .build();
    }
}
