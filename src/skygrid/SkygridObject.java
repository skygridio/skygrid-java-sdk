package io.skygrid;
import java.util.HashMap;
import java.lang.*;

public class SkygridObject {
    HashMap<String, String> _data;
    Boolean _fetched;
    HashMap<String, String> _changes;
    Boolean _changed;
    HashMap<String, String> _changeDefaults;
    
    public SkygridObject() {
        System.out.println("inside SkygridObject");
    }
}