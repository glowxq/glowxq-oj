package com.glowxq.system.admin.service;

import com.glowxq.core.common.entity.CheckPuzzle;
import com.glowxq.core.common.entity.SliderPuzzle;
import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * CaptchaService
 * 
 * @author glowxq
 * @since 2025/1/8 17:01
 * @version 1.0
 */
public interface CaptchaService {

    SliderPuzzle getImageCode(HttpServletRequest request);

    void checkImageCode(CheckPuzzle checkPuzzle) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException;
}
