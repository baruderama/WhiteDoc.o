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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import e.asus.whitedoc.data.model.Lugar;

public class EmergenciaUsuario extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Lugar posActual;
    private Lugar posObjetivo;
    private EditText campoBusqueda;
    private boolean firstDraw;
    private Float lightLevel;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencia_usuario);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        firstDraw = true;
        posActual = new Lugar();
        posObjetivo = new Lugar();
        inflar();

        requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, "Es necesario para acceder a la ubicacion", PERMISSION_LOCATION);
        mLocationRequest = createLocationRequest();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                android.location.Location location = locationResult.getLastLocation();
                lastLocation = location;
                if (location != null) {
                    posActual.setLatitud(location.getLatitude());
                    posActual.setLongitud(location.getLongitude());
                    Geocoder mGeocoder = new Geocoder(getBaseContext());
                    try {
                        posActual.setTexto(mGeocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 2).get(0).getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    redrawMarkers();
                    if (firstDraw && mMap != null) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(posActual.getLatLng()));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                        firstDraw = false;
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
                        Log.i("MAPS", "DARK MAP " + lightLevel);
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(EmergenciaUsuario.this, R.raw.dark_style_map));
                    } else {
                        Log.i("MAPS", "LIGHT MAP " + lightLevel);
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(EmergenciaUsuario.this, R.raw.light_style_map));
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
                .setTitle("Creando un llamado de emergencia")
                .setMessage("Quieres empezar el llamado de mergencia?")
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        dCancelarEmergencia.show();
                    }
                })
                .setPositiveButton("EMPEZAR",  new DialogInterface.OnClickListener() {
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
                final CharSequence positiveButtonText = defaultButton.getText();
                new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        defaultButton.setText(String.format(
                                Locale.getDefault(), "%s (%d)",
                                positiveButtonText,
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1 //add one so it never displays zero
                        ));
                    }
                    @Override
                    public void onFinish() {
                        if (((AlertDialog) dialog).isShowing()) {
                            dialog.dismiss();
                            iniciarEmergencia();
                        }
                    }
                }.start();
            }
        });

        dCancelarEmergencia = new AlertDialog.Builder(this)
                .setTitle("Cancelando llamado de emergencia")
                .setMessage("Quieres cancelar el llamado de mergencia?")
                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        cancelarEmergencia();
                    }
                })
                .setPositiveButton("CONTINUAR",  new DialogInterface.OnClickListener() {
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
                final CharSequence positiveButtonText = defaultButton.getText();
                new CountDownTimer(AUTO_DISMISS_MILLIS, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        defaultButton.setText(String.format(
                                Locale.getDefault(), "%s (%d)",
                                positiveButtonText,
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1 //add one so it never displays zero
                        ));
                    }
                    @Override
                    public void onFinish() {
                        if (((AlertDialog) dialog).isShowing()) {
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
        finish();
    }

    private void iniciarEmergencia() {
        Toast.makeText(this, "Emergencia iniciada", Toast.LENGTH_LONG).show();
    }

    private void inflar() {
    }

    private void showDistance() {

        if (posActual.isEmpty()) {
            Toast.makeText(this, "No está activada la localización", Toast.LENGTH_LONG).show();
            return;
        }
        if (posObjetivo.isEmpty()) {
            Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, posObjetivo.getTexto() + " está a " + distance(posActual.getLatitud(), posActual.getLongitud(), posObjetivo.getLatitud(), posObjetivo.getLongitud()).toString() + " Km", Toast.LENGTH_LONG).show();
    }

    private void redrawMarkers() {
        if (mMap != null) {
            mMap.clear();

            // Draw current location
            if (!posActual.isEmpty())
                mMap.addMarker(new MarkerOptions().position(posActual.getLatLng()).title(posActual.getTexto()));

            // Draw goal marker
            if (!posObjetivo.isEmpty())
                mMap.addMarker(new MarkerOptions().position(posObjetivo.getLatLng()).title(posObjetivo.getTexto()).icon(BitmapDescriptorFactory.fromResource(R.drawable.doctor)));
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
                                resolvable.startResolutionForResult(EmergenciaUsuario.this, REQUEST_CHECK_SETTINGS);
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
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS: {
                if (resultCode == RESULT_OK) {
                    startLocationUpdates();
                } else {
                    Toast.makeText(this,
                            "Sin accesoa localización, hardware deshabilitado!",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000); //tasa de refresco en milisegundos
        mLocationRequest.setFastestInterval(5000); //máxima tasa de refresco
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

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                posObjetivo.setLatLng(latLng);
                Geocoder mGeocoder = new Geocoder(getBaseContext());
                try {
                    posObjetivo.setTexto(mGeocoder.getFromLocation(posObjetivo.getLatitud(), posObjetivo.getLongitud(), 2).get(0).getAddressLine(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                redrawMarkers();
                showDistance();
            }
        });
        if (lightLevel != null && lightLevel < DARK_MAP_THRESHOLD)
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(EmergenciaUsuario.this, R.raw.dark_style_map));
        else
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(EmergenciaUsuario.this, R.raw.light_style_map));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(LAT1, LONG1)));
    }
}
