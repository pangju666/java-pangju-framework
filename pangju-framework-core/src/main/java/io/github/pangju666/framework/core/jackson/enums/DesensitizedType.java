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
