package io.skygrid;

import com.google.gson.JsonElement;

public interface Api {
  //TODO a non blocking request func
  /**
   * a blocking function to make requests
   */
  public JsonElement requestSync(String name, JsonElement data);
  /**
   * overloaded function
   */
  public JsonElement requestSync(String name);
  /**
   * mostly for a socket api
   */
  public void close();

  /**
   * function to check if master key is added to the api
   */
  public Boolean hasMasterkey();
}
