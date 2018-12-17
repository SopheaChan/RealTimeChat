package com.example.dell.chatapp.activity_class;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateNewAccountActivity extends AppCompatActivity {
    private EditText etSignUpEmail, etSignUpPassword, etUsername;
    private TextView signUpFailedText, signUpProgressText, signUpFailHelper;
    private ProgressBar signUpProgressBar;
    private SignInButton googleSingIn;
    
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private DatabaseReference databaseReference;
    private final int rc_sign_in = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);
        etSignUpEmail = findViewById(R.id.email_sign_up);
        etSignUpPassword = findViewById(R.id.password_sign_up);
        etUsername = findViewById(R.id.username_sign_up);
        signUpFailedText = findViewById(R.id.sign_up_fail_text);
        signUpFailedText.setVisibility(View.INVISIBLE);
        signUpProgressText = findViewById(R.id.sign_up_progress_message);
        signUpProgressText.setVisibility(View.INVISIBLE);
        signUpFailHelper = findViewById(R.id.sign_up_fail_help_text);
        signUpFailHelper.setVisibility(View.INVISIBLE);
        signUpProgressBar = findViewById(R.id.sign_up_progress_bar);
        signUpProgressBar.setVisibility(View.INVISIBLE);
        googleSingIn = findViewById(R.id.button_sign_up_with_google_account);

        firebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.api_key))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithGoogleAccount();
            }
        });
    }
    private void loginWithGoogleAccount() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, rc_sign_in);
        signUpFailHelper.setVisibility(View.INVISIBLE);
        signUpFailHelper.setVisibility(View.INVISIBLE);
    }
    public void signUpOnClicked(View view){
        signUpFailHelper.setVisibility(View.INVISIBLE);
        signUpFailedText.setVisibility(View.INVISIBLE);
        final String username = etUsername.getText().toString().trim();
        final String email = etSignUpEmail.getText().toString().trim();
        String password = etSignUpPassword.getText().toString().trim();
        if (!email.isEmpty() && !password.isEmpty() && !username.isEmpty()){
            signUpProgressBar.setVisibility(View.VISIBLE);
            signUpProgressText.setText("Signing up...");
            signUpProgressText.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                saveUserNameToDataBase(username, email);
                                signUpProgressBar.setVisibility(View.INVISIBLE);
                                signUpProgressText.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(CreateNewAccountActivity.this, ContactListActivity.class));
                                finish();
                            }
                            else {
                                    signUpProgressBar.setVisibility(View.INVISIBLE);
                                    signUpProgressText.setVisibility(View.INVISIBLE);
                                    signUpFailedText.setText("Sign up failed.");
                                    signUpFailHelper.setText("Help?");
                                    signUpFailedText.setVisibility(View.VISIBLE);
                                    signUpFailHelper.setVisibility(View.VISIBLE);
                                    signUpFailHelper.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new AlertDialog.Builder(CreateNewAccountActivity.this)
                                                    .setTitle("Sign up error:")
                                                    .setMessage("To be able to sign up with your email and password," +
                                                            " please note that:" +
                                                            "\n"+"1. Input all the required data(username, email, password)" +
                                                            "\n"+"2. Make sure you input the correct form of the standard email" +
                                                            "(eg. silentmonster@gmail.com)" +
                                                            "\n"+"3. Remember that the password must contain any character or number " +
                                                            "with at least 6 characters long" +
                                                            "\n"+"4. The other case of the failure might cause by " +
                                                            "the already existence of the email in the user account database.")
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
            signUpProgressBar.setVisibility(View.INVISIBLE);
            signUpProgressText.setVisibility(View.INVISIBLE);
            if (username.isEmpty() && !email.isEmpty() && !password.isEmpty()){
                etUsername.setHintTextColor(getResources().getColor(R.color.login_fail));
                signUpFailedText.setText("Please input your "+etUsername.getHint().toString());
                signUpFailedText.setVisibility(View.VISIBLE);
                etUsername.requestFocus();
            }
            else if (email.isEmpty() && !username.isEmpty() && !password.isEmpty()){
                //etSignUpEmail.setHintTextColor(getResources().getColor(R.color.login_fail));
                signUpFailedText.setText("Please input your "+etSignUpEmail.getHint().toString());
                signUpFailedText.setVisibility(View.VISIBLE);
                etSignUpEmail.requestFocus();
            }
            else if (password.isEmpty() && !username.isEmpty() && !email.isEmpty()){
                //etSignUpPassword.setHintTextColor(getResources().getColor(R.color.login_fail));
                signUpFailedText.setText("Please input your "+etSignUpPassword.getHint().toString());
                signUpFailedText.setVisibility(View.VISIBLE);
                etSignUpPassword.requestFocus();
            }
        }
    }

    private void saveUserNameToDataBase(String username, String email) {
        signUpProgressBar.setVisibility(View.VISIBLE);
        signUpFailedText.setText("Saving data to database");
        signUpProgressText.setVisibility(View.VISIBLE);
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
                        signUpProgressBar.setVisibility(View.INVISIBLE);
                        signUpProgressText.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        signUpProgressBar.setVisibility(View.INVISIBLE);
                        signUpProgressText.setVisibility(View.INVISIBLE);
                        signUpFailedText.setText("Failed to save user data");
                        signUpFailedText.setVisibility(View.VISIBLE);
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        signUpProgressBar.setVisibility(View.VISIBLE);
        signUpProgressText.setText("Syncing google account...");
        signUpProgressText.setVisibility(View.VISIBLE);
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


    public void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String id = firebaseAuth.getUid();
                        String email = String.valueOf(firebaseAuth.getCurrentUser());
                        startActivity(new Intent(CreateNewAccountActivity.this, ContactListActivity.class));
                        finish();
                        signUpProgressBar.setVisibility(View.INVISIBLE);
                        signUpProgressText.setVisibility(View.INVISIBLE);
                        saveUserNameToDataBase("Guess"+id, email);
                        clearEditTextBoxBeforeLeaving();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        signUpFailedText.setText("Fail to sign in with Google account");
                    }
                });
    }
    private void clearEditTextBoxBeforeLeaving(){
        etUsername.setText("");
        etUsername.requestFocus();
        etSignUpEmail.setText("");
        etSignUpPassword.setText("");
    }
}
