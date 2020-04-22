package e.asus.whitedoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {
    private EditText txtName;
    private EditText txtMail;
    private EditText txtPassword;
    private Spinner spnTipo;
    private Button btnRegistrar;

    private String name;
    private String email;
    private String password;
    private String type;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        mAuth = FirebaseAuth.getInstance();

        txtName = (EditText) findViewById(R.id.editTxtRegNom);
        txtMail = (EditText) findViewById(R.id.editTxtRegMail);
        txtPassword = (EditText) findViewById(R.id.editTxtRegPass);
        btnRegistrar = (Button) findViewById(R.id.btnRegRegistro);
        spnTipo = (Spinner) findViewById(R.id.spnRegTipo);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_registro, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spnTipo.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = txtName.getText().toString();
                email = txtMail.getText().toString();
                password = txtPassword.getText().toString();
                type = spnTipo.getItemAtPosition(spnTipo.getSelectedItemPosition()).toString();
                if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
                    if(password.length() >= 6){
                        registerUser();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG);
                    }

                }
                else{
                    Toast.makeText(getApplicationContext(), "Hay campos vacíos, debe completarlos para registrarse", Toast.LENGTH_LONG);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void registerUser(){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(Registro.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", name);
                    map.put("email", email);
                    map.put("password", password);
                    map.put("type", type);
                    String id = mAuth.getCurrentUser().getUid();
                    mDatabase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()){
                                Intent intent;
                                if(type.equals("Médico")){
                                    intent = new Intent(getApplicationContext(), PantallaPrincipalMedico.class);
                                    startActivity(intent);
                                }
                                else{
                                    if(type.equals("Paciente")){
                                        intent = new Intent(getApplicationContext(), PantallaPrincipalUsuario.class);
                                        startActivity(intent);
                                    }
                                }
                                FirebaseUser user = mAuth.getCurrentUser();
                                user.sendEmailVerification();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "No fue posible crear los datos correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else{
                    Toast.makeText(getApplicationContext(),"Este usuario ya está registrado",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null)
        {
            mAuth.signOut();
        }
    }

    public void pantallaPrincipal(View v){
        final EditText nombre= findViewById(R.id.editTxtRegNom);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
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
