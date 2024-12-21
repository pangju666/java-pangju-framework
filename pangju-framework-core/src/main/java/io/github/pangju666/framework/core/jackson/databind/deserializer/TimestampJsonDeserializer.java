package io.github.pangju666.framework.core.jackson.databind.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.github.pangju666.commons.lang.utils.DateUtils;

import java.io.IOException;
import java.util.Date;

public class TimestampJsonDeserializer extends JsonDeserializer<Date> {
	@Override
	public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		long timestamp = p.getLongValue();
		return DateUtils.toDate(timestamp);
	}
}