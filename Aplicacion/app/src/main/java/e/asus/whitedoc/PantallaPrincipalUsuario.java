package e.asus.whitedoc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class PantallaPrincipalUsuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal_usuario);
    }

    public void programarCita(View v) {
        Intent pantallaBuscar = new Intent(getApplicationContext(), ActividadProgramarCita.class);
        startActivity(pantallaBuscar);
    }

    public void verCitas(View v) {
        Intent pantallaPersona = new Intent(getApplicationContext(), ConsultarCitasPersona.class);
        startActivity(pantallaPersona);
    }

    public void modificarMedicamentos(View v) {
        Intent pantallaListaMedicamentos = new Intent(getApplicationContext(), MedicamentosPersona.class);
        startActivity(pantallaListaMedicamentos);
    }

    public void verPerfil(View v) {
        Intent pantallaPerfil = new Intent(getApplicationContext(), PerfilPaciente.class);
        startActivity(pantallaPerfil);
    }

    public void emergencia(View v) {
        Intent pantallaEmergencia = new Intent(getApplicationContext(), EmergenciaUsuario.class);
        startActivity(pantallaEmergencia);
    }


    public void verDoctores(View view) {
        Intent pantallaDoctores = new Intent(getApplicationContext(), Chat.class);
        startActivity(pantallaDoctores);
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent pantallaLogIn = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(pantallaLogIn);
        finish();
    }
}
