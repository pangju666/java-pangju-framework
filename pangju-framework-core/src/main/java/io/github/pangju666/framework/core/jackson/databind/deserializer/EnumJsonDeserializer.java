package io.github.pangju666.framework.core.jackson.databind.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import org.apache.commons.lang3.EnumUtils;

import java.io.IOException;
import java.util.Objects;

public class EnumJsonDeserializer extends JsonDeserializer<Enum> implements ContextualDeserializer {
	private final Class<? extends Enum> enumClass;

	public EnumJsonDeserializer() {
		this.enumClass = null;
	}

	public EnumJsonDeserializer(Class<? extends Enum> enumClass) {
		this.enumClass = enumClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enum deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		if (Objects.isNull(enumClass)) {
			throw new UnsupportedOperationException();
		}
		return EnumUtils.getEnumIgnoreCase(this.enumClass, p.getText());
	}

	@SuppressWarnings("unchecked")
	@Override
	public JsonDeserializer<Enum> createContextual(DeserializationContext ctxt, BeanProperty property) {
		JavaType type = Objects.nonNull(ctxt.getContextualType()) ? ctxt.getContextualType() : property.getMember().getType();
		return new EnumJsonDeserializer((Class<? extends Enum>) type.getRawClass());
	}
}
