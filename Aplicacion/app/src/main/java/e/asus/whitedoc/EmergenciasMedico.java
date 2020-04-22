package e.asus.whitedoc;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import e.asus.whitedoc.data.model.Lugar;
import e.asus.whitedoc.helper.FetchURL;
import e.asus.whitedoc.helper.TaskLoadedCallback;

public class EmergenciasMedico extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private boolean ESTADO_INICIADO;
    private FirebaseUser sesUsuario;
    private Map<String, Object> map;
    private String atendiendo;
    private Button boton;

    private GoogleMap mMap;
    private Lugar medico;
    private Lugar paciente;
    private boolean firstDraw;
    private Float lightLevel;
    private DatabaseReference dbRef;

    private AlertDialog dEmpezarEmergencia;
    private AlertDialog dCancelarEmergencia;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private android.location.Location lastLocation;
    private LocationCallback mLocationCallback;

    SensorManager sensorManager;
    Sensor lightSensor;
    SensorEventListener lightSensorListener;

    //Limits for the geocoder search (Colombia)
    public static final double lowerLeftLatitude = 1.396967;
    public static final double lowerLeftLongitude = -78.903968;
    public static final double upperRightLatitude = 11.983639;
    public static final double upperRigthLongitude = -71.869905;

    static final int REQUEST_CHECK_SETTINGS = 100;
    private final int PERMISSION_LOCATION = 10;
    static final double RADIUS_OF_EARTH_KM = 6371, LAT1 = 4.697130, LONG1 = -74.141107;
    static final double DARK_MAP_THRESHOLD = 3;
    private Polyline currentPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencia_usuario);
        ESTADO_INICIADO = false;
        sesUsuario = FirebaseAuth.getInstance().getCurrentUser();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        firstDraw = true;
        medico = new Lugar();
        paciente = new Lugar();
        dbRef = FirebaseDatabase.getInstance().getReference();
        map = new HashMap<>();
        map.put("estado", "libre");
        map.put("asignado", "N/A");
        atendiendo = "N/A";
        inflar();

        requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, "Es necesario para acceder a la ubicacion", PERMISSION_LOCATION);
        mLocationRequest = createLocationRequest();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                android.location.Location location = locationResult.getLastLocation();
                lastLocation = location;
                if (location != null) {
                    medico.setLatitud(location.getLatitude());
                    medico.setLongitud(location.getLongitude());
                    Geocoder mGeocoder = new Geocoder(getBaseContext());
                    try {
                        medico.setTexto(mGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 2).get(0).getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    reCalculateMarkersAndRoute();
                    if (firstDraw && mMap != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(medico.getLatLng()));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                        firstDraw = false;
                    }

                    if(ESTADO_INICIADO){
                        map.put("latitud", medico.getLatitud());
                        map.put("longitud", medico.getLongitud());

                        dbRef.child("AtendiendoEmergencia").child(sesUsuario.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task2) {
                                if(task2.isSuccessful()){
                                }
                                else
                                {
                                    Toast.makeText(EmergenciasMedico.this, "Error con la localización", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            }
        };

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        lightSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                lightLevel = event.values[0];
                if (mMap != null) {
                    if (lightLevel < DARK_MAP_THRESHOLD) {

                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(EmergenciasMedico.this, R.raw.dark_style_map));
                    } else {
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(EmergenciasMedico.this, R.raw.light_style_map));
                    }
                }
            }


            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dEmpezarEmergencia = new AlertDialog.Builder(this)
                .setTitle("BUSCAR EMERGENCIAS")
                .setMessage("Quieres empezar a buscar emergencias?")
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dCancelarEmergencia.show();
                    }
                })
                .setPositiveButton(R.string.empezar,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        iniciarEmergencia();
                    }
                })
                .create();
        dEmpezarEmergencia.setOnShowListener(new DialogInterface.OnShowListener() {
            private static final int AUTO_DISMISS_MILLIS = 10000;
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button defaultButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        defaultButton.setText(String.format(
                                Locale.getDefault(), "%s (%d)",
                                getResources().getString(R.string.empezar),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1 //add one so it never displays zero
                        ));
                    }
                    @Override
                    public void onFinish() {
                        if (((AlertDialog) dialog).isShowing()) {
                            defaultButton.setText(R.string.empezar);
                            dialog.dismiss();
                            iniciarEmergencia();
                        }
                    }
                }.start();
            }
        });

        dCancelarEmergencia = new AlertDialog.Builder(this)
                .setTitle("DEJAR DE BUSCAR EMERGENCIAS")
                .setMessage("Quieres dejar de buscar emergencias?")
                .setNegativeButton(R.string.dejar_de_buscar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        cancelarEmergencia();
                    }
                })
                .setPositiveButton(R.string.continuar,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        iniciarEmergencia();
                    }
                })
                .create();
        dCancelarEmergencia.setOnShowListener(new DialogInterface.OnShowListener() {
            private static final int AUTO_DISMISS_MILLIS = 10000;
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button defaultButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        defaultButton.setText(String.format(
                                Locale.getDefault(), "%s (%d)",
                                getResources().getString(R.string.continuar),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1 //add one so it never displays zero
                        ));
                    }
                    @Override
                    public void onFinish() {
                        if (((AlertDialog) dialog).isShowing()) {
                            defaultButton.setText(R.string.continuar);
                            dialog.dismiss();
                            iniciarEmergencia();
                        }
                    }
                }.start();
            }
        });
        dEmpezarEmergencia.show();
    }

    private void cancelarEmergencia() {
        if(ESTADO_INICIADO) {
            ESTADO_INICIADO = false;
            dbRef.child("AtendiendoEmergencia").child(sesUsuario.getUid()).removeValue();
        }
        finish();
    }

    private void iniciarEmergencia() {
        if (!ESTADO_INICIADO) {
            ESTADO_INICIADO = true;
            Toast.makeText(EmergenciasMedico.this, "Búsqueda de mergencias iniciada", Toast.LENGTH_LONG).show();

            dbRef.child("EstadoEmergencia").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                    if(atendiendo.equals("N/A")) {
                        if(dataSnapshot.child("estado").getValue().equals("buscando")) {
                            atendiendo = dataSnapshot.getKey();
                            map.put("estado", "ocupado");
                            map.put("asignado", atendiendo);

                            dbRef.child("EstadoEmergencia").child(atendiendo).child("estado").setValue("atendido");
                            dbRef.child("EstadoEmergencia").child(atendiendo).child("asignado").setValue(sesUsuario.getUid());

                            pacienteEncontrado();
                        }
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                    if(atendiendo.equals("N/A")) {
                        if(dataSnapshot.child("estado").getValue().equals("buscando")) {
                            atendiendo = dataSnapshot.getKey();
                            map.put("estado", "ocupado");
                            map.put("asignado", atendiendo);

                            dbRef.child("EstadoEmergencia").child(atendiendo).child("estado").setValue("atendido");
                            dbRef.child("EstadoEmergencia").child(atendiendo).child("asignado").setValue(sesUsuario.getUid());

                            pacienteEncontrado();
                        }
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) { }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }

            });
        }
    }

    private void pacienteEncontrado() {
        dbRef.child("EstadoEmergencia").child(atendiendo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if(dataSnapshot.child("latitud").exists() && dataSnapshot.child("longitud").exists()) {
                        double pacienteLatitud = Double.parseDouble(dataSnapshot.child("latitud").getValue().toString());
                        double pacienteLongitud = Double.parseDouble(dataSnapshot.child("longitud").getValue().toString());
                        Geocoder mGeocoder = new Geocoder(getBaseContext());
                        paciente.setLatLng(new LatLng(pacienteLatitud, pacienteLongitud));
                        try {
                            paciente.setTexto(mGeocoder.getFromLocation(paciente.getLatitud(), paciente.getLongitud(), 2).get(0).getAddressLine(0));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        reCalculateMarkersAndRoute();
                    }
                }
                else{
                    atendiendo = "N/A";
                    map.put("estado", "libre");
                    map.put("asignado", atendiendo);
                    paciente = new Lugar();
                    dbRef.child("EstadoEmergencia").child(dataSnapshot.getKey()).removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void inflar() {
        boton = (Button) findViewById(R.id.btnEmergenciaPropia);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ESTADO_INICIADO && !atendiendo.equals("N/A")) {
                    dbRef.child("EstadoEmergencia").child(atendiendo).child("estado").setValue("terminado");
                    dbRef.child("EstadoEmergencia").child(atendiendo).child("asignado").setValue("N/A");

                    atendiendo = "N/A";
                    map.put("estado", "libre");
                    map.put("asignado", "N/A");

                    paciente = new Lugar();
                }
            }
        });

    }

    private void showDistance() {

        if (medico.isEmpty()) {
            Toast.makeText(this, "No está activada la localización", Toast.LENGTH_LONG).show();
            return;
        }
        if (paciente.isEmpty()) {
            Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, paciente.getTexto() + " está a " + distance(medico.getLatitud(), medico.getLongitud(), paciente.getLatitud(), paciente.getLongitud()).toString() + " Km", Toast.LENGTH_LONG).show();
    }

    private void reCalculateMarkersAndRoute() {
        if (mMap != null) {
            mMap.clear();

            if(!medico.isEmpty() && !paciente.isEmpty()){
                Log.i("RUTA", "ENTRA");
                String url = getUrl(medico.getLatLng(), paciente.getLatLng(), "driving");
                new FetchURL(EmergenciasMedico.this).execute(url, "driving");
            }

            // Draw current location
            if (!medico.isEmpty())
                mMap.addMarker(new MarkerOptions().position(medico.getLatLng()).title(medico.getTexto()));

            // Draw goal marker
            if (!paciente.isEmpty())
                mMap.addMarker(new MarkerOptions().position(paciente.getLatLng()).title(paciente.getTexto()).icon(BitmapDescriptorFactory.fromResource(R.drawable.sitio)));

        }
    }

    /**
     * Metodo para solicitar un permiso
     *
     * @param context    actividad actual
     * @param permission permiso que se desea solicitar
     * @param just       justificacion para el permiso
     * @param id         identificador con el se marca la solicitud y se captura el callback de respuesta
     */
    public void requestPermission(Activity context, String permission, String just, int id) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                // Show an expanation to the user *asynchronously*
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
        switch (requestCode) {
            case PERMISSION_LOCATION: {
                init();
                break;
            }
        }
    }

    protected void init() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationSettingsRequest.Builder builder = new
                    LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            SettingsClient client = LocationServices.getSettingsClient(this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    startLocationUpdates();
                }
            });
            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case CommonStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                            try {// Show the dialog by calling startResolutionForResult(), andcheck the result in onActivityResult().
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(EmergenciasMedico.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sendEx) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. No way to fix the settings so we won't show the dialog.
                            break;
                    }
                }
            });
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<android.location.Location>() {
                @Override
                public void onSuccess(android.location.Location location) {
                    if (location != null) {
                        lastLocation = location;
                        //Toast.makeText(Location.this, "longitud "+location.getLongitude(), Toast.LENGTH_LONG).show();
                        /*localizacion.setText(
                                "Longitud: "+location.getLongitude()+
                                        "\nLatitud: "+location.getLatitude()+
                                        "\nElevacion: "+location.getAltitude()
                        );
                        distancia.setText("Distancia: "+String.valueOf(distance(LAT1,LONG1,location.getLatitude(),location.getLongitude()))+" km al aeropuerto El Dorado");*/
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS: {
                if (resultCode == RESULT_OK) {
                    startLocationUpdates();
                } else {
                    Toast.makeText(this,
                            "Sin acceso a localización, hardware deshabilitado!",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //tasa de refresco en milisegundos
        mLocationRequest.setFastestInterval(4000); //máxima tasa de refresco
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    public Double distance(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        return Math.round(result * 100.0) / 100.0;
    }

    private void startLocationUpdates() {
        //Verificación de permiso!!
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        sensorManager.unregisterListener(lightSensorListener);
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init(); //Suscribirse a servicios de localización
        sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
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

        if (lightLevel != null && lightLevel < DARK_MAP_THRESHOLD)
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(EmergenciasMedico.this, R.raw.dark_style_map));
        else
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(EmergenciasMedico.this, R.raw.light_style_map));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(LAT1, LONG1)));
        if(ESTADO_INICIADO){
            map.put("latitud", medico.getLatitud());
            map.put("longitud", medico.getLongitud());

            dbRef.child("AtendiendoEmergencia").child(sesUsuario.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task2) {
                    if(task2.isSuccessful()){
                    }
                    else
                    {
                        Toast.makeText(EmergenciasMedico.this, "Error con la localización", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public void onBackPressed() {
        dCancelarEmergencia.show();
    }
}
