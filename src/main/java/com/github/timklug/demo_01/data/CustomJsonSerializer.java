package com.github.timklug.demo_01.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

public class CustomJsonSerializer<T> implements Serializer<T> {

  private ObjectMapper objectMapper;

  public CustomJsonSerializer(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void configure(Map map, boolean b) {

  }

  @Override
  public byte[] serialize(String s, T data) {
    if (data == null) {
      return null;
    }

    try {
      return objectMapper.writeValueAsBytes(data);
    } catch (JsonProcessingException | RuntimeException e) {
      throw new SerializationException("Error serializing to JSON with Jackson", e);
    }
  }

  @Override
  public void close() {
  }
}
