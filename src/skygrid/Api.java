package io.skygrid;

import com.google.gson.JsonElement;

public interface Api {
    //TODO a non blocking request func
    public JsonElement requestSync(String name, JsonElement data);
    public JsonElement requestSync(String name);
    public void close();
}