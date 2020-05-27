package e.asus.whitedoc;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.time.Instant;

import e.asus.whitedoc.data.model.ListaMedicamentos;
import e.asus.whitedoc.data.model.Medicamento;

public class AgregarMedicamento extends AppCompatActivity {
    private EditText editNombre;
    private EditText editDescripcion;
    private TimePicker timePrimeraDosis;
    private Spinner spinPeriodo;
    private Button btnGuardar;

    private ListaMedicamentos medicamentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_medicamento);

        Bundle extras = this.getIntent().getExtras();
        medicamentos = (ListaMedicamentos) extras.get("medicamentos");

        inject();
    }

    private void inject() {
        editNombre = (EditText) findViewById(R.id.editNombreMed);
        editDescripcion = (EditText) findViewById(R.id.editDescripcionMed);
        timePrimeraDosis = (TimePicker) findViewById(R.id.timePrimeraDosis);
        spinPeriodo = (Spinner) findViewById(R.id.spinnerPeriodoMed);
        btnGuardar = (Button) findViewById(R.id.btnGuardarMed);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_registro, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinPeriodo.setAdapter(adapter);
    }

    private boolean medicamentoEsValido(String nombre, String descripcion, Instant primeraDosis, Integer horasPeriodo) {
        if(nombre == null || descripcion == null || primeraDosis == null || horasPeriodo == null || horasPeriodo < 1) {
            Toast.makeText(getApplicationContext(), "No se puede crear el medicamento con estos datos", Toast.LENGTH_SHORT);
            return false;
        }
        for(Medicamento medicamento: medicamentos.getMedicamentos()) {
            if(nombre.equals(medicamento.getNombre())) {
                Toast.makeText(getApplicationContext(), "No se puede crear el medicamento con estos datos", Toast.LENGTH_SHORT);
                return false;
            }
        }
        return true;
    }

    public void gestionMedicamento(View v) {
        Intent pantallaGestion = new Intent(getApplicationContext(), MedicamentosPersona.class);
        startActivity(pantallaGestion);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void informarMedicamento(View view) {
        String nombre = editNombre.getText().toString();
        String descripcion = editDescripcion.getText().toString();
        Instant primeraDosis = Instant.parse(timePrimeraDosis.get.toString());
        Integer horasPeriodo = null;
        if(spinPeriodo.getSelectedItem().equals("Cada 4 horas"))
            horasPeriodo = 4;
        else if(spinPeriodo.getSelectedItem().equals("Cada 6 horas"))
            horasPeriodo = 6;
        else if(spinPeriodo.getSelectedItem().equals("Cada 8 horas"))
            horasPeriodo = 10;
        else if(spinPeriodo.getSelectedItem().equals("Cada 12 horas"))
            horasPeriodo = 12;
        else if(spinPeriodo.getSelectedItem().equals("Cada 24 horas"))
            horasPeriodo = 24;
        else if(spinPeriodo.getSelectedItem().equals("Cada 48 horas"))
            horasPeriodo = 48;
        else if(spinPeriodo.getSelectedItem().equals("Cada 72 horas"))
            horasPeriodo = 72;
        else if(spinPeriodo.getSelectedItem().equals("Una vez a la semana"))
            horasPeriodo = 189;
        else if(spinPeriodo.getSelectedItem().equals("Una vez cada 4 semanas"))
            horasPeriodo = 758;

        if(medicamentoEsValido(nombre, descripcion, primeraDosis, horasPeriodo)) {
            Intent intent=new Intent();
            intent.putExtra("medicamento", new Medicamento(nombre, descripcion, primeraDosis, horasPeriodo));
            setResult(RESULT_OK, intent);
            finish();//finishing activity
        }
    }
}
