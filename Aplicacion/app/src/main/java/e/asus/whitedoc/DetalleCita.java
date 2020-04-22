package e.asus.whitedoc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import model.Cita;

public class DetalleCita extends AppCompatActivity {
    TextView descripcion;
    TextView user;
    TextView fecha;
    DatabaseReference mRef;
    String correo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_cita);
        descripcion= findViewById(R.id.descripcion);
        user= findViewById(R.id.username);
        fecha= findViewById(R.id.fecha);
        correo= getIntent().getStringExtra("correo");
        mRef= FirebaseDatabase.getInstance().getReference("Citas");
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Cita citica=dataSnapshot.getValue(Cita.class);

                //Toast.makeText(AceptarRechazarCitas.this, citica.getEmailUser(), Toast.LENGTH_LONG).show();

                if(citica.getUsername().equals(correo)) {
                    user.setText(citica.getEmailUser());
                    descripcion.setText(citica.getDescription());
                    fecha.setText(citica.getDateFormat());


                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    };




}
