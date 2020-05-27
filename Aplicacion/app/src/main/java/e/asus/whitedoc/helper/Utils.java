package e.asus.whitedoc.helper;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import model.ListaMedicamentos;
import model.Medicamento;

public class Utils {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void actualizarMedicamentosArchivo(ListaMedicamentos medicamentos, Context baseContext) {
        Log.i("LOCATION", medicamentos.toString() );
        writeJSONObject(medicamentos, baseContext);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ListaMedicamentos obtenerMedicamentosArchivo(Context baseContext) {
        String[] jsongString = readFromFile(baseContext).split("\\\\");
        if(jsongString != null && jsongString.length > 4){
            List<Medicamento> auxMedicamentos = new ArrayList<>();

            for (int i = 1; i<jsongString.length; i+=4){
                auxMedicamentos.add(new Medicamento(jsongString[i],jsongString[i+1],Instant.parse(jsongString[i+2]),Integer.valueOf(jsongString[i+3])));
            }
            ListaMedicamentos medicamentosGuardados = new ListaMedicamentos(Instant.parse(jsongString[0]), auxMedicamentos);
            Log.i("LOCATION", jsongString[0] );
            return medicamentosGuardados;
        }
        return null;
    }

    private static void writeJSONObject(ListaMedicamentos medicamentosLista, Context baseContext){

        JSONArray medicamentos = new JSONArray();
        //medicamentos.put(medicamentosLista.getFechaActualizacion());
        for (int i = 0; i< medicamentosLista.getMedicamentos().size(); i++){
            medicamentos.put(medicamentosLista.getMedicamentos().get(i).toJSON());
        }

        Writer output = null;
        String filename= "medicamentos.json";
        try {
            File file = new File(baseContext.getFilesDir(), filename);
            //Log.i(“LOCATION", "Ubicacion de archivo: "+file);
            output = new BufferedWriter(new FileWriter(file));
            output.write( medicamentosLista.toStringJSON());
            output.close();
        } catch (Exception e) {
            //Log error
        }
    }
    private static String readFromFile(Context baseContext) {

        String ret = "";
        InputStream inputStream = null;
        File file = new File(baseContext.getFilesDir(),"medicamentos.json");
        try {
            FileReader fileReader = null;

            fileReader = new FileReader(file);
            //inputStream = openFileInput("medicamentos.json");

            //if ( inputStream != null ) {
            //InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ( (receiveString = bufferedReader.readLine()) != null ) {
                Log.i("VAL: ", receiveString);
                stringBuilder.append(receiveString);
            }

            ret = stringBuilder.toString();
            bufferedReader.close();
            //}
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        } catch (NullPointerException e){
            return null;
        }
        finally {
//            try {
//
//                //inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

        return ret;
    }
}
