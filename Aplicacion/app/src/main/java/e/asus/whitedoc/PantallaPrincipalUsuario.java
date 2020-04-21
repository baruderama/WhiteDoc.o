package e.asus.whitedoc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class PantallaPrincipalUsuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal_usuario);
    }

    public void programarCita(View v) {
        Intent pantallaBuscar = new Intent(getApplicationContext(), ProgramarCita.class);
        startActivity(pantallaBuscar);
    }

    public void verCitas(View v) {
        Intent pantallaPersona = new Intent(getApplicationContext(), ConsultarCitasPersona.class);
        startActivity(pantallaPersona);
    }

    public void modificarMedicamentos(View v) {
        Intent pantallaAgregar = new Intent(getApplicationContext(), ModificarMedicamentos.class);
        startActivity(pantallaAgregar);
    }

    public void verPerfil(View v) {
        Intent pantallaPerfil = new Intent(getApplicationContext(), PerfilPaciente.class);
        startActivity(pantallaPerfil);
    }

    public void emergencia(View v) {
        Toast.makeText(this, "Pantalla no implementada", Toast.LENGTH_SHORT).show();
        // Intent pantallaAgregar = new Intent(getApplicationContext(), ModificarMedicamentos.class);
        // startActivity(pantallaAgregar);
    }


    public void verDoctores(View view) {
        Toast.makeText(this, "Pantalla no implementada (Derivado de Lista de Chats)", Toast.LENGTH_SHORT).show();
        // Intent pantallaAgregar = new Intent(getApplicationContext(), ModificarMedicamentos.class);
        // startActivity(pantallaAgregar);
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent pantallaLogIn = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(pantallaLogIn);
        finish();
    }
}
