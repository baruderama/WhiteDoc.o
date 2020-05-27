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
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistroMedico extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    ImageView chgedImgView;
    Button btnCamera;
    Button btnSelPhoto;
    private Button btnSiguiente;
    private Spinner spnRegMedicoEspecialidades;

    private String name;
    private String email;
    private String password;
    private String type;
    private String fechaNacimiento;
    private String especialidad;
    private String mCurrentPhotoPath;
    private StorageReference mStorage;
    private String[] especialidades;

    static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    static final int MY_PERMISSIONS_REQUEST_GALLERY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_medico);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        spnRegMedicoEspecialidades = (Spinner) findViewById(R.id.spnRegMedEspecialidades);
        btnSelPhoto = (Button) findViewById(R.id.btnRegMedSelFoto);
        btnCamera = (Button) findViewById(R.id.btnRegMedTomarFoto);
        chgedImgView = (ImageView) findViewById(R.id.imgviewRegMedImg);
        btnSiguiente = (Button) findViewById(R.id.btnRegMedSiguiente);

        mStorage = FirebaseStorage.getInstance().getReference();
        mCurrentPhotoPath="";


        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                especialidad = spnRegMedicoEspecialidades.getItemAtPosition(spnRegMedicoEspecialidades.getSelectedItemPosition()).toString();
                if(!mCurrentPhotoPath.isEmpty()){
                    Intent intent;
                    intent = new Intent(getApplicationContext(), RegistroPantallaMap.class);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    intent.putExtra("type", type);
                    intent.putExtra("fecha", fechaNacimiento);
                    intent.putExtra("especialidad", especialidad);
                    intent.putExtra("foto", mCurrentPhotoPath);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Es necesario subir una foto", Toast.LENGTH_SHORT);
                }
            }
        });
        mDatabase.child("Especialidades").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("ENTRA", "hola 3");
                if(dataSnapshot.exists()){
                    especialidades = new String[(int)dataSnapshot.getChildrenCount()];
                    int contador = 0;
                    for (DataSnapshot aux: dataSnapshot.getChildren()) {
                        especialidades[contador] = aux.getValue().toString();
                        contador++;
                    }
                    spnRegMedicoEspecialidades.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, especialidades));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission(Manifest.permission.CAMERA, "Es necesario para acceder a la cámara", MY_PERMISSIONS_REQUEST_CAMERA );
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)  == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                }
            }
        });

        btnSelPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission( Manifest.permission.READ_EXTERNAL_STORAGE, "Es necesario para acceder a la cámara", MY_PERMISSIONS_REQUEST_GALLERY );
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)  == PackageManager.PERMISSION_GRANTED) {
                    loadImage();
                }
            }
        });
//        int permissionCheck;
//        permissionCheck =
//                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);


        recibirDatos();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
// If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
// permission was granted, continue with task related to permission
                    takePicture();
                }
            }
            break;
            case MY_PERMISSIONS_REQUEST_GALLERY: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
// permission was granted, continue with task related to permission
                    loadImage();
                }
            }
            break;
// other 'case' lines to check for other
// permissions this app might request
        }
    }
    private void recibirDatos(){
            Bundle extras = getIntent().getExtras();
            name = extras.getString("name");
            email = extras.getString("email");
            password = extras.getString("password");
            fechaNacimiento = extras.getString("fecha");
            type = extras.getString("type");
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

    private void requestPermission(String permiso, String justificacion, int idCode) {
        if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permiso)) {
                // Show an expanation to the user *asynchronously*Â  Â
                Toast.makeText(this, justificacion, Toast.LENGTH_LONG).show();
            }

            // request the permission.Â  Â
            ActivityCompat.requestPermissions(this, new String[]{permiso}, idCode);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            chgedImgView.setBackground(null);
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
                chgedImgView.setBackground(null);
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

    @Override
    protected void onResume() {
        super.onResume();

    }

}
