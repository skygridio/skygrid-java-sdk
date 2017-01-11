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
import com.google.gson.Gson;


public class RestApi implements Api {

    private class EndpointParams {
        String method;
        JsonElement body;
        JsonElement headers;
        
        public EndpointParams(String m) {
            this.method = m;
            this.body = null;
            this.headers = null;
        }
        
        public EndpointParams(String m, JsonElement d) {
            this.method = m;
            this.body = d;
            this.headers = null;
        }
        
        public String getMethod() {
            return this.method;
        }
        
        public Boolean hasBody() {
            return this.body != null;
        }
        public Boolean hasHeaders() {
            return this.headers != null;
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
    }
    
    private JsonElement _fetchJsonSync(String url, EndpointParams params) {
        try {
            URL urlObject = new URL(this.address.concat(url));
            HttpURLConnection con = (HttpURLConnection) urlObject.openConnection();  
            con.setRequestMethod(params.getMethod().toUpperCase());
            con.setRequestProperty("X-Project-Id",this.projectId);
            System.out.println(con.getResponseMessage());
            
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));  
            String output;  
            StringBuffer response = new StringBuffer();  

            while ((output = in.readLine()) != null) {  
                response.append(output);  
            }  
            in.close();  
            
            Gson reader = new Gson();
            return reader.toJsonTree(response.toString().replaceAll("^\"|\"$", ""));
            
        } catch( ProtocolException e) {
            throw new Error("You messed up son");
        } catch( Throwable e) {
            System.err.println(e.getMessage());
        }
        return new JsonObject();
    }
    
}