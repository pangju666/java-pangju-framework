package io.github.pangju666.framework.core.jackson.databind.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.pangju666.commons.lang.utils.DateUtils;

import java.io.IOException;
import java.util.Date;

public class DateJsonSerializer extends JsonSerializer<Date> {
	@Override
	public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeNumber(DateUtils.getTime(value));
	}
}
