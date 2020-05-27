package e.asus.whitedoc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import e.asus.whitedoc.helper.Utils;
import static e.asus.whitedoc.NotificationChannel1.CHANNEL_ID;

public class PantallaPrincipalMedico extends AppCompatActivity {
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal_medico);
        notificationManager= NotificationManagerCompat.from(this);
    }
    public void actividadCitasProgramadas(View v) {
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
        /*Intent pantallaCalendario = new Intent(getApplicationContext(), DisponibilidadMedico.class);
        startActivity(pantallaCalendario);*/
        Toast.makeText(this, "Aun no hay pantalla para citas programadas", Toast.LENGTH_SHORT).show();
    }

    public void actividadPerfilMedico(View v) {
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
        Intent pantallaperfil = new Intent(getApplicationContext(), PerfilMedico.class);
        startActivity(pantallaperfil);
    }

    public void actividadPacientes(View v) {

        Intent pantallaPaciente = new Intent(getApplicationContext(), Chat.class);
        startActivity(pantallaPaciente);
    }
    public void actividadAceptarCitas(View v) {
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
        Intent pantallaAceptarCitas = new Intent(getApplicationContext(), AceptarRechazarCitas.class);
        startActivity(pantallaAceptarCitas);
    }
    public void actividadHorarioAtencion(View v) {
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
        Intent pantallaHorarioAtencion = new Intent(getApplicationContext(), DisponibilidadMedico.class);
        startActivity(pantallaHorarioAtencion);
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Utils.eraseFile(getBaseContext());
        Utils.eliminarTipoUsuario(getBaseContext());
        Intent pantallaLogIn = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(pantallaLogIn);
        finish();
    }

    public void atenderEmergencias(View view) {
        Intent pantallaEmergenciaMedico = new Intent(getApplicationContext(), EmergenciasMedico.class);
        startActivity(pantallaEmergenciaMedico);
    }
}
