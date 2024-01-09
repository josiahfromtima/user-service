package com.tima.platform.converter;

import com.tima.platform.domain.Address;
import com.tima.platform.model.api.response.AddressRecord;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
public class AddressConverter {
    private AddressConverter() {}

    public static synchronized Address mapToEntity(AddressRecord dto) {
        return Address.builder()
                .street(getOrDefault(dto.street(), ""))
                .city(getOrDefault(dto.city(), ""))
                .state(getOrDefault(dto.state(), ""))
                .postCode(getOrDefault(dto.postCode(), ""))
                .country(getOrDefault(dto.country(), "N/A"))
                .language(getOrDefault(dto.language(), "N/A"))
                .build();
    }

    public static synchronized AddressRecord mapToRecord(Address entity) {
        return  AddressRecord.builder()
                .street(entity.getStreet())
                .city(entity.getCity())
                .state(entity.getState())
                .postCode(entity.getPostCode())
                .country(entity.getCountry())
                .language(entity.getLanguage())
                .createdOn(entity.getCreatedOn())
                .build();
    }

    public static synchronized List<AddressRecord> mapToRecords(List<Address> entities) {
        return entities
                .stream()
                .map(AddressConverter::mapToRecord)
                .toList();
    }
    public static synchronized List<Address> mapToEntities(List<AddressRecord> records) {
        return records
                .stream()
                .map(AddressConverter::mapToEntity)
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
