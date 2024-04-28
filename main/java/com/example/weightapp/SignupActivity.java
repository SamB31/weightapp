package com.example.weightapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.view.View;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button signupButton;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);  // replace with your layout file

        db = new DatabaseHelper(this);
        editTextUsername = findViewById(R.id.signupEditTextText);
        editTextPassword = findViewById(R.id.signupEditTextTextPassword);
        signupButton = findViewById(R.id.signupButton);

        signupButton.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            db.addUser(username, password);
            Toast.makeText(SignupActivity.this, "User registered", Toast.LENGTH_SHORT).show();

            // Handler to delay and then run the code inside run() method
            new Handler().postDelayed(() -> {
                // Intent to go back to MainActivity
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clears the activity stack
                startActivity(intent);
                finish(); // Call this if you want to finish the current activity
            }, 2000);  // 2000 milliseconds delay
        });
    }
}