package io.skygrid;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class JsonArrayBuilder {
  JsonArray p;

  public JsonArrayBuilder() {
    p = new JsonArray();
  }
  
  public JsonArrayBuilder add(JsonElement v) {
    p.add(v);
    return this;
  }
  
  public JsonArrayBuilder add(String v) {
    p.add(v);
    return this;
  }
  
  public JsonArray gen() {
    return p;
  }
}