package io.skygrid;

import io.skygrid.SkygridObject;
import io.skygrid.SkygridError;
import io.skygrid.Api;
import io.skygrid.JsonObjectBuilder;
import io.skygrid.JsonArrayBuilder;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

public class User extends SkygridObject {
  public User(Api api, String id) {
    super(api,
          new JsonObject(),
          new JsonObjectBuilder().add("id",id).gen());
  }
  
  public String email() {
    return this._getDataProperty("email").getAsString();
  }
  
  public void email(String email) {
    this._setDataProperty("email",email);
  }
  
  public JsonElement meta () {
    return this._getDataProperty("meta");
  }
  
  public void meta(JsonElement meta) {
    this._setDataProperty("meta",meta);
  }
  
  @Override
  public void save() {
    if(! this._api.hasMasterkey()) {
      throw new SkygridError ("Can only edit users using masterKey");
    } else {
      this._saveChanges(
        new JsonObjectBuilder()
            .add("default", new JsonObjectBuilder().add("userId",this.id()).gen())
            .add("requestName","updateUser")
            .add("fields",new JsonArrayBuilder().add("email").add("meta").gen())
            .gen()
      );
    }
  }
  
  @Override
  public void fetch() {
    this._fetch(
      "fetchUser",
      new JsonObjectBuilder()
          .add("userId",this.id())
          .gen()
    );
  }
  
  public void remove() {
    this._api.requestSync(
      "deleteUser",
      new JsonObjectBuilder()
          .add("userId",this.id())
          .gen()
    );
  }
}