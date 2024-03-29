package com.mnus.gateway.filter;

import cn.hutool.http.HttpStatus;
import com.mnus.gateway.utils.JwtUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * 登录过滤器
 *
 * @author: <a href="https://github.com/xkayah">xkayah</a>
 * @date: 2024/3/10 16:40:16
 */
@Component
public class LoginUserGlobalFilter implements GlobalFilter, Ordered {
    @Resource
    private JwtUtil jwtUtil;
    private static final Logger LOG = LoggerFactory.getLogger(LoginUserGlobalFilter.class);
    public static final String AUTH_HEADER = "Authorization";
    public static final String AUTH_SCHEMA = "Bearer ";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = request.getHeaders();
        String path = request.getPath().pathWithinApplication().value();

        // 排除拦截的路径；测试地址，管理员地址，登录、验证码地址
        if (isPublic(path)) {
            LOG.info("{} do without auth", path);
            return chain.filter(exchange);
        } else {
            LOG.info("{} do auth", path);
        }
        // 验签
        String token = getToken(headers);
        if (token == null || !jwtUtil.validate(token)) {
            response.setStatusCode(HttpStatusCode.valueOf(HttpStatus.HTTP_UNAUTHORIZED));
            return response.setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 200;
    }

    private String getToken(HttpHeaders headers) {
        String authCL = headers.getFirst(AUTH_HEADER);
        return Optional.ofNullable(authCL)
                .filter(a -> a.startsWith(AUTH_SCHEMA))
                .map(a -> a.substring(AUTH_SCHEMA.length()))
                .orElse(null);
    }

    private boolean isPublic(String path) {
        return path.contains("/hello") ||
                path.contains("/admin") ||
                path.contains("/ucenter/user/sign-in") ||
                path.contains("/ucenter/user/send-code");
    }
}
