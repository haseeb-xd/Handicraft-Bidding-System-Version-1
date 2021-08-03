package com.haseebahmed.handicraftbiddingsystemversion1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.w3c.dom.Text;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{

    private EditText emailLogin,passwordLogin;
    private CircularProgressButton buttonLogin;
    private FirebaseAuth mAuth;
    private TextView forgetpasswordtext;
    private  GoogleSignInClient mGoogleSignInClient;
    private ImageView googleSignInImage;
    private final static int RC_SIGN_IN=3232;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_login);
        emailLogin= (EditText) findViewById(R.id.emailLogin);
        passwordLogin= (EditText) findViewById(R.id.passwordLogin);
        buttonLogin= (CircularProgressButton) findViewById(R.id.loginButton);
        forgetpasswordtext= (TextView) findViewById(R.id.forget_password_text);
        googleSignInImage= (ImageView) findViewById(R.id.google_signin_img);


        buttonLogin.setOnClickListener(this);
        forgetpasswordtext.setOnClickListener(this);
        googleSignInImage.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();


        createRequest();

    }

    private void createRequest() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

         mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();

                            // go to dashboard of app

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Some error happened", Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }

    public void onLoginClick(View View){
        startActivity(new Intent(this,RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }

    @Override
    public void onClick(View view)
    {
        int source= view.getId();

        if(source==R.id.loginButton)
        {
            userLogin();

        }

        if (source==R.id.forget_password_text)
        {
            Intent intent= new Intent(LoginActivity.this,ForgetPasswordActivity.class);
            startActivity(intent);
            finish();
        }

        if(source==R.id.google_signin_img)
        {
            signIn();
        }

    }

    private void userLogin() {

        String email= emailLogin.getText().toString().trim();
        String password= passwordLogin.getText().toString().trim();


        if(email.isEmpty())
        {
            emailLogin.setError("Email is required");
            emailLogin.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailLogin.setError("Please provide valid email");
            emailLogin.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            passwordLogin.setError("Password is required");
            passwordLogin.requestFocus();
            return;
        }

        if(password.length()<6)
        {
            passwordLogin.setError("Min password length should be 6 characters");
            passwordLogin.requestFocus();
            return;
        }

        buttonLogin.startAnimation();


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // redirect to user profile
                            FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

                            buttonLogin.revertAnimation();

                            if(user.isEmailVerified())
                            {
                                Toast.makeText(LoginActivity.this, "User Logged in successfully!",
                                        Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                user.sendEmailVerification();
                                Toast.makeText(LoginActivity.this, "Check your email to verify your account!",
                                        Toast.LENGTH_SHORT).show();

                            }

                            
                        }

                        else {
                            // If sign in fails, display a message to the user.
                            buttonLogin.revertAnimation();
                            Toast.makeText(LoginActivity.this, "Authentication failed! Please check your credentials",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });




    }
}