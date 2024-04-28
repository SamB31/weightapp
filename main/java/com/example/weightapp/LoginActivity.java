package com.example.weightapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.content.SharedPreferences;


public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button loginButton;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login); // make sure you use your correct layout file name

        db = new DatabaseHelper(this);
        editTextUsername = findViewById(R.id.editTextWeight); // make sure these IDs match those in your XML
        editTextPassword = findViewById(R.id.LogineditTextTextPassword);
        loginButton = findViewById(R.id.loginButton);


        loginButton.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            int userId = db.getUserId(username, password);
            if (db.checkUser(username, password)) {
                onLoginSuccess(userId);  // Store the user ID in SharedPreferences
                startActivity(new Intent(LoginActivity.this, UserActivity.class)); // Navigate to UserActivity
                finish();  // Finish LoginActivity to remove it from the activity stack
            } else {
                Toast.makeText(LoginActivity.this, "Login error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onLoginSuccess(int userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("UserId", userId);
        editor.apply();
    }
}