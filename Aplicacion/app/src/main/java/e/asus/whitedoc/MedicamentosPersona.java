package e.asus.whitedoc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import e.asus.whitedoc.helper.Utils;
import model.ListaMedicamentos;
import model.Medicamento;

public class MedicamentosPersona extends AppCompatActivity {

    private List<Medicamento> medicamentos;
    private Instant fechaModificacion;
    private ListView lista;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_medicamentos);
        lista = findViewById(R.id.lista_medicamentos);
        Log.i("RESTA", Instant.now().toString());

        medicamentos = new ArrayList<>();
        fechaModificacion = null;

        ListaMedicamentos guardadosLocalmente = Utils.obtenerMedicamentosArchivo(getBaseContext());
        if(guardadosLocalmente != null) {
            medicamentos = guardadosLocalmente.getMedicamentos();
            fechaModificacion = guardadosLocalmente.getFechaActualizacion();
            actualizarPantalla();
        }
        crearListener();
    }

    private void actualizarPantalla() {
        ArrayAdapter<Medicamento> adapter = new ArrayAdapter<Medicamento>(this, android.R.layout.simple_list_item_1, medicamentos);
        lista.setAdapter(adapter);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), ModificarMedicamento.class);
                intent.putExtra("medicamento", medicamentos.get(position));
                intent.putExtra("posicion", position);
                startActivityForResult(intent, 2);
            }
        });
    }

    void crearListener() {
        String id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference("Recetas").child(id).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    return;
                }
                Instant ultimaModificacion = Instant.ofEpochSecond((Long) dataSnapshot.child("fecha").child("epochSecond").getValue());
                if(fechaModificacion != null && fechaModificacion.isAfter(ultimaModificacion)) {
                    actualizarMedicamentosFirebase();
                }
                else {
                    List<Medicamento> nuevosMedicamentos = new ArrayList<>();
                    for (DataSnapshot nuevo: dataSnapshot.child("Medicamentos").getChildren()) {
                        Medicamento med = new Medicamento(nuevo.child("nombre").getValue().toString(),
                                nuevo.child("descripcion").getValue().toString(),
                                Instant.ofEpochSecond((Long) nuevo.child("horario").child("epochSecond").getValue()),
                                ( (Long) nuevo.child("periodo").getValue()).intValue());
                        nuevosMedicamentos.add(med);
                    }
                    medicamentos = nuevosMedicamentos;
                    fechaModificacion = ultimaModificacion;
                    Utils.actualizarMedicamentosArchivo(new ListaMedicamentos(fechaModificacion, medicamentos), getBaseContext());
                }
                actualizarPantalla();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void agregarMedicamento(View view) {
        ListaMedicamentos info = new ListaMedicamentos(fechaModificacion, medicamentos);
        Intent pantallaAgregarMedicamento = new Intent(getApplicationContext(), AgregarMedicamento.class);
        pantallaAgregarMedicamento.putExtra("medicamentos", info);
        startActivityForResult(pantallaAgregarMedicamento, 1);
    }

    void actualizarMedicamentosFirebase() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> meds = new HashMap<>();
        map.put("fecha", fechaModificacion);
        for(Medicamento medicamento: medicamentos) {
            meds.put(medicamento.getNombre(), medicamento);
        }
        map.put("Medicamentos", meds);
        String id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference("Recetas").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Medicamentos actualizados en la base de datos", Toast.LENGTH_SHORT);
                }
                else {
                    Toast.makeText(getApplicationContext(), "No se pudieron actualizar los medicamentos", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null) {
            if(requestCode ==  1) { //Agregar medicamento
                Bundle extras = data.getExtras();
                Medicamento nuevo = (Medicamento) extras.get("medicamento");
                if(nuevo!=null) {
                    medicamentos.add(nuevo);
                    fechaModificacion = Instant.now();
                    actualizarPantalla();
                    actualizarMedicamentosFirebase();
                }
            }
            else if(requestCode == 2) { // Modificar medicamento
                if(resultCode == RESULT_FIRST_USER) {
                    Bundle extras = data.getExtras();
                    Medicamento nuevo = (Medicamento) extras.get("medicamento");
                    int posicion = (int) extras.get("posicion");
                    medicamentos.remove(posicion);
                    fechaModificacion = Instant.now();
                    actualizarPantalla();
                    actualizarMedicamentosFirebase();
                }
                else if(resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Medicamento nuevo = (Medicamento) extras.get("medicamento");
                    int posicion = (int) extras.get("posicion");
                    if(nuevo!=null) {
                        medicamentos.remove(posicion);
                        medicamentos.add(posicion, nuevo);
                        fechaModificacion = Instant.now();
                        actualizarPantalla();
                        actualizarMedicamentosFirebase();
                    }
                }
            }
        }
    }


}
