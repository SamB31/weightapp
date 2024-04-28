package com.example.weightapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class EditWeightActivity extends AppCompatActivity {
    private EditText editTextWeight;
    private Button buttonSave;
    private DatabaseHelper db;
    private int weightId; // ID of the weight to edit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editweight);

        editTextWeight = findViewById(R.id.editTextWeight);
        buttonSave = findViewById(R.id.buttonSave);
        db = new DatabaseHelper(this);

        // Get the weight ID and initial weight value passed from the main activity
        weightId = getIntent().getIntExtra("weight_id", -1);
        float initialWeight = getIntent().getFloatExtra("weight_value", 0);

        editTextWeight.setText(String.valueOf(initialWeight));

        buttonSave.setOnClickListener(v -> saveWeight());
    }

    private void saveWeight() {
        String newWeight = editTextWeight.getText().toString();
        db.updateWeight(weightId, newWeight); // Assume updateWeight takes an ID and a new weight value
        finish(); // Close this activity and return to the previous screen
    }
}
