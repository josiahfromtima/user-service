package com.tima.platform.converter;

import com.google.gson.reflect.TypeToken;
import com.tima.platform.domain.Country;
import com.tima.platform.model.api.response.CountryRecord;
import com.tima.platform.util.AppUtil;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
public class CountryConverter {
    private CountryConverter() {}

    public static synchronized Country mapToEntity(CountryRecord dto) {
        return Country.builder()
                .name(dto.name())
                .currency(dto.currency())
                .language(AppUtil.gsonInstance().toJson(dto.language()))
                .build();
    }

    public static synchronized CountryRecord mapToRecord(Country entity) {
        return  CountryRecord.builder()
                .name(entity.getName())
                .currency(entity.getCurrency())
                .language(AppUtil.gsonInstance().fromJson(entity.getLanguage(),
                        new TypeToken<List<String>>(){}.getType()))
                .build();
    }

    public static synchronized List<CountryRecord> mapToRecords(List<Country> entities) {
        return entities
                .stream()
                .map(CountryConverter::mapToRecord)
                .toList();
    }
    public static synchronized List<Country> mapToEntities(List<CountryRecord> records) {
        return records
                .stream()
                .map(CountryConverter::mapToEntity)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getOrDefault(Object value, T t){
        if( Objects.isNull(value) ) {
            return t;
        }
        return (T) value;
    }
}
