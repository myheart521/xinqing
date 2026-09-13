package com.hpu.xinqing.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI springOpenAPI() {
        // 访问路径：http://localhost:1314/swagger-ui/index.html 可以打开UI界面.
        return new OpenAPI().info(new Info()
                .title("我是毒液我是毒液我是最强毒液")
                .description("真帅man")
                .version("1.0.0")
                .contact(new Contact()
                        .name("New_Boy")
                        .url("https://你的个人网站.com")
                        .email(""))
        );
    }
}