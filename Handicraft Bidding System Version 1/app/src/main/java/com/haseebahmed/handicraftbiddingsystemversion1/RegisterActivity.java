package com.haseebahmed.handicraftbiddingsystemversion1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class RegisterActivity extends AppCompatActivity implements  View.OnClickListener{

    private FirebaseAuth mAuth;
    private EditText nameText, emailText, passwordText,mobileText;
    private CircularProgressButton registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        changeStatusBarColor();
        mAuth = FirebaseAuth.getInstance();
        nameText= (EditText) findViewById(R.id.nameRegister);
        emailText= (EditText) findViewById(R.id.emailLogin);
        passwordText= (EditText) findViewById(R.id.passwordLogin);
//        mobileText= (EditText) findViewById(R.id.mobileText);
        registerButton= (CircularProgressButton) findViewById(R.id.registerButton);


        registerButton.setOnClickListener(this);



    }


    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    public void onLoginClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void onClick(View view)
    {
        int source= view.getId();

        if(source==R.id.registerButton)
        {
            registerUser();
        }


    }

    private void registerUser()
    {
         final String email= emailText.getText().toString().trim();
         final String name= nameText.getText().toString().trim();
         final String password= passwordText.getText().toString().trim();
//        String mobile= mobileText.getText().toString().trim();


        if(name.isEmpty())
        {
            nameText.setError("Full name is required");
            nameText.requestFocus();
            return;
        }

        if(email.isEmpty())
        {
            emailText.setError("Email is required");
            emailText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailText.setError("Please provide valid email");
            emailText.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            passwordText.setError("Password is required");
            passwordText.requestFocus();
            return;
        }

        if(password.length()<6)
        {
            passwordText.setError("Min password length should be 6 characters");
            passwordText.requestFocus();
            return;
        }
        registerButton.startAnimation();


        mAuth.createUserWithEmailAndPassword(email,password)
               .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful())
                       {
                           User user= new User( name, email, password);
                           FirebaseDatabase.getInstance().getReference("Users")
                                   .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                   .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if(task.isSuccessful())
                                   {
                                       Toast.makeText(RegisterActivity.this, "User has been registered!", Toast.LENGTH_LONG).show();
                                       registerButton.revertAnimation();

                                       new Handler().postDelayed(new Runnable() {
                                           @Override
                                           public void run() {
                                               Intent intent= new Intent(RegisterActivity.this,LoginActivity.class);
                                               startActivity(intent);
                                               finish();

                                           }
                                       }, 500);
                                   }


                               }

                           });
                       }
                       else {
                           Toast.makeText(RegisterActivity.this, "User already exist", Toast.LENGTH_LONG).show();
                           registerButton.revertAnimation();

                           new Handler().postDelayed(new Runnable() {
                               @Override
                               public void run() {
                                   Intent intent= new Intent(RegisterActivity.this,LoginActivity.class);
                                   startActivity(intent);
                                   finish();

                               }
                           }, 500);
                       }
                   }
               });


    }
}