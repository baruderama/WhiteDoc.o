package e.asus.whitedoc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HistorialMedicosXPaciente extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_medicos_x_paciente);
    }

    public void medico(View v) {
        Intent pantallaMedico = new Intent(getApplicationContext(), DetalleMedico.class);
        startActivity(pantallaMedico);
    }
}
