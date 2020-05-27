package e.asus.whitedoc;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class ConsultarCitasPacienteActivity extends AppCompatActivity {

    String[] citas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_citas_paciente);

        File file = new File(getBaseContext().getFilesDir(),"citasMedicas.json");

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try {
            line = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (line != null){
            stringBuilder.append(line).append("\n");
            try {
                line = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // This responce will have Json Format String
        String responce = stringBuilder.toString();
        try {
            JSONArray jsonArray  = new JSONArray(responce);
            this.initArray(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, citas);
        ListView listView = (ListView) findViewById(R.id.listview3);
        listView.setAdapter(adapter);
    }

    private void initArray(JSONArray jsonArray) throws JSONException {
        this.citas = new String[jsonArray.length()];
        Log.i("JSON", String.valueOf(jsonArray.length()));
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject job = jsonArray.getJSONObject(i);
            citas[i] += "Médico: " + String.valueOf(job.getString("Medico")) + " - ";
            citas[i] += "Fecha: " + String.valueOf(job.getString("Fecha")) + " - ";
            citas[i] += "Descripción: " + String.valueOf(job.getString("Descripcion"));

        }
    }
}
