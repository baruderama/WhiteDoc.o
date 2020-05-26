package e.asus.whitedoc.data.model;

import java.time.Instant;
import java.util.Date;
import java.util.List;

public class ListaMedicamentos {
    Instant fechaActualización;
    List<Medicamento> medicamentos;

    public ListaMedicamentos(Instant fechaActualización, List<Medicamento> medicamentos) {
        this.fechaActualización = fechaActualización;
        this.medicamentos = medicamentos;
    }

    public Instant getFechaActualización() {
        return fechaActualización;
    }

    public void setFechaActualización(Instant fechaActualización) {
        this.fechaActualización = fechaActualización;
    }

    public List<Medicamento> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<Medicamento> medicamentos) {
        this.medicamentos = medicamentos;
    }
}
