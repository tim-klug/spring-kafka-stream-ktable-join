package com.github.timklug.demo_01.processor;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.timklug.demo_01.data.DemoData;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
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

  @ClassRule
  public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, TOPIC_DEMO_RESULT, TOPIC_DEMO_DATA_TABLE, TOPIC_DEMO_DATA);

  @BeforeClass
  public static void setup() {
    System.setProperty("spring.cloud.stream.kafka.streams.binder.brokers", embeddedKafka.getBrokersAsString());
  }

  @Test
  public void test() throws InterruptedException {

    Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafka);
    senderProps.put("key.serializer", LongSerializer.class);
    senderProps.put("value.serializer", StringSerializer.class);
    var pf = new DefaultKafkaProducerFactory<Long, String>(senderProps);
    var template = new KafkaTemplate<>(pf, true);
    template.setDefaultTopic(TOPIC_DEMO_DATA);
    template.sendDefault(0L, "Test");

    var demoData = new DemoData();
    demoData.setDemo("some Name");

    Map<String, Object> senderPropsTable = KafkaTestUtils.producerProps(embeddedKafka);
    senderPropsTable.put("key.serializer", LongSerializer.class);
    senderPropsTable.put("value.serializer", JsonSerializer.class);
    var pf2 = new DefaultKafkaProducerFactory<Long, DemoData>(senderPropsTable, new LongSerializer(), new JsonSerializer<DemoData>());
    var template2 = new KafkaTemplate<>(pf2, true);
    template2.setDefaultTopic(TOPIC_DEMO_DATA_TABLE);
    template2.sendDefault(0L, demoData);

    Thread.sleep(2_000);


    Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(GROUP_NAME, "false", embeddedKafka);
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    consumerProps.put("key.deserializer", LongDeserializer.class);
    consumerProps.put("value.deserializer", JsonDeserializer.class);
    var cf = new DefaultKafkaConsumerFactory<Long, DemoData>(consumerProps, new LongDeserializer(), new JsonDeserializer<DemoData>());

    var consumer = cf.createConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(consumer, TOPIC_DEMO_RESULT);
    consumer.assignment();
    var result = KafkaTestUtils.getRecords(consumer, 10_000);
    consumer.commitSync();

    assertThat(result).isNotNull();
    assertThat(result.count()).isEqualTo(1);
  }
}