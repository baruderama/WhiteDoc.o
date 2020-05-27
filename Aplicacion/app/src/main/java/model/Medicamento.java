package model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

public class Medicamento implements Serializable {
    private String nombre;
    private String descripcion;
    private Instant horario; //Debe guardar la fecha a la que se va a tomar la primera pasta
    private Integer periodo; // Debe guardar la fecha

    public Medicamento(String nombre, String descripcion, Instant horario, Integer periodo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.horario = horario;
        this.periodo = periodo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Instant getHorario() {
        return horario;
    }

    public void setHorario(Instant horario) {
        this.horario = horario;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombre + " " + descripcion + " " + horario + " " + periodo;
    }

    public JSONObject toJSON () {
        JSONObject obj = new JSONObject();
        try {
            obj.put("nombre", getNombre());
            obj.put("horario", getHorario());
            obj.put("periodo", getPeriodo());
            obj.put("descripcion", getDescripcion());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
