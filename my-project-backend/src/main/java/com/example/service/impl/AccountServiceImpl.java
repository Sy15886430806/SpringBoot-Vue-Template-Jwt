package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.mapper.AccountMapper;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Resource
    FlowUtils flowUtils;

    @Resource
    AmqpTemplate amqpTemplate;

    @Resource(name = "stringRedisTemplate")
    StringRedisTemplate redisTemplate;

    /**
     * 根据用户名或邮箱加载用户详细信息
     *
     * @param username 用户名或邮箱
     * @return UserDetails 用户详细信息
     * @throws UsernameNotFoundException 如果找不到用户
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名或邮箱查找账户信息
        Account account = this.findAccountByNameOrEmail(username);
        // 如果账户信息为空，抛出用户名或密码错误的异常
        if (account == null)
            throw new UsernameNotFoundException("用户名或密码错误");
        // 返回用户详细信息，包括用户名、密码和角色
        return User
                .withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    /**
     * 根据用户名或邮箱查找账户信息
     *
     * @param text 用户名或邮箱
     * @return Account 账户信息
     */
    @Override
    public Account findAccountByNameOrEmail(String text) {
        // 构建查询条件，根据用户名或邮箱进行查询
        return this.query()
                .eq("username", text).or()
                .eq("email", text)
                .one();
    }

    @Override
    public String registerEmailVerifyCode(String type, String email, String ip) {
        // 使用ip的intern方法获取字符串常量池中的引用，确保同步块使用的是同一个对象
        synchronized (ip.intern()) {
            // 检查IP的请求频率是否超过限制
            if (!this.verifyLimit(ip)) return "请求频繁，请稍后再试";

            // 创建一个随机数生成器
            Random random = new Random();
            // 生成一个6位数的随机验证码
            int code = random.nextInt(899999) + 100000;
            // 创建一个包含验证码信息的Map
            Map<String, Object> data = Map.of("type", type, "email", email, "code", code);
            // 使用AMQP模板发送验证码信息到消息队列
            amqpTemplate.convertAndSend("mail", data);
            // 将验证码存储到Redis中，并设置过期时间为3分钟
            redisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
            // 返回null表示操作成功
            return null;
        }
    }

    // 检查IP的请求频率是否超过限制
    private boolean verifyLimit(String ip) {
        // 构造Redis中的键名
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        // 使用流量控制工具检查请求频率，限制为每分钟一次
        return flowUtils.limitOnceCheck(key, 60);
    }

}
