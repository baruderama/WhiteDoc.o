package e.asus.whitedoc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import model.Chatlist;
import model.User;
//import model.cita;

public class calendarioCita extends AppCompatActivity {

    FirebaseUser fuser;
    //private List<Cita> citasList;
    //private cita cita;
    EditText editText;
    CalendarView calendarView;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario_cita);

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        //String username=





    }


    public void creacionCita(View view){
        editText=findViewById(R.id.text_description);
        calendarView=findViewById(R.id.calendarView2);
        String des=editText.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String selectedDate = sdf.format(new Date(calendarView.getDate()));
        String user=getIntent().getStringExtra("username");
        String emailUser=fuser.getEmail();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("descripcion",des);
        hashMap.put("dateFormat",selectedDate);
        hashMap.put("username",user);
        hashMap.put("emailUser",emailUser);

        reference.child("Citas").push().setValue(hashMap);
        Intent intent = new Intent(getApplicationContext(), PantallaPrincipalUsuario.class);
        //calendarioCita.putExtra("username",adapterView.getItemAtPosition(i).toString());
        startActivity(intent);
    }
}
