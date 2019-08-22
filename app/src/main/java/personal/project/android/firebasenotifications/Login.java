package personal.project.android.firebasenotifications;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    private Button button,signup;
    private EditText mEmail,mPassword;
    private FirebaseAuth mAuth;
    String email,pass;
    FirebaseFirestore firebaseFirestore;
    private FirebaseAuth.AuthStateListener stateListener;
    ProgressBar progressBar;
    Boolean show=false;
    ImageView passv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signup=findViewById(R.id.button);
        button=findViewById(R.id.button2);
        mEmail=findViewById(R.id.editText);
        mPassword=findViewById(R.id.editText2);
        progressBar=findViewById(R.id.progressBar);
        passv=findViewById(R.id.visibility);
        firebaseFirestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        passv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!show){
                    //Show Password
                    mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passv.setImageResource(R.drawable.ic_visibility_black_48dp);
                    show=true;
                }
                else if(show) {
                    //Hide password
                    mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passv.setImageResource(R.drawable.ic_visibility_off_black_48dp);
                    show=false;
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this,SignUp.class);
                startActivity(intent);
            }
        });

        stateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mAuth.getCurrentUser()!=null){

                    Intent intent=new Intent(Login.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(stateListener);
    }

    private void signIn() {
        email = mEmail.getText().toString();
        pass = mPassword.getText().toString();
        if(email.isEmpty() ||pass.isEmpty())
        { if (email.isEmpty()) {
            mEmail.setError("Username cannot be left blank");
            mEmail.requestFocus();
        }
            if(pass.isEmpty()){
                mPassword.setError("Password cannot be left blank");
                mPassword.requestFocus();
            }
        }
        else {

            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete( Task<AuthResult> task) {//Inside The task Variable the results are Stored
                    if (!task.isSuccessful()) {
                        Toast.makeText(Login.this, "Username or Password is Incorrect", Toast.LENGTH_LONG).show();
                    }
                    if(task.isSuccessful()){
                        String token_id= FirebaseInstanceId.getInstance().getToken();  //To get the FCM token Id
                        String cur_id=mAuth.getCurrentUser().getUid();

                        Map<String ,Object> tk=new HashMap<>();
                        assert token_id != null;
                        tk.put("token_id",token_id);
                        firebaseFirestore.collection("users").document(cur_id).update(tk).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

}
