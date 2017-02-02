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

  /**
   * @param userId the id of the user to set the access for
   * @param accessType a valid accessType (create, delete, read, update, deviceKey)
   * @param allowed whether access is granted for this accesstype or not
   */
  public void setAccess(String userId, String accessType, Boolean allowed) {
    this._setAccess(userId,accessType,allowed);
  }

  /**
   * @param userId the id of the user to set the access for
   * @param accessTypes a list of valid acccessTypes (create, delete, read, update, deviceKey)
   * @param allowed whether access is granted for this accesstype or not
   */
  public void setAccess(String userId, List<String> accessTypes, Boolean allowed) {
    for (String accessType : accessTypes) {
      this._setAccess(userId,accessType,allowed);
    }
  }

  /**
   * Sets access to all users
   * @param accessType access type to set access
   * @param allowed if granted or not
   */
  public void setPublicAccess(String accessType,Boolean allowed) {
		this.setAccess(this.PUBLIC_KEY, accessType, allowed);
	}

  /**
   * returns whether access is granted
   * @param userId the id of the user, who's access needs to be got
   * @param accessType access type to get
   * @return if access is granted or not
   */
	public Boolean getAccess(String userId, String accessType) {
		return this._getAccess(userId, accessType);
	}

  /**
   * @return if access is granted or not
   * @param accessType access type to get
   */
	public Boolean getPublicAccess(String accessType) {
		return this._getAccess(this.PUBLIC_KEY, accessType);
	}

  /**
   * removes this access property
   * @param userId user id
   * @param accessType access type
   */
  public void removeAccess(String userId,String accessType) {
    this._removeAccess(userId,accessType);
	}

  /**
   * removes a set of accesses from a particular user
   */
  public void removeAccess(String userId,List<String> accessTypes) {
    for (String accessType : accessTypes) {
      this._removeAccess(userId,accessType);
    }
	}

  /**
   * removes access property for all users
   */
	public void removePublicAccess(String accessType) {
		this.removeAccess(this.PUBLIC_KEY, accessType);
	}

  /**
   * returns the JSON string representation of the permissions
   */
  public String toJson() {
    return this._permissionsById.toString();
  }

  /**
   * It throws an Error
   * TODO: custom exceptions
   * @param accessType access type to check if it is valid
   */
  private void validateAccessType(String accessType) {
    if( accessType.equals("create") ||
        accessType.equals("read") ||
        accessType.equals("update") ||
        accessType.equals("delete") ||
        accessType.equals("deviceKey")) {
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

  /**
   * gets a particular access property (returns null if not set)
   */
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

  /**
   * private helper
   */
  public void _removeAccess(String id, String type) {
    this.validateAccessType(type);
    if(this._permissionsById.has(id)) {
      JsonObject permissions = this._permissionsById.getAsJsonObject(id);
      permissions.remove(type);
      this._permissionsById.add(id,permissions);
    }
  }

  /**
   * private helper
   */
  public void _removeAccess(String id) {
    this._permissionsById.remove(id);
  }
}
