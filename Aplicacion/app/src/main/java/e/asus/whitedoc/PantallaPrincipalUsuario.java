package e.asus.whitedoc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PantallaPrincipalUsuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal_usuario);
    }

    public void solcitarMedico(View v) {
        Intent pantallaBuscar = new Intent(getApplicationContext(), BusquedaMedico.class);
        startActivity(pantallaBuscar);
    }

    public void verSalud(View v) {
        Intent pantallaPersona = new Intent(getApplicationContext(), DetallePersona.class);
        startActivity(pantallaPersona);
    }

    public void agregarMedicamento(View v) {
        Intent pantallaAgregar = new Intent(getApplicationContext(), AgregarMedicamento.class);
        startActivity(pantallaAgregar);
    }


}
