package io.skygrid;

import io.skygrid.SkygridError;
import io.skygrid.Acl;
import io.skygrid.Api;
import io.skygrid.Util;
import io.skygrid.Device;
import io.skygrid.JsonObjectBuilder;

import java.util.function.BiConsumer;
import java.util.Map;
import java.util.HashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

public class SubscriptionManager {
  private class Subscription {
    BiConsumer<JsonObject,Device> _callback;
    JsonObject _settings;
    public Boolean active;
    
    public Subscription(BiConsumer<JsonObject,Device> cb, JsonObject settings, Boolean active) {
      this._callback = cb;
      this._settings = settings;
      this.active = active;
    }
    
    public void call(JsonObject changes, Device device) {
      this._callback.accept(changes,device);
    }
    
    public JsonObject settings() {
      return _settings;
    }
  };
  
  Api _api;
  Integer _subscriptionCount;
  Map<Integer,Subscription> _subscriptions;
  
  public SubscriptionManager(Api api) {
    this._api = api;
    this._subscriptions = new HashMap<Integer,Subscription> ();
    this._subscriptionCount = 0;
  }
  
  //returns the id
  public Integer addSubscription(JsonObject settings, BiConsumer<JsonObject,Device> callback) {
    settings.addProperty("subscriptionId",this._subscriptionCount++);
    return this._requestSubscription(
      new Subscription(callback,settings,false)
    );
  }
  
  //returns the id
  public Integer addSubscription(BiConsumer<JsonObject,Device> callback) {
    return this._requestSubscription(
      new Subscription(callback,new JsonObject() ,false)
    );
  }
  
  public JsonElement removeSubscription(Integer id) {
    this._subscriptions.remove(id);
    return this._api.requestSync(
      "unsubscribe",
      new JsonObjectBuilder().add("subscriptionId",id).gen()
    );
  }
  
  public void raise(Integer id, JsonObject changes, Device device) {
    if(this._subscriptions.containsKey(id)) {
      this._subscriptions.get(id).call(changes,device);
    } else {
      new SkygridError("Subscription not found");
    }
  }
  
  public void requestSubscriptions() {
    for (Integer id : this._subscriptions.keySet()) {
      if(!this._subscriptions.get(id).active) {
        this._requestSubscription(this._subscriptions.get(id));
      }
    }
  }
  
  public void invalidateSubscriptions() {
    for (Integer id : this._subscriptions.keySet()) {
      Subscription sub = this._subscriptions.get(id);
      sub.active = false;
      this._subscriptions.put(id,sub);
    }
  }
  
  public void removeSubscriptions() {
    for (Integer id : this._subscriptions.keySet()) {
      this.removeSubscription(id);
    }
  }
  
  //returns id
  private Integer _requestSubscription(Subscription sub) {
    this._api.requestSync("subscribe",sub.settings());
    sub.active = true;
    Integer id = sub.settings().get("subscriptionId").getAsInt();
    this._subscriptions.put(id,sub);
    return id;
  }
  
}