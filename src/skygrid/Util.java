package io.skygrid;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import java.util.Map;
import java.util.Set;

public class Util {
  public static JsonElement deepClone(JsonElement ob) {
    JsonParser reader = new JsonParser();
    return reader.parse(ob.toString());
  }
  
  public static Boolean objectEmpty(JsonObject ob) {
    return ob.size() == 0;
  }
  
  //fields is an array of Strings
  public static JsonObject mergeFields(JsonObject target, JsonObject source, JsonArray fields) {
    for(JsonElement fieldName : fields) {
      JsonElement sourceField = source.get(fieldName.getAsString());
      if(!sourceField.isJsonObject()) {
        target.add(fieldName.getAsString(), sourceField);
      } else {
        JsonElement targetFieldElem = target.get(fieldName.getAsString());
        JsonObject targetField = null;
        if(!targetFieldElem.isJsonObject()) {
          targetField = new JsonObject();
        } else {
          targetField = targetFieldElem.getAsJsonObject();
        } 
        for (Map.Entry<String,JsonElement> it : sourceField.getAsJsonObject().entrySet()) {
          targetField.add(it.getKey(), it.getValue());
        }
        
        target.add(fieldName.getAsString(),targetField);
        
      }
    }
    return target;
  }
  
  public static JsonObject prepareChanges(JsonObject changes, JsonObject defaults) {
    JsonObject ret = new JsonObject;
    for (Map.Entry<String,JsonElement> it : changes.entrySet()) {
      if(!it.getKey().equals("acl")) {
        ret.add(it.getKey(),it.getValue());
      } else if(!it.getValue().isJsonNull()) {
        //TODO: check if acl actually needs to be a string?
        ret.add(it.getKey(),it.getValue().toString())
      } else {
        ret.add(it.getKey(),null);
      }
    }
  }
  
  public static JsonObject mergeAcl(JsonObject data, JsonObject changes) {
    if(! changes.has("acl")) {
      data.add("acl",changes,get("acl"));
    } else {
      data.remove("acl");
    }
    return data;
  }
}