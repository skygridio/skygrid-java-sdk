package io.skygrid;

import io.skygrid.SkygridObject;
import io.skygrid.SkygridError;
import io.skygrid.Api;
import io.skygrid.Acl;
import io.skygrid.JsonObjectBuilder;
import io.skygrid.JsonArrayBuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.HashMap;

public class Schema extends SkygridObject {
  public class Property {
    String type;
    String def;
    public Property(String type, String def) {
      this.type = type;
      this.def = def;
    }

    public Property(String type) {
      this.type = type;
      this.def = "";
    }

    public Property(JsonObject o) {
      this.type = o.getAsJsonPrimitive("type").getAsString();
      this.def = o.getAsJsonPrimitive("def").getAsString();
    }

    public String type() {
      return type;
    }

    public String def() {
      return def;
    }
  }

  public Schema (Api api, String id) {
    super(api,
          new JsonObjectBuilder()
            .add("properties", new JsonObject())
            .gen(),
          new JsonObjectBuilder()
            .add("id",id)
            .add("properties", new JsonObject())
            .gen());
  }

  public Schema (Api api, JsonObject data) {
    super(api,
          new JsonObjectBuilder()
            .add("properties", new JsonObject())
            .gen(),
          data);
  }

  public String name() {
    return this._getDataProperty("name").getAsString();
  }

  public void name(String name) {
    this._setDataProperty("name",name);
  }

  public Acl acl() {
    return this._getAclProperty();
  }

  public void acl(JsonObject e) {
    this._setAclProperty(e);
  }

  public void acl(Acl a) {
    this._setAclProperty(a);
  }

  public Map<String,Property> properties() {
    Map<String,Property> ret = new HashMap<String,Property>();
    for (Map.Entry <String, JsonElement> it:
      this._data.get("properties").getAsJsonObject().entrySet()) {
        JsonObject elem = it.getValue().getAsJsonObject();
        ret.put(it.getKey(),
                new Property(elem.get("type").getAsString(),
                             elem.get("def").getAsString()));
    }

    for (Map.Entry <String, JsonElement> it:
      this._changes.get("properties").getAsJsonObject().entrySet()) {
        JsonObject elem = it.getValue().getAsJsonObject();
        ret.put(it.getKey(),
                new Property(elem.get("type").getAsString(),
                             elem.get("def").getAsString()));
    }
    return ret;
  }

  public void addProperty(String name, String type, String def) {
    JsonObject props = this._changes.getAsJsonObject("properties");
    props.add(name,new JsonObjectBuilder().add("type",type).add("def",def).gen());
    this._changes.add("properties",props);
    this._changed = true;
  }

  public void addProperty(String name, String type) {
    this.addProperty(name,type,"");
  }

  public void updateProperty(String name, String type, String def) throws SkygridError {
    if(this._changes.getAsJsonObject("properties").has(name)) {
      this.addProperty(name,type,def);
    } else {
      throw new SkygridError("Property ".concat(name).concat(" does not exist"));
    }
  }

  private Boolean hasPropertyInChanges(String name) {
    return this._changes.getAsJsonObject("properties").has(name);
  }

  private Boolean hasPropertyInData(String name) {
    return this._data.getAsJsonObject("properties").has(name);
  }

  public Property getProperty(String name) throws SkygridError{
    if(hasPropertyInChanges(name)) {
      return new Property(this._changes.getAsJsonObject("properties").getAsJsonObject(name));
    } else if(hasPropertyInData(name)) {
      return new Property(this._data.getAsJsonObject("properties").getAsJsonObject(name));
    } else {
      throw new SkygridError("Property ".concat(name).concat( "does not exist"));
    }
  }

  public void removeProperty(String name) {
    JsonObject props = this._changes.getAsJsonObject("properties");
    props.remove(name);
    this._changes.add("properties",props);
  }

  @Override
  public Schema save() {
    if(this._api.hasMasterkey()) {
      this._saveChanges(
        new JsonObjectBuilder()
            .add("default", new JsonObjectBuilder()
                                .add("schemaId",this.id())
                                .gen())
            .add("requestName","updateDeviceSchema")
            .add("fields",new JsonArrayBuilder().add("name").add("description").add("properties").gen())
            .add("hasAcl",true)
            .gen()
      );
    }
    return this;
  }

  @Override
  public Schema fetch() {
    this._fetch(
      "fetchDeviceSchema",
      new JsonObjectBuilder()
          .add("schemaId",this.id())
          .gen()
    );
    return this;
  }

  public void remove() {
    this._api.requestSync(
      "deleteDeviceSchema",
      new JsonObjectBuilder()
          .add("schemaId", this.id())
          .gen()
    );
  }
}
