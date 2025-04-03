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

package io.github.pangju666.framework.core.jackson.enums;

import io.github.pangju666.commons.lang.utils.DesensitizationUtils;
import org.springframework.core.convert.converter.Converter;

public enum DesensitizedType {
	CHINESE_NAME(DesensitizationUtils::desensitizeChineseName),
	ID_CARD(DesensitizationUtils::desensitizeIdCard),
	TEL_PHONE(DesensitizationUtils::desensitizeTelPhone),
	PHONE_NUMBER(DesensitizationUtils::desensitizePhoneNumber),
	ADDRESS(DesensitizationUtils::desensitizeAddress),
	EMAIL(DesensitizationUtils::desensitizeEmail),
	PASSWORD(DesensitizationUtils::desensitizePassword),
	PLATE_NUMBER(DesensitizationUtils::desensitizePlateNumber),
	VEHICLE_ENGINE_NUMBER(DesensitizationUtils::desensitizeVehicleEngineNumber),
	VEHICLE_FRAME_NUMBER(DesensitizationUtils::desensitizeVehicleFrameNumber),
	NICK_NAME(DesensitizationUtils::desensitizeNickName),
	BANK_CARD(DesensitizationUtils::desensitizeBankCard),
	CUSTOM(value -> value);

	private final Converter<String, String> converter;

	DesensitizedType(Converter<String, String> converter) {
		this.converter = converter;
	}

	public Converter<String, String> getConverter() {
		return converter;
	}
}