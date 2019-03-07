package com.github.timklug.demo_01.processor;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.timklug.demo_01.data.CustomJsonDeserializer;
import com.github.timklug.demo_01.data.CustomJsonSerializer;
import com.github.timklug.demo_01.data.DemoData;
import java.util.Map;
import javax.money.Monetary;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.javamoney.moneta.Money;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoProcessorTest {

  private static final String TOPIC_DEMO_DATA = "Demo-Data";
  private static final String TOPIC_DEMO_DATA_TABLE = "Demo-Data-Table";
  private static final String TOPIC_DEMO_RESULT = "Demo-Result";
  private static final String GROUP_NAME = "demo";

  @Autowired
  private ObjectMapper mapper;

  @ClassRule
  public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, TOPIC_DEMO_RESULT);

  @BeforeClass
  public static void setup() {
    System.setProperty("spring.cloud.stream.kafka.streams.binder.brokers", embeddedKafka.getBrokersAsString());
  }

  @Test
  public void test() throws Exception {


    var demoData = new DemoData();
    demoData.setDemo("some Name");
    demoData.setAmount(Money.of(10, Monetary.getCurrency("EUR")));

    Map<String, Object> senderPropsTable = KafkaTestUtils.producerProps(embeddedKafka);
    senderPropsTable.put("key.serializer", LongSerializer.class);
    senderPropsTable.put("value.serializer", JsonSerializer.class);
    var pf2 = new DefaultKafkaProducerFactory<Long, DemoData>(senderPropsTable, new LongSerializer(),
        new CustomJsonSerializer<DemoData>(mapper));
    var template2 = new KafkaTemplate<>(pf2, true);
    template2.setDefaultTopic(TOPIC_DEMO_DATA_TABLE);
    template2.sendDefault(0L, demoData);

    Thread.sleep(2_000);

    Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafka);
    senderProps.put("key.serializer", LongSerializer.class);
    senderProps.put("value.serializer", JsonSerializer.class);
    var pf = new DefaultKafkaProducerFactory<Long, Integer>(senderProps, new LongSerializer(),
        new JsonSerializer<Integer>());
    var template = new KafkaTemplate<>(pf, true);
    template.setDefaultTopic(TOPIC_DEMO_DATA);
    template.sendDefault(0L, 1);

    Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(GROUP_NAME, "false", embeddedKafka);
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    consumerProps.put("key.deserializer", LongDeserializer.class);
    consumerProps.put("value.deserializer", JsonDeserializer.class);
    var cf = new DefaultKafkaConsumerFactory<Long, DemoData>(consumerProps, new LongDeserializer(),
        new CustomJsonDeserializer<DemoData>(mapper, DemoData.class));

    var consumer = cf.createConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(consumer, TOPIC_DEMO_RESULT);
    consumer.assignment();
    var result = KafkaTestUtils.getRecords(consumer, 10_000);
    consumer.commitSync();

    assertThat(result).isNotNull();
    assertThat(result.count()).isEqualTo(1);
  }
}