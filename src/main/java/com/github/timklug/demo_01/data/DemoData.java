package com.github.timklug.demo_01.data;

import org.javamoney.moneta.Money;

public class DemoData {

  private String demo;
  private Money amount;

  public String getDemo() {
    return demo;
  }

  public void setDemo(String demo) {
    this.demo = demo;
  }

  public Money getAmount() {
    return amount;
  }

  public void setAmount(Money amount) {
    this.amount = amount;
  }
}
