package com.paypal.api_gateway.filters;

import com.paypal.api_gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    // public path can be accessed without any authentication/authorization
    public static final List<String> PUBLIC_PATHS = List.of(
            "/auth/signup",
            "/auth/login"
    );


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // first we are extracting the path
        String path = exchange.getRequest().getURI().getPath();

        // cleaning of the path that we got
        String normalizedPath = path.replaceAll("/+$","");

        // if any route is on public path it will automatically route without authorization
        if(PUBLIC_PATHS.contains(normalizedPath)) {
            return chain.filter(exchange)
                    .doOnSubscribe(s -> System.out.println("Proceeding without check"))
                    .doOnSuccess(v -> System.out.println("successfully passed"))
                    .doOnError(e -> System.err.println("error occured"));

        }

        // but if its not on public path then we are checking the authorization here
        // First we are extracting Autharization header from the request

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

       // if auth header doesnot starts with bearer then we simply return its unauthorized
        if(!authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }


        // if header starts with bearer then we are proceeding
        try{
            // now extracting the  token from header
            String token = authHeader.substring(7);
            Claims claims = JwtUtil.validateToken(token);

            // we are checking if the token is valid then if its validated then
            exchange.getRequest().mutate()
                    .header("X-User-Email", claims.getSubject())
                    .build();

            // we allow routing to the desired location
            return chain.filter(exchange)
                    .doOnSubscribe(s -> System.out.println("Proceeding without check"))
                    .doOnSuccess(v -> System.out.println("successfully passed"))
                    .doOnError(e -> System.err.println("error occured"));
        }
        catch(Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

    }

    @Override
    public int getOrder() {
        return 0;
    }

}
