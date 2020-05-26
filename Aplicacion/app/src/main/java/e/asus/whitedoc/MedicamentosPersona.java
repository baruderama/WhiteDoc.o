package e.asus.whitedoc;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
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

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import e.asus.whitedoc.data.model.ListaMedicamentos;
import e.asus.whitedoc.data.model.Medicamento;

public class MedicamentosPersona extends AppCompatActivity {

    private List<Medicamento> medicamentos;
    private Instant fechaModificacion;
    private ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_medicamentos);
        lista = findViewById(R.id.lista_medicamentos);

        medicamentos = new ArrayList<>();
        fechaModificacion = null;

        ListaMedicamentos guardadosLocalmente = obtenerMedicamentosArchivo();
        if(guardadosLocalmente != null) {
            medicamentos = guardadosLocalmente.getMedicamentos();
            fechaModificacion = guardadosLocalmente.getFechaActualización();
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
                intent.putExtra("medicamento", (Parcelable) medicamentos.get( (int) id));
                startActivity(intent);
            }
        });
    }

    void crearListener() {
        String id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference("Recetas").child(id).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Instant ultimaModificacion = Instant.parse(Objects.requireNonNull(dataSnapshot.child("fecha").getValue()).toString());
                if(fechaModificacion != null && fechaModificacion.isAfter(ultimaModificacion)) {
                    Map<String, Object> map = new HashMap<>();
                    Map<String, Object> meds = new HashMap<>();
                    map.put("fecha", fechaModificacion.toString());
                    for(Medicamento medicamento: medicamentos) {
                        meds.put(medicamento.getNombre(), medicamento);
                    }
                    map.put("Medicamentos", meds);
                    String id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    FirebaseDatabase.getInstance().getReference("Recetas").child(id).setValue(new OnCompleteListener() {
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
                else {
                    List<Medicamento> nuevosMedicamentos = new ArrayList<>();
                    for (DataSnapshot nuevo: dataSnapshot.child("Medicamentos").getChildren()) {
                        nuevosMedicamentos.add((Medicamento) nuevo.getValue());
                    }
                    medicamentos = nuevosMedicamentos;
                    fechaModificacion = ultimaModificacion;
                    actualizarMedicamentosArchivo(new ListaMedicamentos(ultimaModificacion, nuevosMedicamentos));
                }
                actualizarPantalla();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void actualizarMedicamentosArchivo(ListaMedicamentos medicamentos) {
        //TODO Guardar los datos de los medicamentos en el archivo (lo sobreescribe) (No hace falta verificar que medicamentos es diferente de null)
    }

    ListaMedicamentos obtenerMedicamentosArchivo() {
        //TODO leer medicamentos desde archivo (Revisar la clase para ver bien lo que debe tener el archivo) (Si no se encuentra archivo retornar null)
        return null;
    }

    public void agregarMedicamento(View view) {
        Intent pantallaAgregarMedicamento = new Intent(getApplicationContext(), AgregarMedicamento.class);
        startActivity(pantallaAgregarMedicamento);
    }
}
