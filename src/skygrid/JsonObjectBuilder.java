package io.skygrid;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

public class JsonObjectBuilder {
  JsonObject p;

  public JsonObjectBuilder() {
    p = new JsonObject();
  }
  
  public JsonObjectBuilder add(String k, JsonElement v) {
    p.add(k,v);
    return this;
  }
  
  public JsonObjectBuilder add(String k, String v) {
    p.addProperty(k,v);
    return this;
  }
  
  public JsonObjectBuilder add(String k, Boolean v) {
    p.addProperty(k,v);
    return this;
  }

  public JsonObject generate() {
    return p;
  }
  
  public JsonObject gen() {
    return p;
  }
}