package com.github.timklug.demo_01.processor;

import com.github.timklug.demo_01.data.DemoData;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class DemoProcessor {

  private static final String DEMO_DATA = "demoData";
  private static final String DEMO_DATA_TABLE = "demoDataTable";
  private static final String DEMO_RESULT = "demoResult";

  @StreamListener
  @SendTo({DEMO_RESULT})
  public KStream<Long, DemoData> process(
      @Input(DEMO_DATA) KStream<Long, Integer> data,
      @Input(DEMO_DATA_TABLE) KTable<Long, DemoData> dataTable
  ) {
    return data
        .leftJoin(dataTable, (name, demoData) -> demoData)
        .peek((k, d) -> System.out.println(d));
  }

  public interface DemoKStreamProcessor {

    @Input(DEMO_DATA)
    KStream<Long, Integer> data();

    @Input(DEMO_DATA_TABLE)
    KTable<Long, DemoData> dataTable();

    @Output(DEMO_RESULT)
    KStream<Long, DemoData> demoResult();
  }
}
