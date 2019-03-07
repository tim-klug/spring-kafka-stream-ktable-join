package com.github.timklug.demo_01.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class CustomJsonDeserializer<T> implements Deserializer<T> {

  private ObjectMapper objectMapper;
  private Class<T> clazz;

  public CustomJsonDeserializer(ObjectMapper objectMapper, Class<T> clazz) {
    this.objectMapper = objectMapper;
    this.clazz = clazz;
  }

  @Override
  public void configure(Map<String, ?> map, boolean b) {
  }

  @Override
  public T deserialize(String s, byte[] bytes) {
    try {
      return objectMapper.readValue(bytes, clazz);
    } catch (IOException | RuntimeException e) {
      throw new SerializationException("Error deserializing from JSON with Jackson", e);
    }
  }

  @Override
  public void close() {
  }
}
