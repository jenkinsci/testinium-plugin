package com.testinium.jenkinsplugin.service.deserializer;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import javax.xml.bind.DatatypeConverter;
import java.lang.reflect.Type;
import java.util.Date;

public class DateDeserializer implements JsonDeserializer<Date> {

    private static final String ALL_DIGIT_REGEX = "^[0-9]+$";

    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        String dateString = jsonElement.getAsJsonPrimitive().getAsString();
        if (dateString.matches(ALL_DIGIT_REGEX)) {
            return new Date(jsonElement.getAsJsonPrimitive().getAsLong());
        } else {
            return DatatypeConverter.parseDateTime(dateString).getTime();
        }


    }
}
