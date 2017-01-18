package com.example.main;

import io.skygrid.RestApi;
import io.skygrid.Api;
import io.skygrid.Project;

import java.text.ParseException;
import java.util.Date;

public class App {
  public static void main(String args[]) {
    Project p = new Project("RHlD5jC0", "http://localhost:3080");
    try {
      p.loginMaster("tMX1b94v+Qmr8/r5RH66Bkjk");
      System.out.println(p.signup("j@mailinator.com", "12345"));
    } catch (Throwable e) {
      System.err.println("Error!");
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }
}