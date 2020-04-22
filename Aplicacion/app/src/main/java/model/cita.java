package model;

import java.text.DateFormat;

public class cita {
    public String descripcion;
    public DateFormat dateFormat;
    public String username;
    public String email;
    public String emailUser;

    public cita(String descripcion, DateFormat dateFormat, String username, String email,String emailUser) {
        this.descripcion = descripcion;
        this.dateFormat = dateFormat;
        this.username = username;
        this.email = email;
        this.emailUser=emailUser;
    }

    public cita(){

    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }
}
