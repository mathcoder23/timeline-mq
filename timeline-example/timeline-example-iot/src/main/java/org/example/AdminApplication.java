package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * @author Petty Fox
 * @version 1.0
 * @date 2021/7/1
 */
@SpringBootApplication
@Component
@ComponentScan({"org.example"
        , "org.pettyfox.timeline2.store"})
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
