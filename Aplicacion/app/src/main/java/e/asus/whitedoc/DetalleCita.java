package e.asus.whitedoc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import model.Cita;

public class DetalleCita extends AppCompatActivity {
    TextView descripcion;
    TextView usuario;
    TextView fecha;
    DatabaseReference mRef;
    String correo;

    String description;
    String date;
    String medico;

    public DetalleCita(String medico, String description, String date) {
        this.medico = medico;
        this.description = description;
        this.date = date;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_cita);
        descripcion= findViewById(R.id.descripcion);
        usuario = findViewById(R.id.username);
        fecha= findViewById(R.id.fecha);
        correo= getIntent().getStringExtra("correo");
        mRef= FirebaseDatabase.getInstance().getReference("Citas");
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Cita citica=dataSnapshot.getValue(Cita.class);

                //Toast.makeText(AceptarRechazarCitas.this, citica.getEmailUser(), Toast.LENGTH_LONG).show();

                if(citica.getUsername().equals(correo)) {
                    usuario.setText(citica.getEmailUser());
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

    public String getCorreo() {
        return correo;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getMedico() {
        return medico;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("Descripcion", this.getDescription());
            obj.put("Fecha", this.getDate());
            obj.put("Medico", this.getMedico());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }


}
