package e.asus.whitedoc.data.model;

import java.util.Date;

public class Medicamento {
    private String nombre;
    private Date horario; //Debe guardar la fecha a la que se va a tomar la primera pasta
    private Integer periodo; // Debe guardar la fecha

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getHorario() {
        return horario;
    }

    public void setHorario(Date horario) {
        this.horario = horario;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
