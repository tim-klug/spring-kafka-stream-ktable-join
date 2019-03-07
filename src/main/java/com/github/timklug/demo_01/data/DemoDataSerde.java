package com.github.timklug.demo_01.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.zalando.jackson.datatype.money.MoneyModule;

public class DemoDataSerde extends JsonSerde<DemoData> {

  public DemoDataSerde() {
    super(new ObjectMapper().registerModule(new MoneyModule()));
  }
}
