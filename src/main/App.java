package com.example.main;

import io.skygrid.*;
import com.google.gson.*;

import java.text.ParseException;
import java.util.Date;

public class App {
  public static void main(String args[]) {
    try {
      Project p = new Project("0G7AQCOb");
      p.fetch();
      System.out.println(p.schemas().size());
      for (Schema s : p.schemas()) {
        s.fetchIfNeeded();
        System.out.println(s.isComplete());
        System.out.println(s.id());
        System.out.println(s.name());
      }
      // p.loginMaster("ZVjceXBRgBd9a7HS249ztfuy");
      // p.addSchema(
      //   "test",
      //   new JsonObjectBuilder()
      //     .add(
      //       "prop1",
      //       new JsonObjectBuilder()
      //         .add("type","number")
      //         .gen()
      //     )
      //     .gen()
      // );
      // for (Schema s : p.schemas()) {
      //   System.out.println(s);
      // }
    } catch (Throwable e) {
      System.err.println("Error!");
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }
}
