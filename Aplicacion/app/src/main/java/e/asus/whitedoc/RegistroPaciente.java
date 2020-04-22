package e.asus.whitedoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistroPaciente extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText txtName;
    private EditText txtMail;
    private EditText txtPassword;
    private Spinner spnTipo;
    private Button btnSiguiente;

    private String name;
    private String email;
    private String password;
    private String type;

    static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    static final int MY_PERMISSIONS_REQUEST_GALLERY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_paciente);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void registerUser(){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
                                if(type == "Médico"){
                                    intent = new Intent(getApplicationContext(), PantallaPrincipalMedico.class);
                                    startActivity(intent);
                                }
                                else{
                                    if(type == "Paciente"){
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
                                Toast.makeText(getApplicationContext(), "No fue posible crear los datos correctamente", Toast.LENGTH_SHORT);
                            }
                        }
                    });

                }
                else{
                    Toast.makeText(getApplicationContext(),"No se pudo registrar este usuario",Toast.LENGTH_SHORT);
                }
            }
        });
    }
}
