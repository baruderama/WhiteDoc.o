package e.asus.whitedoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class PerfilPaciente extends AppCompatActivity {
    private ImageView imgFotoPerfilPaciente;
    private Button guardarCambios;
    private EditText nombrePaciente;
    private EditText fechaNacPaciente;
    static final int IMAGE_PICKER_REQUEST =98;
    static final int PERMISSION_CAMERA=1;
    static final int PERMISSION_GALERY=2;
    static final int REQUEST_IMAGE_CAPTURE =99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_paciente);
        inflar();
    }
    public void inflar() {
        //this.guardarCambios = findViewById(R.id.);
        this.nombrePaciente = findViewById(R.id.txt_perfilPacienteNombre);
        this.fechaNacPaciente = findViewById(R.id.txt_perfilPacienteFechNac);
        this.imgFotoPerfilPaciente = findViewById(R.id.img_perfilPacienteFoto);
        /* llenado inicial */
        this.nombrePaciente.setText("Perencejo");
        this.nombrePaciente.setEnabled(false);
        this.fechaNacPaciente.setText("22/06/1995");
        this.fechaNacPaciente.setEnabled(false);
    }
    public void editarNombrePaciente(View v) {
        this.nombrePaciente.setEnabled(true);
        this.nombrePaciente.setClickable(true);
        this.nombrePaciente.setFocusable(true);
        this.nombrePaciente.requestFocus();
    }
    public void editarFechaNacPaciente(View v) {
        this.fechaNacPaciente.setClickable(true);
        this.fechaNacPaciente.setEnabled(true);
        this.fechaNacPaciente.setFocusable(true);
        this.fechaNacPaciente.requestFocus();
    }
    public void agregarRecordatorio(View v) {
        Intent pantallaRecordatorio = new Intent(getApplicationContext(), RecordatorioCita.class);
        startActivity(pantallaRecordatorio);
    }
    public void cambiarFotoPerfilCamara(View v) {
        requestPermission(PerfilPaciente.this, CAMERA, "Es necesario para acceder a la cámara", PERMISSION_CAMERA );
        takePicture();
    }
    public void cambiarFotoPerfilGaleria(View v) {
        requestPermission(PerfilPaciente.this, READ_EXTERNAL_STORAGE, "Es necesario para acceder a la galeria", PERMISSION_GALERY );
        selectImage();
    }
    public void guardarPerfilPaciente(View v) {
        Toast.makeText(this, "Falta implementar ", Toast.LENGTH_SHORT).show();
    }
    /**
     * Metodo para solicitar un permiso
     * @param context actividad actual
     * @param permission permiso que se desea solicitar
     * @param just justificacion para el permiso
     * @param id identificador con el se marca la solicitud y se captura el callback de respuesta
     */
    public void requestPermission(Activity context, String permission, String just, int id) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                // Show an expanation to the user *asynchronously*Â  Â
                Toast.makeText(context, just, Toast.LENGTH_LONG).show();
            }
            // request the permission.Â  Â
            ActivityCompat.requestPermissions(context, new String[]{permission}, id);

        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Si se tienen varios casos
        switch(requestCode){
            case PERMISSION_CAMERA: {
                takePicture();
                break;
            }
            case PERMISSION_GALERY:
                selectImage();
                break;
        }
    }
    private void takePicture() {
        if(ContextCompat.checkSelfPermission(this, CAMERA)== PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(takePictureIntent.resolveActivity(getPackageManager())!=null) {
                startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void selectImage(){
        if(ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
            Intent pickImage = new Intent(Intent.ACTION_PICK);
            pickImage.setType("image/*");
            startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
        }
    }
    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode codigo de la peticion
     * @param resultCode resultado de la peticion del permiso
     * @param data informacion del archivo en cuestion
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_PICKER_REQUEST:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri imageUri = data.getData();
                        if(imageUri!=null)
                        {
                            InputStream imageStream = getContentResolver().openInputStream(imageUri);
                            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            this.imgFotoPerfilPaciente.setImageBitmap(selectedImage);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if(resultCode==RESULT_OK)
                {
                    Bundle extras =data.getExtras();
                    Bitmap selectedImage= (Bitmap) extras.get("data");
                    if(selectedImage!=null)
                    {
                        this.imgFotoPerfilPaciente.setImageBitmap(selectedImage);
                    }
                }
                break;
        }
    }
}
