package e.asus.whitedoc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class calendarioCita extends AppCompatActivity {

    JSONArray citasMedicas;
    FirebaseUser fuser;
    EditText editText;
    CalendarView calendarView;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario_cita);

        citasMedicas = new JSONArray();
        fuser= FirebaseAuth.getInstance().getCurrentUser();
    }

    public void creacionCita(View view){

        editText=findViewById(R.id.text_description);
        calendarView=findViewById(R.id.calendarView2);
        String descrip=editText.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String selectedDate = sdf.format(new Date(calendarView.getDate()));
        String user = getIntent().getStringExtra("username");
        String emailUser = fuser.getEmail();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("descripcion",descrip);
        hashMap.put("dateFormat",selectedDate);
        hashMap.put("username",user);
        hashMap.put("emailUser",emailUser);

        DetalleCita detalleCita = new DetalleCita(user,descrip,selectedDate);
        citasMedicas.put(detalleCita.toJSON());

        createJSON(detalleCita);

        reference.child("Citas").push().setValue(hashMap);
        Intent intent = new Intent(getApplicationContext(), PantallaPrincipalUsuario.class);
        startActivity(intent);
    }

    public void createJSON(DetalleCita detalleCita) {
        Writer output = null;
        String filename= "citasMedicas.json";

        try {
            File file = new File(getBaseContext().getFilesDir(), filename);
            Log.i("CITA", "Ubicacion de archivo: " + file.getAbsolutePath());
            output = new BufferedWriter(new FileWriter(file));
            output.write(citasMedicas.toString());
            output.close();
            Toast.makeText(getApplicationContext(), "Cita médica guardada", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.i("EXCEPTION", e.toString());
        }
    }
}
