package com.adyl.logistics.platform.auth.client;

/**
 * 自定义客户端查询实现
 *
 * @author Dengb
 * @date 20180910
 */
public interface IClientDetailsDao {
    CustomClientDetails getClientByClientId(String clientId);
}
