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

/**
 * This class is mostly used internally, to manage subscriptions (duh)
 */
public class SubscriptionManager {
  /**
   * a class to define suvscriptions, used internally
   */
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

  /**
   * adds a new subscription
   * @param callback the callback to be called (you can use Java 8 lambda syntax)
   * @param settings the settings for this subscription, this is passed as the body to the api request
   * @return the id of the newly created subscription (can be used in removeSubscription)
   *
   * @see Subscription#addSubscription
   */
  public Integer addSubscription(BiConsumer<JsonObject,Device> callback, JsonObject settings) {
    settings.addProperty("subscriptionId",this._subscriptionCount++);
    return this._requestSubscription(
      new Subscription(callback,settings,false)
    );
  }

  /**
   * Overloaded function
   */
  public Integer addSubscription(BiConsumer<JsonObject,Device> callback) {
    return this._requestSubscription(
      new Subscription(callback,new JsonObject() ,false)
    );
  }

  /**
   * Removes a certain subscription
   * @param id id of the subscription (returned from the addSubscription)
   * @return whatever the api returns
   */
  public JsonElement removeSubscription(Integer id) {
    this._subscriptions.remove(id);
    return this._api.requestSync(
      "unsubscribe",
      new JsonObjectBuilder().add("subscriptionId",id).gen()
    );
  }

  /**
   * calls the selected subscription with the given arguments
   * @param id id of the subscription
   * @param changes changes object to be passed to the callback
   * @param device device object to be passed to the callback
   */
  public void raise(Integer id, JsonObject changes, Device device) {
    if(this._subscriptions.containsKey(id)) {
      this._subscriptions.get(id).call(changes,device);
    } else {
      new SkygridError("Subscription not found");
    }
  }

  /**
   * request subscriptions of all the inactive subscriptions in the list of subscriptions <br>
   * opposite functionality of invalidateSubscriptions
   */
  public void requestSubscriptions() {
    for (Integer id : this._subscriptions.keySet()) {
      if(!this._subscriptions.get(id).active) {
        this._requestSubscription(this._subscriptions.get(id));
      }
    }
  }

  /**
   * invalidates all subscriptions <br>
   * opposite functionality of requestSubscriptions
   */
  public void invalidateSubscriptions() {
    for (Integer id : this._subscriptions.keySet()) {
      Subscription sub = this._subscriptions.get(id);
      sub.active = false;
      this._subscriptions.put(id,sub);
    }
  }

  /**
   * removes all subscriptions
   */
  public void removeSubscriptions() {
    for (Integer id : this._subscriptions.keySet()) {
      this.removeSubscription(id);
    }
  }

  /**
   * internal function to activate and put a subscription in the list
   */
  private Integer _requestSubscription(Subscription sub) {
    this._api.requestSync("subscribe",sub.settings());
    sub.active = true;
    Integer id = sub.settings().get("subscriptionId").getAsInt();
    this._subscriptions.put(id,sub);
    return id;
  }

}
