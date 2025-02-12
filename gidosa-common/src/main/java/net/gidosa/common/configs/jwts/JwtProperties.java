package net.gidosa.common.configs.jwts;

import java.time.Duration;

public interface JwtProperties {
    String AUTHORITIES_KEY = "auth"; // 우리 서버만 알고 있는 비밀값
    //    int EXPIRATION_TIME = 864000000; // 10일 (1/1000초)
    long ACCESS_TOKEN_EXPIRE_TIME = Duration.ofDays(1).toMillis();
    String AUTHORIZATION_HEADER = "Authorization";
    String BEARER_PREFIX = "Bearer ";
    String SECURITY_NAME = "JWT";
}
