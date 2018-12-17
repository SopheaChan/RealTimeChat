package com.example.dell.chatapp.activity_class;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.chatapp.R;
import com.example.dell.chatapp.activity_class.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.security.Provider;
import java.util.ArrayList;
import java.util.List;

public class LogInActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private final int rc_sign_in = 1;
    private EditText etEmail, etPassword;
    private ProgressBar progressBar;
    private TextView loginProgressText, loginFailText, loginFailHelper;
    private SignInButton googleButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        etEmail = findViewById(R.id.edit_text_email);
        etPassword = findViewById(R.id.edit_text_password);
        progressBar = findViewById(R.id.login_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        loginProgressText = findViewById(R.id.login_progress_message);
        loginFailText = findViewById(R.id.login_fail_text);
        loginFailText.setVisibility(View.INVISIBLE);
        loginFailHelper = findViewById(R.id.log_in_fail_help_text);
        loginFailHelper.setVisibility(View.INVISIBLE);
        googleButton = findViewById(R.id.button_log_in_with_google_account);
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithGoogleAccount();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.api_key))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);


        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(this,ContactListActivity.class));
        }
    }

    private void loginWithGoogleAccount() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, rc_sign_in);
        loginFailHelper.setVisibility(View.INVISIBLE);
        loginFailText.setVisibility(View.INVISIBLE);
    }

    public void loginOnClick(View v){
        loginFailHelper.setVisibility(View.INVISIBLE);
        loginFailText.setVisibility(View.INVISIBLE);
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (!email.isEmpty() && !password.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            loginProgressText.setText("Signing in...");
            email = etEmail.getText().toString().trim();
            password = etPassword.getText().toString().trim();
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                Intent intent = new Intent(LogInActivity.this, ContactListActivity.class);
                                clearEditTextBoxBeforeLeaving();
                                startActivity(intent);
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                loginFailText.setText("Incorrect username or password");
                                loginFailText.setVisibility(View.VISIBLE);
                                loginProgressText.setVisibility(View.INVISIBLE);
                                loginFailHelper.setText("Help?");
                                loginFailHelper.setVisibility(View.VISIBLE);
                                loginFailHelper.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        new AlertDialog.Builder(LogInActivity.this)
                                                .setTitle("Login error:")
                                                .setMessage("To be able to log in with your email and password, " +
                                                        "please note that: " +
                                                        "\n"+"1. Make sure you have completed all the required data(email, password)" +
                                                        "\n"+"2. Make sure that you're using a valid account(the email and password which" +
                                                        " have been already registered with our database, if not click on Sign up button below.)" +
                                                        "\n"+"3. Make sure that you provided the correct data of your account log in information")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.cancel();
                                                    }
                                                })
                                                .show();
                                    }
                                });
                            }
                        }
                    });
        }
        else {
            loginFailText.setText("Please input your email and password");
            loginFailText.setVisibility(View.VISIBLE);
        }
    }
    public void signUpOnClicked(View view){
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(LogInActivity.this, CreateNewAccountActivity.class);
        clearEditTextBoxBeforeLeaving();
        startActivity(intent);
        progressBar.setVisibility(View.INVISIBLE);
    }
    private void clearEditTextBoxBeforeLeaving(){
        etEmail.setText("");
        etEmail.requestFocus();
        etPassword.setText("");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        progressBar.setVisibility(View.VISIBLE);
        loginProgressText.setText("Syncing google account...");
        loginProgressText.setVisibility(View.VISIBLE);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == rc_sign_in){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);
            }catch (ApiException e){
                Log.e("ERROR", "onActivityResult: " +  e.getMessage());
            }
        }
    }
    private void saveUserNameToDataBase(String username, String email) {
        progressBar.setVisibility(View.VISIBLE);
        loginFailText.setText("Saving data to database");
        loginProgressText.setVisibility(View.VISIBLE);
        final String userID = firebaseAuth.getUid();
        User usernameAndID = new User(userID, username, email,"https://avatars3.githubusercontent.com/u/66577?s=200&v=4");
        FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(userID)
                .setValue(usernameAndID)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.INVISIBLE);
                        loginProgressText.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        loginProgressText.setVisibility(View.INVISIBLE);
                        loginFailHelper.setText("Failed to save user data");
                        loginFailText.setVisibility(View.VISIBLE);
                    }
                });
    }

    public void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        final String id = firebaseAuth.getUid();
        final String email = String.valueOf(firebaseAuth.getCurrentUser());
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(LogInActivity.this, ContactListActivity.class));
                        progressBar.setVisibility(View.INVISIBLE);
                        loginProgressText.setVisibility(View.INVISIBLE);
                        clearEditTextBoxBeforeLeaving();
                        saveUserNameToDataBase("Guest"+id,email);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loginFailText.setText("Fail to sign in with Google account");
                    }
                });
    }
}
