package com.dj.customer.dto;

import com.dj.customer.annotation.Mask;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Invalid email")
    @Mask(type = Mask.MaskType.EMAIL)
    private String email;

    @NotBlank(message = "Mobile is required")
    @Mask(type = Mask.MaskType.MOBILE)
    private String mobile;
}
