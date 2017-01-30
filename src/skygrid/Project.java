package io.skygrid;

import io.skygrid.SkygridObject;
import io.skygrid.Api;
import io.skygrid.User;
import io.skygrid.JsonObjectBuilder;
import io.skygrid.JsonArrayBuilder;
import io.skygrid.SkygridError;
import io.skygrid.SubscriptionManager;

import java.lang.Error;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.LinkedList;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

public class Project extends SkygridObject {
  String projectId;
  //TODO this does not sound like a good idea
  JsonObject _user;
  Api _api;
  SubscriptionManager _subManager;

  private static String API_URL = "https://api.skygrid.io";
  private static String SOCKETIO_URL = "https://api.skygrid.io:81";

  public Project(String projectId, String address, String api){
    this.projectId = projectId;
    if(api == "rest") {
      this._api = new RestApi(address,projectId);
    } else if(api == "socketio") {
      throw new Error("Scoket IO Api not yet implemented");
    } else {
      throw new Error("invalid api arg");
    }
    this._subManager = new SubscriptionManager(this._api);
  }

  public Project(String projectId, String address) {
    this(projectId,address,"rest");
  }

  public Project(String projectId) {
    this(projectId,"https://api.skygrid.io","rest");
  }

  public String name() {
      return this._getDataProperty("name").getAsString();
  }

  public Boolean allowSignup() {
      return this._getDataProperty("allowSignup")
                 .getAsJsonPrimitive()
                 .getAsBoolean();
  }

  public void allowSignup(Boolean value) {
      this._setDataProperty("allowSignup",value);
  }

  public Acl acl() {
      return this._getAclProperty();
  }

  public void acl(Acl value) {
      this._setAclProperty(value);
  }

  public JsonObject meta() {
      return this._getDataProperty("meta").getAsJsonObject();
  }

  public void meta(JsonObject value) {
      this._setDataProperty("meta",value);
  }

  public Date fetchServerTime() throws ParseException {
    SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    return f.parse(
      this._api
      .requestSync("getServerTime")
      .getAsString())
    ;
  }

  public String signup(String email, String password) {
    return this._api.requestSync(
      "signup",
      new JsonObjectBuilder()
      .add("email",email)
      .add("password",email)
      .gen()
    )
    .getAsJsonObject()
    .getAsJsonPrimitive("id")
    .getAsString();
  }

  public void loginMaster(String masterKey) {
    this._api.requestSync(
      "loginMaster",
      new JsonObjectBuilder()
      .add("masterKey",masterKey)
      .gen()
    );
  }

  public void login(String email, String password) {
    JsonObject data = this._api.requestSync(
      "login",
      new JsonObjectBuilder()
          .add("email",email)
          .add("password",password)
          .gen()
    ).getAsJsonObject();
    this._user =
    new JsonObjectBuilder()
      .add("email",email)
      .add("id",data.get("userId"))
      .add("token",data.get("token"))
      .gen();
  }

  public void logout() {
    this._api.requestSync("logout");
    this._user = null;
  }

  public User user(String userId) {
    return new User(this._api, userId);
  }

  public List<User> users(JsonObject constraints, Boolean fetch) {
    JsonArray data = this._api.requestSync(
      "findUsers",
      new JsonObjectBuilder()
          .add("constraints",constraints)
          .add("fetch",fetch)
          .gen()
    ).getAsJsonArray();
    List<User> ret = new LinkedList<User>();
    for(JsonElement user : data) {
      ret.add(this.user(user.getAsJsonObject().get("id").getAsString()));
    }
    return ret;
  }

  public List<User> users(JsonObject constraints) {
    return users(constraints,true);
  }

  public Schema addSchema(String name, JsonObject properties) {
    JsonObject data = this._api.requestSync(
      "addDeviceSchema",
      new JsonObjectBuilder()
          .add("name",name)
          .add("properties",properties)
          .gen()
    ).getAsJsonObject();
    return this.schema(data.get("id").getAsString()).fetch();
  }

  public Schema schema(String schemaId) {
    return new Schema(this._api, schemaId);
  }

  public List<Schema> schemas(JsonObject constraints, Boolean fetch) {
    JsonArray data = this._api.requestSync(
      "findDeviceSchemas",
      new JsonObjectBuilder()
          .add("constraints",constraints)
          .add("fetch",fetch)
          .gen()
    ).getAsJsonArray();
    List<Schema> ret = new LinkedList<Schema>();
    for(JsonElement schema : data) {
      ret.add(this.schema(schema.getAsJsonObject().get("id").getAsString()));
    }
    return ret;
  }

  public List<Schema> schemas(JsonObject constraints) {
    return schemas(constraints,true);
  }

  public Device addDevice(String name, Schema schema) {
    JsonObject data = this._api.requestSync(
      "addDevice",
      new JsonObjectBuilder()
          .add("name",name)
          .add("schemaId",schema.id())
          .gen()
    ).getAsJsonObject();
    return this.device(data.get("id").getAsString()).fetch();
  }

  public Device device(String deviceId) {
    return new Device(this._api, this._subManager, deviceId);
  }

  public List<Device> devices(JsonObject constraints, Boolean fetch) {
    JsonArray data = this._api.requestSync(
      "findeDevices",
      new JsonObjectBuilder()
          .add("constraints",constraints)
          .add("fetch",fetch)
          .gen()
    ).getAsJsonArray();
    List<Device> ret = new LinkedList<Device>();
    for(JsonElement device : data) {
      ret.add(this.device(device.getAsJsonObject().get("id").getAsString()));
    }
    return ret;
  }

  public List<Device> devices(JsonObject constraints) {
    return devices(constraints,true);
  }

  public Project fetch() {
    this._fetch(
      "fetchProject",
      new JsonObjectBuilder()
          .add("projectId",this.id())
          .gen()
    );
    return this;
  }

  public Project save() {
    if (!this._api.hasMasterkey()) {
			throw new SkygridError("Can only edit projects when using the master key");
		}

    this._saveChanges(
      new JsonObjectBuilder()
        .add(
          "default",
          new JsonObjectBuilder()
            .add("projectId",this.id())
            .gen()
        )
        .add("requestName","updateProject")
        .add(
          "fields",
          new JsonArrayBuilder()
            .add("allowSignup")
            .add("meta")
            .gen()
        )
        .add("hasAcl",true)
        .gen()
    );
    return this;
  }
}
