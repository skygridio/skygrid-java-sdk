package io.skygrid;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

/**
 * A builder class to create JsonObjects <br>
 *
 * <pre>
 * Usage :
 * JsonElement e = new JsonObject();
 * JsonObject o = new JsonObjectBuilder()
 *                .add("key","value")
 *                .add("num",1)
 *                .add("element",e)
 *                .gen()
 * </pre>
 *
 */
public class JsonObjectBuilder {
  JsonObject p;

  /**
   * A class to easily build Json Objects
   */
  public JsonObjectBuilder() {
    p = new JsonObject();
  }

  /**
   * Adds a key - value pair to the object
   * @param k key value for the element
   * @param v value of the pair
   * @return the same builder to continue building on the same object
   */
  public JsonObjectBuilder add(String k, JsonElement v) {
    p.add(k,v);
    return this;
  }

  /**
   * Overloaded function
   */
  public JsonObjectBuilder add(String k, String v) {
    p.addProperty(k,v);
    return this;
  }

  /**
   * Overloaded function
   */
  public JsonObjectBuilder add(String k, Boolean v) {
    p.addProperty(k,v);
    return this;
  }

  /**
   * Overloaded function
   */
  public JsonObjectBuilder add(String k, Integer v) {
    p.addProperty(k,v);
    return this;
  }

  /**
   * returns the created JsonObject
   */
  public JsonObject gen() {
    return p;
  }
}
