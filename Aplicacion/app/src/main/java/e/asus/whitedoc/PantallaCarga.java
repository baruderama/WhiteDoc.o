package e.asus.whitedoc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import e.asus.whitedoc.helper.Utils;

public class PantallaCarga extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_carga);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Intent pantallaLogIn = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(pantallaLogIn);
            finish();
        }
        else {
            if(Utils.leerTipoUsuario(getBaseContext()).equals("Médico")) {
                Intent pantallaPrincipal = new Intent(getApplicationContext(), PantallaPrincipalMedico.class);
                startActivity(pantallaPrincipal);
                finish();
            }
            else if(Utils.leerTipoUsuario(getBaseContext()).equals("Paciente")) {
                    Intent pantallaPrincipal = new Intent(getApplicationContext(), PantallaPrincipalUsuario.class);
                    startActivity(pantallaPrincipal);
                    finish();
            }
        }
    }
}
