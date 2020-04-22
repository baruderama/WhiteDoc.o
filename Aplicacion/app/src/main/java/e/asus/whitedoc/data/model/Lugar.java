package e.asus.whitedoc.data.model;

import com.google.android.gms.maps.model.LatLng;

public class Lugar {
    private Double latitud;
    private Double longitud;
    private String texto;

    public Lugar() {
        latitud = null;
        longitud = null;
        texto = null;
    }

    public Lugar(LatLng latLng, String texto) {
        setLatLng(latLng);
        this.texto = texto;
    }

    public Lugar(String texto) {
        latitud = null;
        longitud = null;
        this.texto = texto;
    }

    public Lugar(double latitud, double longitud, String texto) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.texto = texto;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String getTexto() {
        return texto;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void clear() {
        texto = null;
        longitud = null;
        latitud = null;
    }

    public boolean isEmpty() {
        return longitud == null || latitud == null || texto == null;
    }

    public void setLatLng(LatLng latLng) {
        longitud = latLng.longitude;
        latitud = latLng.latitude;
    }

    public LatLng getLatLng() {
        return new LatLng(latitud, longitud);
    }
}
