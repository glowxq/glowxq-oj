package com.glowxq.core.common.valid.validator;

import com.glowxq.core.common.valid.annotation.ValidPhone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author glowxq
 * @version 1.0
 * @date 2025/3/18
 */
public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {
    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        return phone != null && phone.matches("^1[3-9]\\d{9}$");
    }
}