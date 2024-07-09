package org.uts.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;


/**
 * 开启快速失败（Fali Fast）：
 * 默认情况下在对参数进行校验时Spring Validation会校验完所有字段然后才抛出异常，
 * 可以通过配置开启 Fali Fast模式，一旦校验失败就立即返回。
 * **/
@Configuration
public class ValidatedConfig {
    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                // 快速失败模式
                .failFast(true)
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }
}
