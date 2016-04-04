package com.example.geojsonsimplifier;

public class ResponseBean {

  public Object message;

  public ResponseBean() {};

  public ResponseBean(Object message) {
    this.message = message;
  }

  public Object getMessage() {
    return message;
  }

  public void setMessage(Object message) {
    this.message = message;
  }
}
