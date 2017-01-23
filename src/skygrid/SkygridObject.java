package io.skygrid;
import java.lang.*;
import io.skygrid.Util;
import io.skygrid.Api;
import io.skygrid.Acl;
import io.skygrid.SkygridError;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

public class SkygridObject {
  protected JsonObject _data;
  protected Boolean _fetched = false;
  protected JsonObject _changes;
  protected Boolean _changed = false;
  JsonObject _changeDefaults;
  Api _api;

  public SkygridObject() {
    this._changeDefaults = new JsonObject();
    this._data = new JsonObject();
    this._changes = new JsonObject();
    this._api = null;
  }
  
  public SkygridObject(Api api, JsonObject changeDefaults, JsonObject data) {
    this._changeDefaults = changeDefaults;
    this._data = data;
    this._changes = changeDefaults;
    this._api = api;
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
    this._changes = Util.deepClone(this._changeDefaults).getAsJsonObject();
    this._changed = false;
  }

  public void save() {
    throw new Error("save not implemented for this object");
  }

  public void fetch() {
    throw new Error("fetch not implemented for this object");
  }

  public void fetchIfNeeded() {
    if(this._fetched != true) {
      this.fetch();
    }
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

  protected Acl _getAclProperty() {
    if(!this._changes.has("acl")) {
      return null;
    } else {
      return new Acl(this._changes.get("acl").getAsJsonObject());
    }
  }

  protected void _setAclProperty(JsonObject value) {
    this._setDataProperty("acl",value);
  }

  protected void _setAclProperty(Acl value) {
    this._setAclProperty(value.permissions());
  }

  //pre changeDesc always has default prop
  protected void _saveChanges(JsonObject changeDesc) {
    if( this._changed == true) {
      JsonObject changes = Util.prepareChanges(this._changes, changeDesc.getAsJsonObject("default"));
      this._api.requestSync(changeDesc.get("requestName").getAsString(), changes);
    
      this._data = Util.mergeFields(
        this._data,
        this._changes,
        changeDesc.getAsJsonArray("fields"));
    
      if(changeDesc.has("hasAcl")) {
        this._data = Util.mergeAcl(this._data, this._changes);
      }
    }
  }

  protected JsonElement _fetch(String request, JsonElement desc) {
    this._data = this._api.requestSync(request,desc).getAsJsonObject();
    this._fetched = true;
    return this._data;
  }
}