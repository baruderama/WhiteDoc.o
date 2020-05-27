package e.asus.whitedoc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import e.asus.whitedoc.helper.Utils;
import static e.asus.whitedoc.NotificationChannel1.CHANNEL_ID;

public class PantallaPrincipalUsuario extends AppCompatActivity {

    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal_usuario);
        notificationManager= NotificationManagerCompat.from(this);
    }

    public void programarCita(View v) {

        Intent intent= new Intent(this, Chat.class);
        PendingIntent contentIntent= PendingIntent.getActivity(this,0,intent,0);

        Notification notification=new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_chat_black_24dp)
                .setContentTitle("Chat")
                .setContentText("prueba nuestro nuevo chat :D")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.RED)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();

        notificationManager.notify(1,notification);
        Intent pantallaBuscar = new Intent(getApplicationContext(), ActividadProgramarCita.class);
        startActivity(pantallaBuscar);
    }

    public void verCitas(View v) {
        Intent intent= new Intent(this, Chat.class);
        PendingIntent contentIntent= PendingIntent.getActivity(this,0,intent,0);

        Notification notification=new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_chat_black_24dp)
                .setContentTitle("Chat")
                .setContentText("prueba nuestro nuevo chat :D")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.RED)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();

        notificationManager.notify(1,notification);
        Intent pantallaPersona = new Intent(getApplicationContext(), ConsultarCitasPersona.class);
        startActivity(pantallaPersona);
    }

    public void modificarMedicamentos(View v) {
        Intent intent= new Intent(this, Chat.class);
        PendingIntent contentIntent= PendingIntent.getActivity(this,0,intent,0);

        Notification notification=new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_chat_black_24dp)
                .setContentTitle("Chat")
                .setContentText("prueba nuestro nuevo chat :D")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.RED)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();

        notificationManager.notify(1,notification);
        
        Intent pantallaListaMedicamentos = new Intent(getApplicationContext(), MedicamentosPersona.class);
        startActivity(pantallaListaMedicamentos);
    }

    public void verPerfil(View v) {
        Intent intent= new Intent(this, Chat.class);
        PendingIntent contentIntent= PendingIntent.getActivity(this,0,intent,0);

        Notification notification=new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_chat_black_24dp)
                .setContentTitle("Chat")
                .setContentText("prueba nuestro nuevo chat :D")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.RED)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();

        notificationManager.notify(1,notification);

        Intent pantallaPerfil = new Intent(getApplicationContext(), PerfilPaciente.class);
        startActivity(pantallaPerfil);
    }

    public void emergencia(View v) {
        Intent pantallaEmergencia = new Intent(getApplicationContext(), EmergenciaUsuario.class);
        startActivity(pantallaEmergencia);
    }


    public void verDoctores(View view) {
        Intent pantallaDoctores = new Intent(getApplicationContext(), Chat.class);
        startActivity(pantallaDoctores);
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Utils.eraseFile(getBaseContext());
        Utils.eliminarTipoUsuario(getBaseContext());
        Intent pantallaLogIn = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(pantallaLogIn);
        finish();
    }
}
