package com.example.main;

import io.skygrid.*;
import com.google.gson.*;

import java.text.ParseException;
import java.util.Date;

public class App {
  public static void main(String args[]) {
    try {
      Project p = new Project("RHlD5jC0", "http://localhost:3080");
      
    } catch (Throwable e) {
      System.err.println("Error!");
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }
}