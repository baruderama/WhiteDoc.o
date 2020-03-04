package e.asus.whitedoc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DetallePaciente extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_paciente);
    }

    public void agregarRecordatorio(View v) {
        Intent pantallaRecordatorio = new Intent(getApplicationContext(), RecordatorioCita.class);
        startActivity(pantallaRecordatorio);
    }
}
