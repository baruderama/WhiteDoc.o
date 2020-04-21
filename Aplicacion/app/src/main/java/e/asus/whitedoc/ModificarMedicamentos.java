package e.asus.whitedoc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ModificarMedicamentos extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_medicamento);
    }

    public void gestionMedicamento(View v) {
        Intent pantallaGestion = new Intent(getApplicationContext(), GestionMedicamentos.class);
        startActivity(pantallaGestion);
    }
}
