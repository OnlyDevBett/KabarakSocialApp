package com.kabarakuniversityforumApp.AccountCreation;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.kabarakuniversityforumApp.General_Issues.ThePanel;
import com.kabarakuniversityforumApp.R;


public class LoginActivity extends AppCompatActivity {


    private EditText mEmail, mPassword;
        private Button mLogin, mPasswordreset;
        private ProgressBar progressBar;
        private FirebaseAuth mAuth;
        private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mAuth = FirebaseAuth.getInstance();
        mEmail = (EditText) findViewById(R.id.email_address);
        mPassword = (EditText) findViewById(R.id.password_input);
        mPasswordreset = (Button) findViewById(R.id.forget_password_link);
        mLogin = (Button) findViewById(R.id.login_button);
        loadingBar = new ProgressDialog(this);


        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                loginUserAccount();

            }
        });

        mPasswordreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, PasswordReset.class);
                startActivity(i);
            }
        });

    }

    private void loginUserAccount()
    {

        String email;
        String password;
         email = mEmail.getText().toString();
         password = mPassword.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            mEmail.setError("Please Enter email...!!!");
            return;
        }
        if (TextUtils.isEmpty(password))
        {
            mPassword.setError("Please enter password");
            return;
        }

        loadingBar.setTitle("Logging in.. ...");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, ThePanel.class);
                            startActivity(intent);
                        }
                        else {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Login Failed!!!", Toast.LENGTH_SHORT).show();

                             }


                    }
                });

    }



}