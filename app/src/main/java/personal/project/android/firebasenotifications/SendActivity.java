package personal.project.android.firebasenotifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SendActivity extends AppCompatActivity {
    private TextView textView;
    private String user_id,currid,uname;
    private EditText editText;
    private FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    private Button send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        textView=findViewById(R.id.uid);
        user_id=getIntent().getStringExtra("UID");
        uname=getIntent().getStringExtra("Name");
        send=findViewById(R.id.send);
        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(SendActivity.this);
        editText=findViewById(R.id.message);
        textView.setText("Sending to "+uname);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        currid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ms=editText.getText().toString();
                if(ms.isEmpty()){
                    editText.setError("Cannot be Blank");
                    editText.requestFocus();
                }
                else {
                    progressDialog.setTitle("Sending");
                    progressDialog.show();
                    Map<String ,Object> notifs=new HashMap<>();
                    notifs.put("message",ms);
                    notifs.put("id",currid);
                    notifs.put("timestamp", FieldValue.serverTimestamp()); //TODO
                    firestore.collection("users").document(user_id).collection("Notifications").add(notifs).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            progressDialog.dismiss();
                            editText.setText("");
                            Toast.makeText(SendActivity.this,"Notification Sent",Toast.LENGTH_SHORT).show();
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SendActivity.this,"Error",Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });

    }
}
