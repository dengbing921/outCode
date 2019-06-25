package com.adyl.logistics.platform.auth.api.annotation;

import com.adyl.logistics.platform.auth.api.store.ResJwtTokenStore;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在资源服务器应用上注解开启JWT存储令牌
 *
 * @author Dengb
 * @date 20180911
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ResJwtTokenStore.class)
public @interface EnableResJwtTokenStore {
}
