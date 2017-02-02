package io.skygrid;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class JsonArrayBuilder {
  JsonArray p;

  /**
   * a class to easily build JSON arrays
   */
  public JsonArrayBuilder() {
    p = new JsonArray();
  }

  /**
   * adds an element to the JsonArray
   */
  public JsonArrayBuilder add(JsonElement v) {
    p.add(v);
    return this;
  }

  /**
   * overloaded function
   */
  public JsonArrayBuilder add(String v) {
    p.add(v);
    return this;
  }

  /**
   * generates the created array
   */
  public JsonArray gen() {
    return p;
  }
}
