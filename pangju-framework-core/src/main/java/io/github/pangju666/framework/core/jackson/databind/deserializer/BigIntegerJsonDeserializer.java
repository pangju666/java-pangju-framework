/*
 *   Copyright 2025 pangju666
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.pangju666.framework.core.jackson.databind.deserializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigInteger;

public class BigIntegerJsonDeserializer extends JsonDeserializer<BigInteger> {
	@Override
	public BigInteger deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		try {
			return BigInteger.valueOf(p.getLongValue());
		} catch (JsonParseException e) {
			try {
				return new BigInteger(p.getText());
			} catch (JsonParseException | NumberFormatException ex) {
				return null;
			}
		}
	}
}
