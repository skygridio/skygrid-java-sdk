package io.skygrid;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonElement;
import io.skygrid.Util;
import java.util.List;

public class Acl {
  JsonObject _permissionsById;
  private static final String PUBLIC_KEY = "*";
  
  public Acl() {
    this._permissionsById = new JsonObject();
  }
  
  public Acl(JsonObject data) {
    this._permissionsById = Util.deepClone(data).getAsJsonObject();
  }
  
  public Acl(Acl acl) {
    this._permissionsById = Util.deepClone(acl.permissions()).getAsJsonObject();
  }
  
  public JsonObject permissions() {
    return this._permissionsById;
  }
  
  public Boolean isEmpty () {
    return Util.objectEmpty(this._permissionsById);
  }
  
  public void setAccess(String userId, String accessType, Boolean allowed) {
    this._setAccess(userId,accessType,allowed);
  }
  
  public void setAccess(String userId, List<String> accessTypes, Boolean allowed) {
    for (String accessType : accessTypes) {
      this._setAccess(userId,accessType,allowed);
    }
  }
  
  public void setPublicAccess(String accessType,Boolean allowed) {
		this.setAccess(this.PUBLIC_KEY, accessType, allowed);
	}

	public Boolean getAccess(String userId, String accessType) {
		return this._getAccess(userId, accessType);
	}

	public Boolean getPublicAccess(String accessType) {
		return this._getAccess(this.PUBLIC_KEY, accessType);
	}
  
  public void removeAccess(String userId,String accessType) {
    this._removeAccess(userId,accessType);
	}
  
  public void removeAccess(String userId,List<String> accessTypes) {
    for (String accessType : accessTypes) {
      this._removeAccess(userId,accessType);
    }
	}

	public void removePublicAccess(String accessType) {
		this.removeAccess(this.PUBLIC_KEY, accessType);
	}
  
  public String toJson() {
    return this._permissionsById.toString();
  }
  
  private void validateAccessType(String accessType) {
    if( accessType.equals("create") ||
        accessType.equals("read") ||
        accessType.equals("create") ||
        accessType.equals("create") ||
        accessType.equals("create")) {
      return;
    } else {
      throw new Error("Access Type '".concat(accessType).concat("' is invalid"));
    }
  }
  
  private void _setAccess(String id, String type, Boolean allowed) {
    this.validateAccessType(type);
    JsonElement permissionsElem = this._permissionsById.get(id);
    JsonObject permissions;
    if(permissionsElem == null) {
      permissions = new JsonObject();
    } else {
      permissions = permissionsElem.getAsJsonObject();
    }
    permissions.addProperty(type,allowed);
    this._permissionsById.add(id,permissions);
  }
  
  //returns null if no permision is set
  private Boolean _getAccess(String id, String type) {
    this.validateAccessType(type);
    if(! this._permissionsById.has(id)) {
      return null;
    } else {
      JsonElement ret = this._permissionsById.getAsJsonObject(id).get(type);
      if(ret == null) {
        return null;
      } else {
        return ret.getAsBoolean();
      }
    }
  }
  
  public void _removeAccess(String id, String type) {
    this.validateAccessType(type);
    if(this._permissionsById.has(id)) {
      JsonObject permissions = this._permissionsById.getAsJsonObject(id);
      permissions.remove(type);
      this._permissionsById.add(id,permissions);
    }
  }
  
  public void _removeAccess(String id) {
    this._permissionsById.remove(id);
  }
}