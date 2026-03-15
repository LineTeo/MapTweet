package tweet.model;

import java.io.Serializable;

public class User implements Serializable {
  private String id;
  private String pass;
  private String name;
  private String profile;

  public User() { }
  public User(String id, String pass, String name, String profile) {
    this.id = id;
    this.pass = pass;
    this.name = name;
    this.profile = profile;
  }
  public String getId() { return id; }
  public String getPass() { return pass; }
  public String getName() { return name; }
  public String getProfile() { return profile; }
}