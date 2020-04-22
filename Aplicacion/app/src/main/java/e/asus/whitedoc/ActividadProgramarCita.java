package e.asus.whitedoc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import model.User;

public class ActividadProgramarCita extends AppCompatActivity {

    ListView myListView;
    ArrayList<String> myarrayList=new ArrayList<>();
    //ArrayList<String> myEmailList=new ArrayList<>();
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_programar_cita);

        final ArrayAdapter<String> myArrayAdapter=new ArrayAdapter<String>(ActividadProgramarCita.this, android.R.layout.simple_list_item_1,myarrayList);


        myListView=(ListView) findViewById(R.id.listview1);
        myListView.setAdapter(myArrayAdapter);

        mRef= FirebaseDatabase.getInstance().getReference("Users");
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user=dataSnapshot.getValue(User.class);
                if(user.getType().equals("Médico")) {
                    String value = user.getName();

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


        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //adapterView.getItemAtPosition(i);
                String userName= String.valueOf(adapterView.getItemAtPosition(i));
                Toast.makeText(ActividadProgramarCita.this, userName, Toast.LENGTH_LONG).show();
                Intent calendarioCita = new Intent(getApplicationContext(), calendarioCita.class);
                calendarioCita.putExtra("username",userName);
                startActivity(calendarioCita);

            }


        });


    }




}
