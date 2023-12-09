package com.tima.platform.domain;

import com.tima.platform.util.AppUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/8/23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("public.custom_refresh_token")
public class CustomRefreshToken implements Serializable, Persistable<String> {

    @Id
    private String id;
    private String encodedId;
    private Instant expiresOn;
    @Transient
    private String generatedId;

    @Override
    public boolean isNew() {
        boolean newRecord = AppUtil.isNewRecord(id);
        if(newRecord) {
            id = generatedId;
        }
        return newRecord;
    }
}
