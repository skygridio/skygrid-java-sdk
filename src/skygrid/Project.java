package io.skygrid;

import io.skygrid.SkygridObject;
import io.skygrid.Api;
import io.skygrid.JsonObjectBuilder;

import java.lang.Error;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;



public class Project extends SkygridObject {
  String projectId;

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
  }

  public Project(String projectId, String address) {
    this(projectId,address,"rest");
  }

  public Project(String projectId) {
    this(projectId,"https://api.skygrid.io","rest");
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
}