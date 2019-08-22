package personal.project.android.firebasenotifications;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private Button button,save;
    private FirebaseAuth auth;
    ProgressDialog getProgressDialog,progressDialog;
    Uri resulturi,url;
    Bitmap bitmap;
    private String user_id,name,cuid;
    private FirebaseAuth mAuth;
    private EditText uname;
    private ImageView imageView;
    private int INT_CONST=393;
    FirebaseFirestore firebaseFirestore;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment
        auth=FirebaseAuth.getInstance();
        button=v.findViewById(R.id.button3);
        firebaseFirestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(getContext());
        imageView=v.findViewById(R.id.image);
        save=v.findViewById(R.id.button5);
        uname=v.findViewById(R.id.un);
        getProgressDialog=new ProgressDialog(getContext());
        mAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        loadUserInfo();
        if(auth.getCurrentUser()!=null)
        cuid=auth.getCurrentUser().getUid();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) getContext(),new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},33);
            }
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=uname.getText().toString();
                if(name.isEmpty()){
                    uname.setError("Cannot be Empty");
                    uname.requestFocus();
                }
                else{
                saveUserInfo();
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String ,Object> tokenremove=new HashMap<>();
                tokenremove.put("token_id", "");
                firebaseFirestore.collection("users").document(cuid).update(tokenremove).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
                auth.signOut();
                Intent in=new Intent(getContext(),Login.class);
                startActivity(in);
                getActivity().finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Profile Picture"),INT_CONST);//Create Image Chooser Method
            }
        });



        return v;
    }

    @SuppressLint("SetTextI18n")
    private void loadUserInfo() {   //We use Glide to Load the Image
        final FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(getContext()).load(user.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(imageView);
            }
            if (user.getDisplayName() != null) {
                String displayname = user.getDisplayName();
                uname.setText(displayname);
            }
        }
    }


    private void saveUserInfo() {

        progressDialog.setTitle("Updating Details..");
        progressDialog.show();

        final FirebaseUser user=mAuth.getCurrentUser();   //We get the current user
        if(mAuth.getCurrentUser()!=null) {
            user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        if(user!=null && url!=null){

            UserProfileChangeRequest request=new UserProfileChangeRequest.Builder().setDisplayName(name).setPhotoUri(url).build();
            user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    if(task.isSuccessful()){
                        Map<String ,String> u=new HashMap<>();
                        u.put("name",name);
                        u.put("url",url.toString());
                        firebaseFirestore.collection("users").document(user_id)
                                .set(u)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        Toast.makeText(getContext(),"Profile Updated",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else if(user!=null && url==null){
            UserProfileChangeRequest request=new UserProfileChangeRequest.Builder().setDisplayName(name).build();
            user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    if(task.isSuccessful()){
                        Map<String ,String> u=new HashMap<>();
                        u.put("name",name);
                        u.put("url",user.getPhotoUrl().toString());

                        firebaseFirestore.collection("users").document(user_id)
                                .set(u)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                        Toast.makeText(getContext(),"Profile Updated",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {  //To get the result of the Image Chosen
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==INT_CONST && resultCode==RESULT_OK && data!=null && data.getData()!=null){ //see if the code matches and result is ok and data is not null

            resulturi= data.getData();         //Get Image Uri
            try {

                bitmap= MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),resulturi);  //TODO resolve error
                imageView.setImageBitmap(bitmap);
                //uploadtoStorage();
                uploadFile(bitmap);
                getProgressDialog.setTitle("Uploading Image");
                getProgressDialog.show();


            } catch (IOException e) {
                e.printStackTrace();
            }



        }

    }


    private void uploadFile(Bitmap bitmap) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();


        final StorageReference ImagesRef = storageRef.child("images").child(resulturi.getLastPathSegment());


        //TODO compressed thumbnail post image for loading fast

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = ImagesRef.putBytes(data);



        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("Error:",exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.i("problem", task.getException().toString());
                        }

                        return ImagesRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                               getProgressDialog.dismiss();
                            Toast.makeText(getContext(),"Upload Successfull",Toast.LENGTH_SHORT).show();
                            url=downloadUri;


                            //StorageReference ref = FirebaseStorage.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());

                            assert downloadUri != null;
                            Log.i("seeThisUri", downloadUri.toString());// This is the one you should store

                            //ref.child("imageURL").setValue(downloadUri.toString());


                        } else {
                             getProgressDialog.dismiss();
                            Log.i("wentWrong","downloadUri failure");
                        }
                    }
                });
            }
        });

    }
}
