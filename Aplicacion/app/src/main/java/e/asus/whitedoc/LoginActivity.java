package e.asus.whitedoc;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import e.asus.whitedoc.PantallaPrincipalMedico;
import e.asus.whitedoc.PantallaPrincipalUsuario;
import e.asus.whitedoc.R;
import e.asus.whitedoc.Registro;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String usuario;
    private String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.btnLogin);
        final Button registrarseButton = findViewById(R.id.btnRegistrarse);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        mAuth = FirebaseAuth.getInstance();

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed out

                }
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadingProgressBar.setVisibility(View.VISIBLE);

                // El email a validar
                usuario = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();

                loginUser();
            }
        });
        registrarseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registrarseActivity = new Intent(getApplicationContext(), Registro.class);
                startActivity(registrarseActivity);
            }
        });
    }

    private void loginUser(){
        // Patrón para validar el email
        Pattern pattern = Pattern
                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = pattern.matcher(usuario);
        if(!usuario.isEmpty() && !password.isEmpty())
        {
            if (!matcher.find()) {
                Toast.makeText(getApplicationContext(), "Usuario incorrecto", Toast.LENGTH_SHORT).show();
            }
            else{
                if(password.length() > 5)
                {
                    mAuth.signInWithEmailAndPassword(usuario,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.i("ENTRA", "hola " + usuario + password);
                            if(task.isSuccessful()){
                                Log.i("ENTRA", "hola 2");
                                String id = mAuth.getCurrentUser().getUid();
                                mDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            if(dataSnapshot.child("type").getValue().toString().equals("Médico")){
                                                Intent pantallaPrincipal = new Intent(getApplicationContext(), Chat.class);
                                                startActivity(pantallaPrincipal);
                                                finish();
                                            }
                                            else
                                            {
                                                if(dataSnapshot.child("type").getValue().toString().equals("Paciente")){
                                                    Intent pantallaPrincipal = new Intent(getApplicationContext(), Chat.class);
                                                    startActivity(pantallaPrincipal);
                                                    finish();
                                                }
                                            }
                                        }
                                        else {
                                            Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrecto", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Usuario o Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                }

            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "CAMPOS INCORRECTOS: Campo Usuario o contraseña es vacío", Toast.LENGTH_SHORT).show();
        }
    }
}
