package com.github.timklug.demo_01;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Demo01ApplicationTests {

  @ClassRule
  public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true);

  @BeforeClass
  public static void setup() {
    System.setProperty("spring.cloud.stream.kafka.streams.binder.brokers", embeddedKafka.getBrokersAsString());
  }

  @Test
  public void contextLoads() {
  }

}
