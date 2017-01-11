package io.skygrid;

import java.util.HashMap;

public class Util {
    public static HashMap<String, String> deepClone(HashMap <String,String> map) {
        return new HashMap<String,String>(map);
    }
}