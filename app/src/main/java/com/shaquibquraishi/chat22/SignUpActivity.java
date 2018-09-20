package com.shaquibquraishi.chat22;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private ImageView profilePic;
    private Button signUp;
    private TextView login;
    private ConstraintLayout constraintLayout;
    private TextView logo;
    private FirebaseAuth mAuth;
    private String dispalyName;
    String profileImageUrl;
    private Uri profileUri;
    ProgressBar progressBar;
    @Override
    public void onClick(View v) {
        int id=v.getId();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(inputMethodManager!=null) {
            if (id == constraintLayout.getId()||id==logo.getId() && inputMethodManager.isActive()) {
                // InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
            }
        }
        if(id==signUp.getId()){
            String firstname=firstName.getText().toString().trim();
            String lastname=lastName.getText().toString().trim();
            String emails=email.getText().toString().trim();
            String pass=password.getText().toString().trim();

            if(firstname.isEmpty()){
                firstName.setError("First Name is required!");
                firstName.requestFocus();
                return;
            }
            if(lastname.isEmpty()){
                lastName.setError("First Name is required!");
                lastName.requestFocus();
                return;
            }
            if(emails.isEmpty()){
                email.setError("First Name is required!");
                email.requestFocus();
                return;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(emails).matches()){
                email.setError("Please enter a valid email");
                email.requestFocus();
                return;

            }
            if(pass.isEmpty()){
                password.setError("Password is required");
                password.requestFocus();
                return;
            }

            if(pass.length()<6){
                password.setError("Minimum length of password id 6");
                password.requestFocus();
                return;

            }
            dispalyName=firstname+" "+lastname;
            createAccount(emails,pass);




        }
        if(id==login.getId()){
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        if(id==profilePic.getId()){
            showImageChooser();
        }


    }

    private void showImageChooser() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Image"),1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK &&data!=null&&data.getData()!=null){
            profileUri=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),profileUri);
                profilePic.setImageBitmap(bitmap);
                //uploadImageToFirebase();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
    private void uploadImageToFirebase() {
        final StorageReference profileImageRef= FirebaseStorage.getInstance().getReference("profilepics/"+System.currentTimeMillis()+".jpg");
        if(profileUri!=null){
           // progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(profileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   // progressBar.setVisibility(View.GONE);
                    profileImageUrl=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();


                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                           // progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("info", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            uploadImageToFirebase();
                            if(dispalyName!=null&&profileImageUrl!=null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(dispalyName)
                                        .setPhotoUri(Uri.parse(profileImageUrl))
                                        .build();

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.i("info", "User profile updated.");
                                                }
                                            }
                                        });
                            }
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("info", "createUserWithEmail:failure"+ task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });

    }
    private  void updateUI(FirebaseUser user){
        if(user!=null){
            Intent intent=new Intent(getApplicationContext(),ChatActivity.class);
            startActivity(intent);
            finish();

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firstName=findViewById(R.id.editTextFirstNameSignup);
        lastName=findViewById(R.id.editTextLastNameSignup);
        email=findViewById(R.id.editTextEmailSignup);
        password=findViewById(R.id.editTextPasswordSignup);
        confirmPassword=findViewById(R.id.editTextConfirmPasswordSignup);
        profilePic=findViewById(R.id.imageViewProfilePic);
        signUp=findViewById(R.id.buttonCreateAccountSignup);
        login=findViewById(R.id.textViewLogin);
        constraintLayout=findViewById(R.id.constraintLayoutSignup);
        logo=findViewById(R.id.textViewLogoSignup);
        progressBar=findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        profileImageUrl="https://materialdesignicons.com/api/download/icon/png/E76EC23F-AB71-49B3-9173-841544527A20/48/FFFFFF/1/000000/0/0/0/account";

        constraintLayout.setOnClickListener(this);
        logo.setOnClickListener(this);
        signUp.setOnClickListener(this);
        login.setOnClickListener(this);
        profilePic.setOnClickListener(this);
        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!password.getText().toString().trim().matches(confirmPassword.getText().toString().trim())){
                    confirmPassword.setError("Password not matched");
                    confirmPassword.requestFocus();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!password.getText().toString().trim().matches(confirmPassword.getText().toString().trim())){
                    confirmPassword.setError("Password not matched");
                    confirmPassword.requestFocus();

                }


            }
        });
    }


}
