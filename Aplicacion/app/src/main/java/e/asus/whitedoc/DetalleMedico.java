package e.asus.whitedoc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DetalleMedico extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_medico);


    }

    public void pantallaChat(View v) {
        Intent pantallaChat = new Intent(getApplicationContext(), Chat.class);
        startActivity(pantallaChat);
    }
}
