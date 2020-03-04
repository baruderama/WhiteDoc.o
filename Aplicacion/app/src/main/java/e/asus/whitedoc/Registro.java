package e.asus.whitedoc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Registro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);




    }

    public void pantallaPrincipal(View v){
        final EditText nombre= findViewById(R.id.editText4);

        String medico="medico";

        if(nombre.getText().toString().equals(medico)) {
            Intent pantallaPrincipal = new Intent(getApplicationContext(), PantallaPrincipalMedico.class);
            startActivity(pantallaPrincipal);
        }
        else {
            Intent pantallaPrincipal = new Intent(getApplicationContext(), PantallaPrincipalUsuario.class);
            startActivity(pantallaPrincipal);
        }
    }
}
