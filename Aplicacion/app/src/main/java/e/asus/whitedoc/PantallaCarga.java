package e.asus.whitedoc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PantallaCarga extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_carga);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Intent pantallaLogIn = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(pantallaLogIn);
            finish();
        }
        else {
            FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        if(dataSnapshot.child("type").getValue().toString().equals("Médico")){
                            Intent pantallaPrincipal = new Intent(getApplicationContext(), PantallaPrincipalMedico.class);
                            startActivity(pantallaPrincipal);
                            finish();
                        }
                        else
                        {
                            if(dataSnapshot.child("type").getValue().toString().equals("Paciente")){
                                Intent pantallaPrincipal = new Intent(getApplicationContext(), PantallaPrincipalUsuario.class);
                                startActivity(pantallaPrincipal);
                                finish();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Intent pantallaLogIn = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(pantallaLogIn);
                    finish();
                }
            });
        }
    }

}
