package com.glowxq.security.service;

import com.glowxq.security.pojo.ClientVO;

/**
 * @author glowxq
 * @since 2024/2/18 8:42
 * @version 1.0
 */
public interface ClientService {

    ClientVO getClientByClientId(Object id);

}
