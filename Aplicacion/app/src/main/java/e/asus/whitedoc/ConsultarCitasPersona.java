package e.asus.whitedoc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ConsultarCitasPersona extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_persona);
    }

    public void historialMedicos(View v) {
        Intent pantallaMedicos = new Intent(getApplicationContext(), HistorialMedicosXPaciente.class);
        startActivity(pantallaMedicos);
    }

    public void aboutUs(View v) {
        Intent pantallaAbout = new Intent(getApplicationContext(), AboutUs.class);
        startActivity(pantallaAbout);
    }
}
