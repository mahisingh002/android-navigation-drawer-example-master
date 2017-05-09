package net.simplifiedcoding.navigationdrawerexample.Model;

import java.io.Serializable;

/**
 * Created by vibes on 12/3/17.
 */

public class Login  implements Serializable{

    private String message;
    private String id;
    private String email;
    private String designation;
    private String name;
    private String validuser;
    private String session_key;

    public String getValiduser() {
        return validuser;
    }

    public void setValiduser(String validuser) {
        this.validuser = validuser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {

        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Login{" +
                "message='" + message + '\'' +
                ", id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", designation='" + designation + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }
}
