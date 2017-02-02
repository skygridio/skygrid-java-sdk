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

  /**
   * Getter
   */
  public String email() {
    return this._getDataProperty("email").getAsString();
  }

  /**
   * Setter
   */
  public void email(String email) {
    this._setDataProperty("email",email);
  }

  /**
   * Getter
   */
  public JsonElement meta () {
    return this._getDataProperty("meta");
  }

  /**
   * Setter
   */
  public void meta(JsonElement meta) {
    this._setDataProperty("meta",meta);
  }

  /**
   * Saves the object to the server
   */
  @Override
  public User save() {
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
    return this;
  }

  /**
   * Fetches the data of this object from the server
   */
  @Override
  public User fetch() {
    this._fetch(
      "fetchUser",
      new JsonObjectBuilder()
          .add("userId",this.id())
          .gen()
    );
    return this;
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
