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
  JsonObject _user;
  Api _api;
  SubscriptionManager _subManager;

  private static String API_URL = "https://api.skygrid.io";
  private static String SOCKETIO_URL = "https://api.skygrid.io:81";

  /**
   * @param projectId the id of the specified project
   * @param address the absolute URL of the api (default is "https://api.skygrid.io")
   * @param api the api type (either "rest" (default) or "socketio")
   */
  public Project(String projectId, String address, String api) {
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

  /**
   * overloaded constructor
   * @param projectId     id
   * @param address       address of the api
   */
  public Project(String projectId, String address) {
    this(projectId,address,"rest");
  }

  /**
   * overloaded constructor
   * @param projectId     id
   */
  public Project(String projectId) {
    this(projectId,"https://api.skygrid.io","rest");
  }

  /**
   * Getter
   * @return String name of the project
   */
  public String name() {
      return this._getDataProperty("name").getAsString();
  }

  /**
   * Getter
   * @return Boolean whether signup of new users is permitted
   */
  public Boolean allowSignup() {
      return this._getDataProperty("allowSignup")
                 .getAsJsonPrimitive()
                 .getAsBoolean();
  }

  /**
   * Setter
   * @param value whether the project should allow signups or not
   */
  public void allowSignup(Boolean value) {
      this._setDataProperty("allowSignup",value);
  }

  /**
   * Getter
   * @return Acl current Access plans of the project
   */
  public Acl acl() {
      return this._getAclProperty();
  }

  /**
   * Setter
   * @param value new Acl to apply to the project
   */
  public void acl(Acl value) {
      this._setAclProperty(value);
  }

  public JsonObject meta() {
      return this._getDataProperty("meta").getAsJsonObject();
  }

  public void meta(JsonObject value) {
      this._setDataProperty("meta",value);
  }

  /**
   * fetches the current time at the server
   */
  public Date fetchServerTime() {
    try {
      SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
      return f.parse(
      this._api
      .requestSync("getServerTime")
      .getAsString());
    } catch ( ParseException e) {
      System.out.println("Something is seriously wrong");
      return null;
    }
  }

  /**
   * signs up a new user to the project
   * @param email email
   * @param password password
   * @return the id of the created user
   */
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

  /**
   * changes the session to a master session (with more privileges)
   * @param masterKey the master key
   */
  public void loginMaster(String masterKey) {
    this._api.requestSync(
      "loginMaster",
      new JsonObjectBuilder()
      .add("masterKey",masterKey)
      .gen()
    );
  }

  /**
   * logins a user
   * @param email    email
   * @param password password
   * @return String id of the user
   */
  public String login(String email, String password) {
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
    return data.get("userId").getAsString();
  }

  /**
   * logouts out of the current session
   */
  public void logout() {
    this._api.requestSync("logout");
    this._user = null;
  }

  /**
   * returns a user
   * @param  userId        id of the user in the project
   * @return User the User object for the specified user
   */
  public User user(String userId) {
    return new User(this._api, userId);
  }

  /**
   * TODO provide documentation for constraints
   * TODO change the JsonObject constraints to something else (maybe another class)
   * a list of users that match the constraints provided
   * @param  constraints   constraints object
   * @param    fetch         whether the users should be fetched or not
   * @return the list of users
   */
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

  /**
   * Overloaded function
   * @param  constraints constrains to search for
   * @return the list of users
   */
  public List<User> users(JsonObject constraints) {
    return users(constraints,true);
  }

  /**
   * adds a new Schema
   * @param       name          name of the Schema
   * @param   properties    The properties of the schema
   * @return            The new Schema object
   */
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

  /**
   * gets a schema instance
   * @param  schemaId      the id of the schema to get
   * @return Schema the Schema object
   */
  public Schema schema(String schemaId) {
    return new Schema(this._api, schemaId);
  }

  /**
   * gets the list of schemas that match some constraints
   * @param  constraints   constraints object
   * @param  fetch         whether to fetch the schemas or not
   * @return List of schemas
   */
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

  /**
   * overloaded function
   * @param constraints   constraints object
   * @return List of users
   */
  public List<Schema> schemas(JsonObject constraints) {
    return schemas(constraints,true);
  }

  /**
   * adds a new device
   * @param  name          name of the device
   * @param  schema        The schema the device will follow
   * @return        The newly added Device object
   */
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

  /**
   * gets a particular device
   * @param  deviceId      id of the device
   * @return Device
   */
  public Device device(String deviceId) {
    return new Device(this._api, this._subManager, deviceId);
  }

  /**
   * gets a list of Devices that match soem constraints
   * @param  constraints   constraints object
   * @param  fetch         whether to fetch the object or not
   * @return list of devices
   */
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

  /**
   * Overloaded Constructor
   * @param  constraints cosntraints to search for
   * @return the list of devices
   */
  public List<Device> devices(JsonObject constraints) {
    return devices(constraints,true);
  }

  /**
   * fetches the current Project
   * @return Project this project
   */
  public Project fetch() {
    this._fetch(
      "fetchProject",
      new JsonObjectBuilder()
          .add("projectId",this.id())
          .gen()
    );
    return this;
  }

  /**
   * saves the current project
   * @return Project this Project
   */
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
