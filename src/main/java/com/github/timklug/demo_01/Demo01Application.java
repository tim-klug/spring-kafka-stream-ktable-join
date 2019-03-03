package com.github.timklug.demo_01;

import com.github.timklug.demo_01.processor.StreamBindings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(StreamBindings.class)
@SpringBootApplication
public class Demo01Application {

  public static void main(String[] args) {
    SpringApplication.run(Demo01Application.class, args);
  }

}
