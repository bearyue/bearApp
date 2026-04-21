package com.bear.asset.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bear.asset.entity.SysUser;
import com.bear.asset.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Long count = sysUserMapper.selectCount(new LambdaQueryWrapper<>());
        if (count == 0) {
            SysUser admin = new SysUser();
            admin.setUsername("bear");
            admin.setPasswordHash(passwordEncoder.encode("bear123"));
            admin.setNickname("Bear");
            admin.setRole("ADMIN");
            admin.setEnabled(true);
            sysUserMapper.insert(admin);
            log.info("========================================");
            log.info("初始管理员账户已创建");
            log.info("用户名: bear");
            log.info("密码: bear123");
            log.info("请登录后立即修改密码!");
            log.info("========================================");
        }
    }
}
