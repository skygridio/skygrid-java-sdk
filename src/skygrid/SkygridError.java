package io.skygrid;
import java.lang.Error;

//TODO remove this and add better exceptions
public class SkygridError extends Error{

  public SkygridError(String message) {
    super(message);
  }
}
