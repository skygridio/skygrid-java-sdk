package io.skygrid;

import io.skygrid.SubscriptionManager;
import io.skygrid.SkygridObject;
import io.skygrid.SkygridError;
import io.skygrid.Acl;
import io.skygrid.Api;
import io.skygrid.Util;
import io.skygrid.Schema;
import io.skygrid.JsonObjectBuilder;
import io.skygrid.JsonArrayBuilder;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonElement;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.Map;
import java.util.HashMap;

public class Device extends SkygridObject {
  SubscriptionManager _subManager;

  /**
   * @param api Api to use for this Device
   * @param manager Subscription Manager to be used for this Devcice
   * @param deviceId String representing the id of the device
   */
  public Device(Api api, SubscriptionManager manager, String deviceId) {
    super(
      api,
      new JsonObjectBuilder().add("properties", new JsonObject()).gen(),
      new JsonObjectBuilder().add("id",deviceId).add("properties", new JsonObject()).gen()
    );
    this._subManager = manager;
  }

  // probably not needed?
  // public Device(Api api, SubscriptionManager manager, JsonObject data) {
  //   super(
  //     api,
  //     new JsonObjectBuilder().add("properties", new JsonObject()).gen(),
  //     Util.fixDataDates(data)
  //   );
  //   this._fetched = data.has("properties");
  //   this._subManager = manager;
  // }

  /**
   * function to get the name of the Device
   */
  public String name() {
    return this._getDataProperty("name").getAsString();
  }

  /**
   * function to set the name of the device
   */
  public void name(String name) {
    this._setDataProperty("name",name);
  };

  /**
   * get the Acl of this device
   */
  public Acl acl() {
    return this._getAclProperty();
  }

  /**
   * set the Acl
   */
  public void acl(Acl a) {
    this._setAclProperty(a);
  }

  /**
  * overloaded function
  */
  public void acl(JsonObject e) {
    this._setAclProperty(e);
  }

  /**
   * if log is set or not
   */
  public Boolean log() {
    return this._getDataProperty("log").getAsBoolean();
  }

  /**
   * set the log
   */
  public void log(Boolean value) {
    this._setDataProperty("log",value);
  }

  /**
   * get the schemaId of this device
   */
  public String schemaId() {
    return this._getDataProperty("schemaId").getAsString();
  }

  /**
   * get the Schema instance
   */
  public Schema schema() {
    return new Schema(this._api, this.schemaId());
  }

  /**
   * returns a Map of String to Object <br>
   * The Key represents the property defined in Schema <br>
   * The Object is the value (could be String, Number, Boolean)
   *
   * @see Schema
   */
  public Map<String,Object> properties() {
    Map<String,Object> ret = new HashMap<String,Object>();
    for (Map.Entry <String, JsonElement> it:
      this._data.get("properties").getAsJsonObject().entrySet()) {
        JsonPrimitive elem = it.getValue().getAsJsonPrimitive();
        ret.put(it.getKey(),this._getAsObject(elem));
    }

    for (Map.Entry <String, JsonElement> it:
      this._changes.get("properties").getAsJsonObject().entrySet()) {
        JsonPrimitive elem = it.getValue().getAsJsonPrimitive();
        ret.put(it.getKey(),this._getAsObject(elem));
    }
    return ret;
  }

  /**
   * set a value for a property
   */
  public void set(String name, Number value) {
    this._set(name, new JsonPrimitive(value));
  }

  /**
   * overloaded function
   */
  public void set(String name, Boolean value) {
    this._set(name, new JsonPrimitive(value));
  }

  /**
   * overloaded function
   */
  public void set(String name, String value) {
    this._set(name, new JsonPrimitive(value));
  }

  public void _set(String name, JsonPrimitive p) {
    JsonObject properties = this._changes.getAsJsonObject("properties");
    properties.add(name,p);
    this._changed = true;
    this._changes.add("properties",properties);
  }

  /**
   * gets the property as an Object
   * the Object could be String, Number, Boolean or null
   */
  public Object get(String name) {
    JsonObject properties = this._changes.getAsJsonObject("properties");
    if(properties.has(name)) {
      return this._getAsObject(properties.getAsJsonPrimitive(name));
    } else if(this.propertyExists(name)) {
      return this._getAsObject(this._data.getAsJsonObject("properties").getAsJsonPrimitive(name));
    } else {
      return null;
    }

  }

  /**
   * checks if the property exists
   */
  public Boolean propertyExists(String name) {
    return this._data.getAsJsonObject("properties").has(name);
  }

  public Device save() {
    this._saveChanges(
      new JsonObjectBuilder()
          .add(
            "default",
            new JsonObjectBuilder().add("deviceId",this.id()).gen()
          )
          .add("requestName","updateDevice")
          .add(
            "fields",
            new JsonArrayBuilder().add("name").add("log").add("properties").gen()
          )
          .add("hasAcl",true)
          .gen()
    );
    return this;
  }

  public Device fetch() {
    this._fetch(
      "fetchDevice",
      new JsonObjectBuilder().add("deviceId",this.id()).gen()
    );
    this._data = Util.fixDataDates(this._data);
    return this;
  }

  //TODO change this to sometheing that doesn't return a JsonElement
  //maybe a List<E>
  public JsonElement history(Date start, Date end) {

    JsonObject time = new JsonObject();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    time.addProperty("$gte",df.format(start));
    time.addProperty("$lte",df.format(end));

    return this._api.requestSync(
      "fetchHistory",
      new JsonObjectBuilder()
        .add(
          "constraints",
          new JsonObjectBuilder()
            .add("time",time)
            .gen()
        )
        .gen()
    );
  }

  /**
   * returns history of this device
   * TODO change the return type to a LIST of some kind
   */
  public JsonElement history(Date start, Date end, Integer limit) {

    JsonObject time = new JsonObject();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    time.addProperty("$gte",df.format(start));
    time.addProperty("$lte",df.format(end));

    return this._api.requestSync(
      "fetchHistory",
      new JsonObjectBuilder()
        .add(
          "constraints",
          new JsonObjectBuilder()
            .add("time",time)
            .gen()
        )
        .add(
          "limit",
          limit
        )
        .gen()
    );
  }

  public void remove() {
    this._api.requestSync(
      "deleteDevice",
      new JsonObjectBuilder()
        .add("deviceId",this.id())
        .gen()
    );
  }

  //TODO implement the subscribe functions after implementing socketio api

  private Object _getAsObject(JsonPrimitive p) {
    if(p.isBoolean()) {
      return p.getAsBoolean();
    } else if(p.isNumber()) {
      return p.getAsNumber();
    } else if(p.isString()) {
      return p.getAsString();
    } else {
      throw new Error ("unknown Primitive");
    }
  }
}
