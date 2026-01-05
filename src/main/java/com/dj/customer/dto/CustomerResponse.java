package com.dj.customer.dto;

import com.dj.customer.annotation.Mask;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerResponse {
    private Long id;
    private String fullName;
    @Mask(type = Mask.MaskType.EMAIL)
    private String email;
    @Mask(type = Mask.MaskType.MOBILE)
    private String mobile;
}
