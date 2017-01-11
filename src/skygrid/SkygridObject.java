package io.skygrid;
import java.util.HashMap;
import java.lang.*;
import io.skygrid.Util;
import com.google.gson.Gson;

public class SkygridObject {
    HashMap<String, String> _data;
    Boolean _fetched = false;
    HashMap<String, String> _changes;
    Boolean _changed = false;
    HashMap<String, String> _changeDefaults;
    
    public SkygridObject() {
        this._changeDefaults = new HashMap<String,String>();
        this._data = new HashMap<String,String>();
        this._changes = new HashMap<String,String>();
    }
    
    public String id() {
        return this._data.get("id");
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
    
    public void fetchIfNeeded() {
        //TODO learn about promises mate
    }
    
    protected void _setDataProperty(String name,String value) {
        this._changes.put(name,value);
        this._changed = true;
    }
}