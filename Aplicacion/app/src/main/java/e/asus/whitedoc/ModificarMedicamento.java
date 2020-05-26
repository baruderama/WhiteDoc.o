package e.asus.whitedoc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ModificarMedicamento extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_medicamento);
    }

    public void gestionMedicamento(View v) {
        Intent pantallaGestion = new Intent(getApplicationContext(), MedicamentosPersona.class);
        startActivity(pantallaGestion);
    }
}
