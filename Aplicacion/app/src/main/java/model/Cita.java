package model;

public class Cita {
    public String description;
    public String dateFormat;
    public String emailUser;
    public String username;

    public Cita(String description, String dateFormat, String emailUser, String username) {
        this.description = description;
        this.dateFormat = dateFormat;
        this.emailUser = emailUser;
        this.username = username;
    }

    public Cita(){

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
