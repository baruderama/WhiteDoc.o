package e.asus.whitedoc;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistroPantallaMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    static final int PERMISSION_LOCATION = 0;
    static final double RADIUS_OF_EARTH_KM = 6371.01;
    private android.location.Location mCurrentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private EditText txtDirections;
    private Button btnRegMedMapRegistrar;

    private String name;
    private String email;
    private String password;
    private String type;
    private String fechaNacimiento;
    private String especialidad;
    private String mCurrentPhotoPath;
    private String longitud;
    private String latitud;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Marker currentLocationMarker;
    private Geocoder mGeocoder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_pantalla_map);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        recibirDatos();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        int permissionCheck =
                ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, "Es necesario para localizar", PERMISSION_LOCATION );
        //mLocationRequest = createLocationRequest();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        txtDirections = (EditText) findViewById(R.id.txtDirections);
        currentLocationMarker = null;
        btnRegMedMapRegistrar = (Button) findViewById(R.id.btnRegMedMapRegistrar);

        btnRegMedMapRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLocationMarker != null){
                    registerUser();
                }
            }
        });

        txtDirections.setOnEditorActionListener(new TextView.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.i("MAPA2", "BIEN!!!!!!!!!!!!!!!!!!!!!!!!!" + EditorInfo.IME_ACTION_DONE + " " + actionId);
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH || event == null || event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    String addressString = txtDirections.getText().toString();
                    Log.i("MAPA", "BIEN!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    if (!addressString.isEmpty()) {
                        try {
                            List<Address> addresses = mGeocoder.getFromLocationName(addressString, 2);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address addressResult = addresses.get(0);
                                LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                                mMap.clear();
                                if (mMap != null) {
                                    MarkerOptions myMarkerOptions = new MarkerOptions();
                                    myMarkerOptions.position(position);
                                    myMarkerOptions.title("Dirección Encontrada");
                                    myMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                    currentLocationMarker = mMap.addMarker(myMarkerOptions);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14));
                                }
                            } else {Toast.makeText(RegistroPantallaMap.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();}
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {Toast.makeText(RegistroPantallaMap.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();}
                }
                return false;
            }
        });

    }

    private void recibirDatos(){
        Bundle extras = getIntent().getExtras();
        name = extras.getString("name");
        email = extras.getString("email");
        password = extras.getString("password");
        fechaNacimiento = extras.getString("fechaNacimiento");
        type = extras.getString("type");
        especialidad = extras.getString("especialidad");
        mCurrentPhotoPath = extras.getString("foto");
    }

    @Override
    public void onResume() {
        super.onResume();
        requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, "Es necesario para localizar", PERMISSION_LOCATION);
        //currentLocation();
        currentLocationMarker = null;
        LocationSettingsRequest.Builder builder = new
                LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PERMISSION_LOCATION: {
                if (resultCode == RESULT_OK) {
                     //Se encendió la localización!!!
                } else {
                    Toast.makeText(this,
                            "Sin acceso a localización, hardware deshabilitado!",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
// Agregar un marcador en bogotá
        mGeocoder = new Geocoder(getBaseContext());
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();

               // currentLocation();
                try {
                    Address address = mGeocoder.getFromLocation(latLng.latitude,latLng.longitude,2).get(0);
                    String textAddress = "";
                    if (address != null) {
                        for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                            textAddress += " " + address.getAddressLine(i);
                        }
                        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(textAddress));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        LatLng bogota = new LatLng(4.65, -74.05);
        //mMap.addMarker(new MarkerOptions().position(bogota).title("Marcador en Bogotá"));
        //currentLocation();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bogota));
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
                    map.put("fecha", fechaNacimiento);
                    map.put("especialidad", especialidad);
                    map.put("foto", mCurrentPhotoPath);
                    map.put("Latitud", currentLocationMarker.getPosition().latitude);
                    map.put("Longitud", currentLocationMarker.getPosition().longitude);

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
}
