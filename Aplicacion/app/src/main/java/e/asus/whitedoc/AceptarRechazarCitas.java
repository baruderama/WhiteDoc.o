package e.asus.whitedoc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import model.Cita;
//import model.User;
//mport model.cita;

public class AceptarRechazarCitas extends AppCompatActivity {

    ListView myListView;
    ArrayList<String> myarrayList=new ArrayList<>();
    //ArrayList<String> myEmailList=new ArrayList<>();
    DatabaseReference mRef;
    FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aceptar_rechazar_citas);

        final ArrayAdapter<String> myArrayAdapter=new ArrayAdapter<String>(AceptarRechazarCitas.this, android.R.layout.simple_list_item_1,myarrayList);
        fuser= FirebaseAuth.getInstance().getCurrentUser();

        myListView=(ListView) findViewById(R.id.listview2);
        myListView.setAdapter(myArrayAdapter);
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent(getApplicationContext(),DetalleCita.class);
                intent.putExtra("correo",(String) adapterView.getItemAtPosition(i));
                startActivity(intent);
            }
        });
        mRef= FirebaseDatabase.getInstance().getReference("Citas");
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Cita citica=dataSnapshot.getValue(Cita.class);

                Toast.makeText(AceptarRechazarCitas.this, citica.getEmailUser(), Toast.LENGTH_LONG).show();

                if(citica.getUsername().equals(fuser.getEmail())) {
                    String value = citica.getEmailUser();

                    myarrayList.add(value);

                    myArrayAdapter.notifyDataSetChanged();
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
    }
}
