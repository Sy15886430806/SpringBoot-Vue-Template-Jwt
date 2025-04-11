package com.example.filter;

import com.example.entity.RestBean;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Order(Const.ORDER_LIMIT)
public class FlowLimitFilter extends HttpFilter {

    @Resource
    StringRedisTemplate template;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String address = request.getRemoteAddr();
        if (this.tryCount(address)) {
            chain.doFilter(request, response);
        } else {
            this.writeBlockMessage(response);
        }

    }

    private void writeBlockMessage(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.forbidden("操作频繁，请稍后再试").asJsonString());
    }

    private boolean tryCount(String ip) {
        synchronized (ip.intern()) {
            if (template.hasKey(Const.FLOW_LIMIT_BLOCK + ip)) return false;
            return this.limitPeriodCheck(ip);
        }

    }

    private boolean limitPeriodCheck(String ip) {
        if (template.hasKey(Const.FLOW_LIMIT_COUNT + ip)) {
            Long increment = Optional.ofNullable(template.opsForValue().increment(Const.FLOW_LIMIT_COUNT + ip)).orElse(0L);
            if (increment > 10) {
                template.opsForValue().set(Const.FLOW_LIMIT_BLOCK + ip, "", 30, TimeUnit.SECONDS);
                return false;
            }
        } else {
            template.opsForValue().set(Const.FLOW_LIMIT_COUNT + ip, "1", 3, TimeUnit.SECONDS);
        }
        return true;
    }
}
