package kr.co.seoulit.hisback.surgery.global.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * boolean ↔ CHAR(1) 'Y'/'N' 변환기
 * <p>개발표준가이드 §14.2: _yn 컬럼은 CHAR(1)에 'Y'/'N'만 저장한다.
 * Hibernate 기본 boolean 매핑(NUMBER(1) 0/1)을 쓰지 않고 이 컨버터를 명시적으로 붙여야 한다.</p>
 */
@Converter
public class YnConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        return Boolean.TRUE.equals(attribute) ? "Y" : "N";
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        return "Y".equals(dbData);
    }
}
