package com.example.HealthMonitor;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.concurrent.Executor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;


public class authbio extends AppCompatActivity {
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    ConstraintLayout mMainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authbio);

        mMainLayout = findViewById(R.id.authbio1);

        BiometricManager biometricManager = BiometricManager.from(this);
        switch(biometricManager.canAuthenticate())
        {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(getApplicationContext(),"Device Doesnt have fingerprint",Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(getApplicationContext(), "Not working", Toast.LENGTH_SHORT).show();
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(getApplicationContext(),"No fingerPrint",Toast.LENGTH_SHORT).show();

        }
        Executor executor= ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(authbio.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString){
                super.onAuthenticationError(errorCode,errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_SHORT).show();
                openNewActivity();
            }
            @Override
            public void onAuthenticationFailed()
            {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Bio Auth")
                .setDescription("Use Fingerprint to login").setDeviceCredentialAllowed(true).build();
        biometricPrompt.authenticate(promptInfo);


    }

    public void openNewActivity() {
        Intent intent = new Intent(this, HeathCheckMenu.class);
        startActivity(intent);
    }
}