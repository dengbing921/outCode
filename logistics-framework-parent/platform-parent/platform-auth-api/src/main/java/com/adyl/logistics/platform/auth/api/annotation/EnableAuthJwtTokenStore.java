package com.adyl.logistics.platform.auth.api.annotation;

import com.adyl.logistics.platform.auth.api.store.AuthJwtTokenStore;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在应用启动类上添加JWT存储令牌
 *
 * @author Dengb
 * @date 20180911
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(AuthJwtTokenStore.class)
public @interface EnableAuthJwtTokenStore {
}
