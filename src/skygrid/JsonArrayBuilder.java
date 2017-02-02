package io.skygrid;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * A Builder class to build JsonArrays
 *
 * <pre>
 * Usage
 * JsonElement = new JsonPrimitive(1);
 * JsonArray a = new JsonArrayBuilder()
 *               .add("something")
 *               .add(element)
 *               .gen();
 * </pre>
 */
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

  //NOTE overloaded functions for Number and Boolean was not added as that was not required in this package

  /**
   * generates the created array
   */
  public JsonArray gen() {
    return p;
  }
}
