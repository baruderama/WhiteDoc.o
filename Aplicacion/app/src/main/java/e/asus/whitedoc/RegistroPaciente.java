package e.asus.whitedoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegistroPaciente extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText txtName;
    private EditText txtMail;
    private EditText txtPassword;
    private Spinner spnTipo;
    private Button btnSiguiente;
    ImageView chgedImgView;
    Button btnCamera;
    Button btnSelPhoto;

    private String name;
    private String email;
    private String password;
    private String type;
    private String fechaNacimiento;
    private String mCurrentPhotoPath;
    private StorageReference mStorage;

    static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    static final int MY_PERMISSIONS_REQUEST_GALLERY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_paciente);
        recibirDatos();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        mCurrentPhotoPath="";

        btnSelPhoto = (Button) findViewById(R.id.btnRegPacSelFoto);
        btnCamera = (Button) findViewById(R.id.btnRegPacTomarFoto);
        chgedImgView = (ImageView) findViewById(R.id.imgviewRegPacImg);
        btnSiguiente = (Button) findViewById(R.id.btnRegPacSiguiente);

        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mCurrentPhotoPath.isEmpty() ){
                    registerUser();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Es necesario subir una foto", Toast.LENGTH_SHORT);
                }
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)  == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                }
            }
        });

        btnSelPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)  == PackageManager.PERMISSION_GRANTED) {
                    loadImage();
                }
            }
        });
        int permissionCheck;
        permissionCheck =
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        requestPermission(this, Manifest.permission.CAMERA, "Es necesario para acceder a la cámara", MY_PERMISSIONS_REQUEST_CAMERA );
    }

    private void requestPermission(Activity context, String permiso, String justificacion, int idCode) {
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permiso)) {
                // Show an expanation to the user *asynchronously*Â  Â
                Toast.makeText(context, justificacion, Toast.LENGTH_LONG).show();
            }

            // request the permission.Â  Â
            ActivityCompat.requestPermissions(context, new String[]{permiso}, idCode);

        }
    }

    private void loadImage(){
        Intent loadPictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Log.i("LOCATION", "Entra");
        startActivityForResult(loadPictureIntent, MY_PERMISSIONS_REQUEST_GALLERY);
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    private void registerUser(){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Map<String, Object> map = new HashMap<>();
                    map.put("name", name);
                    map.put("email", email);
                    map.put("password", password);
                    map.put("type", type);
                    map.put("foto", mCurrentPhotoPath);
                    map.put("fecha", fechaNacimiento);
                    String id = mAuth.getCurrentUser().getUid();
                    mDatabase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if(task2.isSuccessful()){
                                Intent intent;
                                if(type == "Médico"){
                                    intent = new Intent(getApplicationContext(), PantallaPrincipalMedico.class);
                                    startActivity(intent);
                                }
                                else{
                                    if(type == "Paciente"){
                                        intent = new Intent(getApplicationContext(), PantallaPrincipalUsuario.class);
                                        startActivity(intent);
                                    }
                                }
                                FirebaseUser user = mAuth.getCurrentUser();
                                user.sendEmailVerification();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "No fue posible crear los datos correctamente", Toast.LENGTH_SHORT);
                            }
                        }
                    });

                }
                else{
                    Toast.makeText(getApplicationContext(),"No se pudo registrar este usuario",Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void recibirDatos(){
        Bundle extras = getIntent().getExtras();
        name = extras.getString("name");
        email = extras.getString("email");
        password = extras.getString("password");
        fechaNacimiento = extras.getString("fecha");
        type = extras.getString("type");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            chgedImgView.setImageBitmap(imageBitmap);
            extras = data.getExtras();

            String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());

// Creamos una referencia a la carpeta y el nombre de la imagen donde se guardara
            StorageReference mountainImagesRef = mStorage.child("fotos").child(timeStamp + ".jpeg");
            mCurrentPhotoPath = timeStamp + ".jpeg";
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] datas = baos.toByteArray();

            // Empezamos con la subida a Firebase
            UploadTask uploadTask = mountainImagesRef.putBytes(datas);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getBaseContext(),"Hubo un error",Toast.LENGTH_LONG);
                    mCurrentPhotoPath="";
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getBaseContext(),"Subida con exito",Toast.LENGTH_LONG);
                }
            });

        }
        else {
            if (requestCode == MY_PERMISSIONS_REQUEST_GALLERY && resultCode == RESULT_OK) {
                Uri uri = data.getData();
                chgedImgView.setImageURI(uri);
                StorageReference filePath = mStorage.child("fotos").child(uri.getLastPathSegment()+ ".jpeg");
                mCurrentPhotoPath = uri.getLastPathSegment()+ ".jpeg";
                filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i("Fotos", "Subido");
                    }
                });
                //Uri path = data.getData();
                //chgedImgView.setImageURI(path);
            }
            else
            {
                if(resultCode == RESULT_OK){
                    // finish();
                }
            }
        }
    }
}
