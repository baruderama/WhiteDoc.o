package e.asus.whitedoc;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.time.Instant;
import java.util.Calendar;

import model.Medicamento;

public class ModificarMedicamento extends AppCompatActivity {
    private EditText editNombre;
    private EditText editDescripcion;
    private EditText editPrimeraDosis;
    private EditText editDiaInicio;
    private Spinner spinPeriodo;

    private Medicamento medicamento;
    private int posicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_medicamento);

        Bundle extras = this.getIntent().getExtras();
        medicamento = (Medicamento) extras.get("medicamento");
        posicion = (int) extras.get("posicion");

        inject();
    }

    private void inject() {
        editNombre = (EditText) findViewById(R.id.editNombreMed);
        editDescripcion = (EditText) findViewById(R.id.editDescripcionMed);
        editDiaInicio = (EditText) findViewById(R.id.editDiaInicio);
        editPrimeraDosis = (EditText) findViewById(R.id.editPrimeraDosis);
        spinPeriodo = (Spinner) findViewById(R.id.spinnerPeriodoMed);

        editNombre.setText(medicamento.getNombre());
        editDescripcion.setText(medicamento.getDescripcion());
        editPrimeraDosis.setText(medicamento.getHorario().toString().substring(11, 16));
        editDiaInicio.setText(medicamento.getHorario().toString().substring(0, 10));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_medicamento, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinPeriodo.setAdapter(adapter);
        int horas = medicamento.getPeriodo();

        switch(horas) {
            case 4:
                spinPeriodo.setSelection(0);
                break;
            case 6:
                spinPeriodo.setSelection(1);
                break;
            case 8:
                spinPeriodo.setSelection(2);
                break;
            case 12:
                spinPeriodo.setSelection(3);
                break;
            case 24:
                spinPeriodo.setSelection(4);
                break;
            case 48:
                spinPeriodo.setSelection(5);
                break;
            case 72:
                spinPeriodo.setSelection(6);
                break;
            case 168:
                spinPeriodo.setSelection(7);
                break;
            case 758:
                spinPeriodo.setSelection(8);
                break;
        }
    }

    public void gestionMedicamento(View v) {
        Intent pantallaGestion = new Intent(getApplicationContext(), MedicamentosPersona.class);
        startActivity(pantallaGestion);
    }

    public void pedirHora(View view) {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        editPrimeraDosis.setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void pedirFecha(View view) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        editDiaInicio.setText(String.format("%04d", year) + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth));

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void guardarCambiosMedicamento(View view) {
        String nombre = editNombre.getText().toString();
        String descripcion = editDescripcion.getText().toString();
        Instant primeraDosis = Instant.parse(editDiaInicio.getText() + "T"+ editPrimeraDosis.getText()+ ":00.00Z");
        Integer horasPeriodo = null;
        if(spinPeriodo.getSelectedItemPosition() == 0)
            horasPeriodo = 4;
        else if(spinPeriodo.getSelectedItemPosition() == 1)
            horasPeriodo = 6;
        else if(spinPeriodo.getSelectedItemPosition() == 2)
            horasPeriodo = 8;
        else if(spinPeriodo.getSelectedItemPosition() == 3)
            horasPeriodo = 12;
        else if(spinPeriodo.getSelectedItemPosition() == 4)
            horasPeriodo = 24;
        else if(spinPeriodo.getSelectedItemPosition() == 5)
            horasPeriodo = 48;
        else if(spinPeriodo.getSelectedItemPosition() == 6)
            horasPeriodo = 72;
        else if(spinPeriodo.getSelectedItemPosition() == 7)
            horasPeriodo = 168;
        else if(spinPeriodo.getSelectedItemPosition() == 8)
            horasPeriodo = 672;

        if(medicamentoEsValido(nombre, descripcion, primeraDosis, horasPeriodo)) {
            Intent intent=new Intent();
            intent.putExtra("medicamento", new Medicamento(nombre, descripcion, primeraDosis, horasPeriodo));
            intent.putExtra("posicion", posicion);
            setResult(RESULT_OK, intent);
            finish();//finishing activity
        }
    }

    private boolean medicamentoEsValido(String nombre, String descripcion, Instant primeraDosis, Integer horasPeriodo) {
        if(nombre == null || descripcion == null || primeraDosis == null || horasPeriodo == null || horasPeriodo < 1) {
            Toast.makeText(getApplicationContext(), "No se puede cambiar el medicamento con estos datos", Toast.LENGTH_SHORT);
            return false;
        }
        if(nombre.equals(medicamento.getNombre())) {
            Toast.makeText(getApplicationContext(), "No se puede cambiar el medicamento con estos datos", Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }

    public void eliminarMedicamento(View view) {
    }
}
