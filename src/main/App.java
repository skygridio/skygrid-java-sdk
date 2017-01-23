package com.example.main;

import io.skygrid.*;
import com.google.gson.*;

import java.text.ParseException;
import java.util.Date;

public class App {
  public static void main(String args[]) {
    Project p = new Project("RHlD5jC0", "http://localhost:3080");
    try {
      JsonObject o = new JsonObjectBuilder().add("add","asss").gen();
      JsonObject c = Util.deepClone(o).getAsJsonObject();
      System.out.println(c.toString());
      System.out.println(o.toString());
    } catch (Throwable e) {
      System.err.println("Error!");
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }
}