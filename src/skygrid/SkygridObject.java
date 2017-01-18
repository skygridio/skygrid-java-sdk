package io.skygrid;
import java.lang.*;
import io.skygrid.Util;
import io.skygrid.Api;
import io.skygrid.Acl;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

public class SkygridObject {
  JsonObject _data;
  Boolean _fetched = false;
  JsonObject _changes;
  Boolean _changed = false;
  JsonObject _changeDefaults;
  Api _api;

  public SkygridObject() {
    this._changeDefaults = new JsonObject();
    this._data = new JsonObject();
    this._changes = new JsonObject();
  }

  public String id() {
    return this._data.get("id").getAsString();
  }

  public Boolean isDirty() {
    return this._changed == true;
  }

  public Boolean isComplete() {
    return this._fetched == true;
  }

  public void discardChanges() {
    this._changes = Util.deepClone(this._changeDefaults);
    this._changed = false;
  }

  public void save() {
    throw new Error("save not implemented for this object");
  }

  public void fetch() {
    throw new Error("fetch not implemented for this object");
  }

  public void fetchIfNeeded() {
    //TODO learn about promises mate
  }

  protected void _setDataProperty(String name, JsonElement value) {
    this._changes.add(name,value);
    this._changed = true;
  }
  protected void _setDataProperty(String name, Boolean value) {
    this._changes.addProperty(name,value);
    this._changed = true;
  }
  protected void _setDataProperty(String name, Character value) {
    this._changes.addProperty(name,value);
    this._changed = true;
  }
  protected void _setDataProperty(String name, Number value) {
    this._changes.addProperty(name,value);
    this._changed = true;
  }
  protected void _setDataProperty(String name, String value) {
    this._changes.addProperty(name,value);
    this._changed = true;
  }

  protected JsonElement _getDataProperty(String name) {
    if(this._changes.has(name) )
      return this._changes.get(name);
    else
      return this._data.get(name);
  }

  //NOTE consider returning Acl instead of JsonElement
  protected JsonElement _getAclProperty() {
    if(!this._changes.has("acl")) {
      //TODO do this after making Acl
      return null;
    } else {
      return this._changes.get("acl");
    }
  }

  protected void _setAclProperty(JsonElement value) {
    this._setDataProperty("acl",value);
  }

  protected void _setAclProperty(Acl value) {
    //TODO convert Acl to JsonElement and then call overloaded function
  }

  //TODO this depends on how Api.request is implemented (some sort of Promise)
  //probably not void return type
  protected void _saveChanges(JsonElement changeDesc) {
    if( this._changed == true) {

    } else {

    }
  }

  //TODO depends on Api.request
  protected void _fetch(String request, JsonElement desc) {
    //TODO
  }
}