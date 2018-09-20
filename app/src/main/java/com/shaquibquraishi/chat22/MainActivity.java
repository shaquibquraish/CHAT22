package com.shaquibquraishi.chat22;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ConstraintLayout constraintLayoutLogin;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonLoginWithFb;
    private TextView forgotPassword;
    private TextView signup;
    private FirebaseAuth mAuth;

    @Override
    public void onClick(View v) {
        int id=v.getId();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(inputMethodManager!=null) {
            if (id == constraintLayoutLogin.getId() && inputMethodManager.isActive()) {
                // InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
            }
        }
        if(id==buttonLogin.getId()){
            String email=editTextEmail.getText().toString().trim();
            String password=editTextPassword.getText().toString().trim();
            if(email.isEmpty()){
                editTextEmail.setError("Email is required");
                editTextEmail.requestFocus();
                return;
            }
           if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                editTextEmail.setError("Please enter a valid email");
                editTextEmail.requestFocus();
                return;

            }
            if(password.isEmpty()){
                editTextPassword.setError("Password is required");
                editTextPassword.requestFocus();
                return;
            }

            if(password.length()<6){
                editTextPassword.setError("Minimum length of password id 6");
                editTextPassword.requestFocus();
                return;

            }
            signInUser(email,password);


        }
        if(id==forgotPassword.getId()){
            return;

        }
        if(id==signup.getId()){
            Intent intent=new Intent(getApplicationContext(),SignUpActivity.class);
            startActivity(intent);
            finish();


        }

    }

    private void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("info", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("info", "signInWithEmail:failure"+ task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    public void updateUI(FirebaseUser user){
        if(user!=null){
            Intent intent=new Intent(getApplicationContext(),ChatActivity.class);
            startActivity(intent);
            finish();

        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        constraintLayoutLogin=findViewById(R.id.constraintLayoutLogin);
        editTextEmail=findViewById(R.id.editTextEmail);
        editTextPassword=findViewById(R.id.editTextPassword);
        buttonLogin=findViewById(R.id.buttonLogin);
        buttonLoginWithFb=findViewById(R.id.buttonFbSignIn);
        forgotPassword=findViewById(R.id.textViewForgotPass);
        signup=findViewById(R.id.textViewSignup);
        mAuth = FirebaseAuth.getInstance();
        constraintLayoutLogin.setOnClickListener(this);
        buttonLogin.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        signup.setOnClickListener(this);

    }

}
