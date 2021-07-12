package org.example.config;

import com.codahale.metrics.MetricRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/8
 */
@Configuration
public class MetersConfig {
    @Bean
    public MetricRegistry metrics() {
        return new MetricRegistry();
    }
}
