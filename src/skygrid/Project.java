package io.skygrid;

import io.skygrid.*;

import java.lang.Error;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Project extends SkygridObject {
  String projectId;
  Api _api;

  private class ParamsBuilder {
    JsonObject p;

    public ParamsBuilder() {
      p = new JsonObject();
    }

    public ParamsBuilder add(String k, String v) {
      p.addProperty(k,v);
      return this;
    }

    public JsonElement generate() {
      return p;
    }
  }

  public Project(String projectId,String address,String api){
    this.projectId = projectId;
    if(api == "rest")
      this._api = new RestApi(address,projectId);
    else if(api == "socketio")
      throw new Error("Scoket IO Api not yet implemented");
    else
      throw new Error("invalid api arg");
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
      new ParamsBuilder()
      .add("email",email)
      .add("password",email)
      .generate()
      )
      .getAsJsonObject()
      .get("id")
      .getAsString();
  }

  public void loginMaster(String masterKey) {
    this._api.requestSync(
      "loginMaster",
      new ParamsBuilder()
      .add("masterKey",masterKey)
      .generate()
      );
  }
}