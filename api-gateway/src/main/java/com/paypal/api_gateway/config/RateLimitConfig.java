package com.paypal.api_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimitConfig {

    @Bean
    public KeyResolver userKeyResolver(){
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            if (userId != null){
                System.out.println("Rate limiting by userId: " + userId);
                return Mono.just(userId);
            }
            // fallback via ip address
            // fallback via ip address
            String ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            System.out.println("Rate limiting by IP: " + ip);
            return Mono.just(ip);
        };

    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10, 20, 1);
    }

}