package com.tima.platform.model.api.request;

import com.tima.platform.model.api.response.AddressRecord;
import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
@Builder
public record AddressRequestRecord(String publicId, String pictureName, String documentName, AddressRecord addressRecord)
{}
