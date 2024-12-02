package com.vasquezhouse.analytics.analytics_api.configuration;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class GraphQLDateTimeCoercing implements Coercing<ZonedDateTime, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
        if (dataFetcherResult instanceof ZonedDateTime) {
            return ((ZonedDateTime) dataFetcherResult).format(FORMATTER);
        }
        throw new CoercingSerializeException("Expected a ZonedDateTime object.");
    }

    @Override
    public ZonedDateTime parseValue(Object input) throws CoercingParseValueException {
        try {
            if (input instanceof StringValue stringValue) {
                return ZonedDateTime.parse(stringValue.getValue(), FORMATTER);
            }
            throw new CoercingParseValueException("Expected a String");
        } catch (Exception e) {
            throw new CoercingParseValueException(String.format("Not a valid date: '%s'.", input), e);
        }
    }

    @Override
    public ZonedDateTime parseLiteral(Object input) throws CoercingParseLiteralException {
        if (input instanceof StringValue stringValue) {
            try {
                return ZonedDateTime.parse(stringValue.getValue(), FORMATTER);
            } catch (Exception e) {
                throw new CoercingParseLiteralException(e);
            }
        } else {
            throw new CoercingParseLiteralException("Expected a String");
        }
    }
}
