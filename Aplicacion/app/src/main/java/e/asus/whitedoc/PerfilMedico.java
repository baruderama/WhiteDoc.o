package e.asus.whitedoc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class PerfilMedico extends AppCompatActivity {
    private ImageView imgFotoPerfilMedico;
    private Button guardarCambios;
    private EditText nombreMedico;
    private EditText fechaNacMedico;
    private EditText especialidadMedico;
    private EditText ubicacionConsulMedico;
    static final int IMAGE_PICKER_REQUEST =98;
    static final int PERMISSION_CAMERA=1;
    static final int PERMISSION_GALERY=2;
    static final int REQUEST_IMAGE_CAPTURE =99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_medico);
        inflar();
    }
    public void inflar() {
        this.guardarCambios = findViewById(R.id.btn_guardarPerfilMedico);
        this.nombreMedico = findViewById(R.id.txt_perfilMedicoNombreMedico);
        this.fechaNacMedico = findViewById(R.id.txt_PerfilMedicoFecha);
        this.especialidadMedico = findViewById(R.id.txt_perfilMedicoEspecialidad);
        this.imgFotoPerfilMedico = findViewById(R.id.img_perfilMedicoFoto);
        /* llenado inicial */
        this.guardarCambios.setEnabled(false);
        this.nombreMedico.setText("Pepito");
        this.nombreMedico.setEnabled(false);
        this.fechaNacMedico.setText("22/06/1995");
        this.fechaNacMedico.setEnabled(false);
        this.especialidadMedico.setText("Psicologo");
        this.especialidadMedico.setEnabled(false);
    }
    public void guardarPerfilMedico(View v) {
        this.guardarCambios.setEnabled(false);
        this.nombreMedico.setEnabled(false);
        this.fechaNacMedico.setEnabled(false);
        this.especialidadMedico.setEnabled(false);
        Toast.makeText(this, "Falta implementar ", Toast.LENGTH_SHORT).show();
    }
    public void cambiarFotoPerfilCamara(View v) {
        this.guardarCambios.setEnabled(true);
        requestPermission(PerfilMedico.this, CAMERA, "Es necesario para acceder a la cámara", PERMISSION_CAMERA );
        takePicture();
    }
    public void cambiarFotoPerfilGaleria(View v) {
        this.guardarCambios.setEnabled(true);
        requestPermission(PerfilMedico.this, READ_EXTERNAL_STORAGE, "Es necesario para acceder a la galeria", PERMISSION_GALERY );
        selectImage();
    }

    public void editarNombreMedico(View v) {
        this.guardarCambios.setEnabled(true);
        this.nombreMedico.setEnabled(true);
        this.nombreMedico.setClickable(true);
        this.nombreMedico.setFocusable(true);
        this.nombreMedico.requestFocus();
    }
    public void editarEspecialidadMedico(View v) {
        this.guardarCambios.setEnabled(true);
        this.especialidadMedico.setEnabled(true);
        this.especialidadMedico.setClickable(true);
        this.especialidadMedico.setFocusable(true);
        this.especialidadMedico.requestFocus();
    }

    public void editarFechaNacMedico(View v) {
        this.guardarCambios.setEnabled(true);
        this.fechaNacMedico.setEnabled(true);
        this.fechaNacMedico.setClickable(true);
        this.fechaNacMedico.setFocusable(true);
        this.fechaNacMedico.requestFocus();
    }
    public void editarUbicacionConsMedico(View v) {
        this.guardarCambios.setEnabled(true);
        Toast.makeText(this,"Falta implementar", Toast.LENGTH_SHORT).show();
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
                            this.imgFotoPerfilMedico.setImageBitmap(selectedImage);
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
                        this.imgFotoPerfilMedico.setImageBitmap(selectedImage);
                    }
                }
                break;
        }
    }
    /*public void pantallaChat(View v) {
        Intent pantallaChat = new Intent(getApplicationContext(), Chat.class);
        startActivity(pantallaChat);
    }*/
}
