package io.skygrid;
import java.lang.Error;

public class SkygridError extends Error{
  
  public SkygridError(String message) {
    super(message);
  }
}