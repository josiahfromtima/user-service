package com.tima.platform.domain;

import com.tima.platform.util.AppUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("public.user")
public class User implements Serializable,Persistable<Integer> {

        @Id
        private Integer id;
        private String username;
        private String password;
        private String publicId;
        private boolean enabled;
        private Instant createdOn;

        @Override
        public boolean isNew() {
            boolean newRecord = AppUtil.isNewRecord(id);
            if(newRecord) {
                createdOn = Instant.now();
                publicId = UUID.randomUUID().toString();
            }
            return newRecord;
        }

}
