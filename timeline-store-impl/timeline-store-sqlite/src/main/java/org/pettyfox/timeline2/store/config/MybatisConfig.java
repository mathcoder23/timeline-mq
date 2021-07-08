package org.pettyfox.timeline2.store.config;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/8
 */
@Configuration
@MapperScan("org.pettyfox.timeline2.store.models.mapper")
public class MybatisConfig {
    private static final Snowflake SNOWFLAKE = new Snowflake(1, 1);

    @Component
    public class CustomIdGenerator implements IdentifierGenerator {
        @Override
        public Long nextId(Object entity) {
            //可以将当前传入的class全类名来作为bizKey,或者提取参数来生成bizKey进行分布式Id调用生成.
            return SNOWFLAKE.nextId();
        }
    }

    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig conf = new GlobalConfig();
        conf.setIdentifierGenerator(new CustomIdGenerator());
        return conf;
    }
}
