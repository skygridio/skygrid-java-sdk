package io.skygrid;

import java.io.BufferedReader;  
import java.io.DataOutputStream;  
import java.io.InputStreamReader;  
import java.io.IOException;
import java.net.ProtocolException;

import java.lang.Error;
import java.util.Map;
import java.util.HashMap;
import java.util.function.*;

import java.net.HttpURLConnection;  
import java.net.URL;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class RestApi implements Api {

    private class EndpointParams {
        String method;
        JsonObject body;
        JsonObject headers;
        
        public EndpointParams(String m) {
            this.method = m;
            this.body = new JsonObject();
            this.headers = new JsonObject();
        }
        
        public EndpointParams(String m,JsonObject b) {
            this.method = m;
            this.body = b;
            this.headers = new JsonObject();
        }
        
        public EndpointParams addHeaders(String k, String v) {
            headers.addProperty(k,v);
            return this;
        }
        
        public EndpointParams addBody(String k, String v) {
            body.addProperty(k,v);
            return this;
        }
        
        public String getMethod() {
            return this.method;
        }
        
        public Boolean hasBody() {
            return this.body.size() != 0;
        }
        
        public Boolean hasHeaders() {
            return this.headers.size() != 0;
        }
        
        public String getBody() {
            return this.body.toString();
        }
    }

    String address;
    String projectId;
    String _masterKey;
    String _token;
    Map<String,Function<JsonElement,JsonElement>> _endpoints;
    
    public RestApi(String address,String projectId) {
        this.address = address;
        this.projectId = projectId;
        this._masterKey = null;
        this._token = null;
        this._endpoints = new HashMap<String,Function<JsonElement,JsonElement>>();
        this._setupEndpoints();
    }
    
    public void close() {
        
    }
    
    public JsonElement requestSync(String name, JsonElement data) {
        if(! this._endpoints.containsKey(name))
            throw new Error(name.concat(" endpoint does not exist"));
        else 
            return this._endpoints.get(name).apply(data);
    }
    
    public JsonElement requestSync(String name) {
        if(! this._endpoints.containsKey(name))
            throw new Error(name.concat(" endpoint does not exist"));
        else 
            return this._endpoints.get(name).apply(new JsonObject());
    }
    
    private void _setupEndpoints() {
        this._endpoints.put("getServerTime", (data) -> {
            return this._fetchJsonSync(
                "/time",
                new EndpointParams("get")
            );
        });
        
        this._endpoints.put("loginMaster", data -> {
            this._masterKey = data
                .getAsJsonObject()
                .get("masterKey")
                .getAsString();
            return new JsonObject();
        });
        
        this._endpoints.put("signup", data -> {
            return this._fetchJsonSync(
                "/users",
                new EndpointParams("post",data.getAsJsonObject())
            );
        });
    }
    
    private JsonElement _fetchJsonSync(String url, EndpointParams params) {
        try {
            URL urlObject = new URL(this.address.concat(url));
            HttpURLConnection con = (HttpURLConnection) urlObject.openConnection();  
            
            con.setRequestMethod(params.getMethod().toUpperCase());
            con.setRequestProperty("Accept","application/json");
            con.setRequestProperty("Content-Type","application/json");
            
            if(this._token != null ){
                con.setRequestProperty("X-Access-Token",this._token);
            } else {
                if(this._masterKey != null) 
                    con.setRequestProperty("X-Master-Key", this._masterKey);
                
                con.setRequestProperty("X-Project-Id",this.projectId);
            }
            
            if(params.hasBody()) {
                con.setDoOutput(true);  
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());  
                wr.writeBytes(params.getBody());  
                wr.flush();  
                wr.close(); 
            }
            
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));  
            String output;  
            StringBuffer response = new StringBuffer();  
            while ((output = in.readLine()) != null)
                response.append(output);
            in.close();  
            
            JsonParser reader = new JsonParser();
            return reader.parse(response.toString().replaceAll("^\"|\"$", ""));
            
        } catch( ProtocolException e) {
            throw new Error("You messed up son");
        } catch( Throwable e) {
            System.err.println(e.getMessage());
        }
        return new JsonObject();
    }
    
}