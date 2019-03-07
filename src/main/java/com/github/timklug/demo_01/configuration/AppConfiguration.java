package com.github.timklug.demo_01.configuration;

import com.fasterxml.jackson.databind.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.jackson.datatype.money.MoneyModule;

@Configuration
public class AppConfiguration {

  @Bean
  public Module moneyModule() {
    return new MoneyModule();
  }
}
