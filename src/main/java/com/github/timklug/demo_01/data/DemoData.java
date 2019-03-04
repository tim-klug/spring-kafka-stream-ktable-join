package com.github.timklug.demo_01.data;

import javax.money.MonetaryAmount;
import org.javamoney.moneta.Money;

public class DemoData {

  private String demo;
  private MonetaryAmount amount;

  public String getDemo() {
    return demo;
  }

  public void setDemo(String demo) {
    this.demo = demo;
  }

  public MonetaryAmount getAmount() {
    return amount;
  }

  public void setAmount(MonetaryAmount amount) {
    this.amount = amount;
  }
}
