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
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/7/23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("public.verification")
public class Verification implements Serializable, Persistable<Integer> {

    @Id
    private Integer id;
    private String userOtp;
    private Integer userId;
    private String type;
    private Instant createdOn;
    @Transient
    private long durationInHours;

    @Override
    public boolean isNew() {
        boolean newRecord = AppUtil.isNewRecord(id);
        if(newRecord) {
            createdOn = Instant.now().plus(durationInHours, ChronoUnit.HOURS);
        }
        return newRecord;
    }
}
