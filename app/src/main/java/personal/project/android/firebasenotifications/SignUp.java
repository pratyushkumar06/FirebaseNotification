package personal.project.android.firebasenotifications;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    EditText email,pass,namee;
    String username,password,name,user_id;
    TextView textView;
    Button button;
    FirebaseFirestore firebaseFirestore;
    Uri resulturi,url;
    Boolean show=false;
    ImageView passv;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        textView=findViewById(R.id.textView2);
        email=findViewById(R.id.editText);
        pass=findViewById(R.id.editText2);
        passv=findViewById(R.id.visibility);
        button=findViewById(R.id.button);
        firebaseFirestore=FirebaseFirestore.getInstance();
        progressBar=findViewById(R.id.progressBar2);
        mAuth=FirebaseAuth.getInstance();
        passv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!show){
                    //Show Password
                    pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passv.setImageResource(R.drawable.ic_visibility_black_48dp);
                    show=true;
                }
                else if(show) {
                    //Hide password
                    pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passv.setImageResource(R.drawable.ic_visibility_off_black_48dp);
                    show=false;
                }
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        username=email.getText().toString();
        password=pass.getText().toString();
        if(username.isEmpty() ||password.isEmpty()|| password.length()<6)
        { if (username.isEmpty()) {
            email.setError("Username cannot be left blank");
            email.requestFocus();
            return;
        }
            if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
                email.setError("Enter a Valid Email");
                email.requestFocus();
                return;
            }
            if(password.isEmpty() || password.length()<6){
                pass.setError("Minimum Length of password should be 6 charecters");
                pass.requestFocus();
                return;
            }


        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
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
                        finish();
                        Toast.makeText(SignUp.this,"Registered Successfully",Toast.LENGTH_LONG).show();
                    }
                    else {
                        if(task.getException() instanceof FirebaseAuthUserCollisionException){
                            Toast.makeText(SignUp.this,"Email id Already Registered",Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(SignUp.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

    }





}
