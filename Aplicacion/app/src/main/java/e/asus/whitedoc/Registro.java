package e.asus.whitedoc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {
    static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    static final int MY_PERMISSIONS_REQUEST_GALLERY = 1;

    private EditText txtName;
    private EditText txtMail;
    private EditText txtPassword;
    private Spinner spnTipo;
    private Button btnSiguiente;
    private ImageButton btnDatePicker;
    private TextView txtvwFecha;

    private String name;
    private String email;
    private String password;
    private String type;
    private String fechaNacimiento;
    private Calendar calendario;
    private DatePickerDialog datePickDial;


    private Pattern pattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        fechaNacimiento = "";
        txtName = (EditText) findViewById(R.id.editTxtRegNom);
        txtMail = (EditText) findViewById(R.id.editTxtRegMail);
        txtPassword = (EditText) findViewById(R.id.editTxtRegPass);
        btnSiguiente = (Button) findViewById(R.id.btnRegRegistro);
        spnTipo = (Spinner) findViewById(R.id.spnRegTipo);
        btnDatePicker = (ImageButton) findViewById(R.id.imgbtnRegDatePicker);
        txtvwFecha = (TextView) findViewById(R.id.txtRegFechaNac);

        pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_registro, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spnTipo.setAdapter(adapter);

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendario = Calendar.getInstance();
                int dia = calendario.get(Calendar.DAY_OF_MONTH);
                int mes = calendario.get(Calendar.MONTH);
                int anio = calendario.get(Calendar.YEAR);

                datePickDial = new DatePickerDialog(Registro.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int mYear, int mMonth, int mDayOfMonth) {
                        txtvwFecha.setText(mDayOfMonth + "/" + (mMonth + 1) + "/" + mYear);
                    }
                }, dia, mes, anio);
                datePickDial.show();
            }
        });

        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = txtName.getText().toString();
                email = txtMail.getText().toString();
                password = txtPassword.getText().toString();
                fechaNacimiento = txtvwFecha.getText().toString();
                Log.i("ENTRA", "fecha " + fechaNacimiento);
                Matcher matcher = pattern.matcher(email);
                if (!matcher.find()) {
                    Toast.makeText(getApplicationContext(), "Correo incorrecto", Toast.LENGTH_LONG).show();
                } else {
                    type = spnTipo.getItemAtPosition(spnTipo.getSelectedItemPosition()).toString();
                    if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !fechaNacimiento.isEmpty()) {
                        Log.i("ENTRA2", "type" + type);
                        if (password.length() >= 6) {
                            Intent intent;
                            if (type.equals("Médico")) {
                                intent = new Intent(getApplicationContext(), RegistroMedico.class);
                                intent.putExtra("name", name);
                                intent.putExtra("email", email);
                                intent.putExtra("password", password);
                                intent.putExtra("type", type);
                                intent.putExtra("fecha", fechaNacimiento);
                                startActivity(intent);
                            } else {
                                if (type.equals("Paciente")) {
                                    intent = new Intent(getApplicationContext(), RegistroPaciente.class);
                                    intent.putExtra("name", name);
                                    intent.putExtra("email", email);
                                    intent.putExtra("password", password);
                                    intent.putExtra("fecha", fechaNacimiento);
                                    startActivity(intent);
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG);
                        }

                    } else {
                        Log.i("NO ENTRA2", "fecha2 " + fechaNacimiento);
                        Toast.makeText(getApplicationContext(), "Hay campos vacíos, debe completarlos para registrarse", Toast.LENGTH_LONG);
                    }
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {

        if (resultCode == RESULT_OK) {
            finish();
        }
    }
}