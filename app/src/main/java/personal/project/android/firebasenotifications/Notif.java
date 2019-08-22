package personal.project.android.firebasenotifications;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Notif extends AppCompatActivity {
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);
        String m=getIntent().getStringExtra("message");
        String n=getIntent().getStringExtra("from_id");

        textView=findViewById(R.id.textView3);
        textView.setText("From "+n+" "+m);
    }
}
