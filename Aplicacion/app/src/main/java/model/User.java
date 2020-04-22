package model;

public class User {
    private String email;
    private String name;
    private String password;
    private String type;

    public User(String email, String name, String password, String type) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.type = type;
    }
    public User(){

    }


    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getType() {
        return type;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setType(String type) {
        this.type = type;
    }
}
