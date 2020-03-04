package e.asus.whitedoc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PantallaPrincipalMedico extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal_medico);

    }
    public void pantallaCalendario(View v) {
        Intent pantallaCalendario = new Intent(getApplicationContext(), DisponibilidadMedico.class);
        startActivity(pantallaCalendario);
    }

    public void perfilMedico(View v) {
        Intent pantallaperfil = new Intent(getApplicationContext(), DetallePersona.class);
        startActivity(pantallaperfil);
    }

    public void perfilPaciente(View v) {
        Intent pantallaPaciente = new Intent(getApplicationContext(), DetallePaciente.class);
        startActivity(pantallaPaciente);
    }
}
