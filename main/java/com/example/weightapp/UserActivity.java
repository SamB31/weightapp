package com.example.weightapp;

import android.os.Bundle;
import android.database.Cursor;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageButton;
import android.view.View;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import android.Manifest;
import androidx.core.app.ActivityCompat;


import java.util.ArrayList;
import java.util.List;
import android.content.SharedPreferences;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.widget.Toast;


import com.example.weightapp.DatabaseHelper;
import com.example.weightapp.Weight;
import com.example.weightapp.WeightsAdapter;

public class UserActivity extends AppCompatActivity implements WeightsAdapter.OnWeightListener {
    private RecyclerView weightsRecyclerView;
    private WeightsAdapter weightsAdapter;
    private List<Weight> weightsList;
    private DatabaseHelper db;
    private int userId;
    private EditText addTextWeight;
    private TextView todayWeight;
    private ImageButton editButton;
    private ImageButton deleteButton;
    private int itemId;
    private static final int PERMISSION_SEND_SMS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        View inflatedView = getLayoutInflater().inflate(R.layout.weight_item, null);

        todayWeight = findViewById(R.id.todayWeight);
        userId = getUserId();
        db = new DatabaseHelper(this);
        weightsRecyclerView = findViewById(R.id.weightsRecyclerView);
        weightsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        weightsList = new ArrayList<>(); // Initialize the list here before calling loadData()
        Button addWeightButton = findViewById(R.id.addWeightButton);
        addTextWeight = findViewById(R.id.addWeightText);
        editButton = inflatedView.findViewById(R.id.editButton);
        deleteButton = inflatedView.findViewById(R.id.deleteButton);
        Button enableNotificationsButton = findViewById(R.id.button3);


        loadData();

        addWeightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWeight();
            }
        });

        enableNotificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSmsPermission();
            }
        });
    }

    private void loadData() {
        todayWeight.setText(db.getLatestWeight(userId));
        Cursor cursor = db.getAllWeights(userId); // Assume userId is known
        weightsList.clear();
        int idColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_WEIGHT_ID);
        int dateColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE);
        int weightColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_WEIGHT);

        if (idColumnIndex == -1 || dateColumnIndex == -1 || weightColumnIndex == -1) {
            // Handle the error - perhaps by logging or displaying an error message
            cursor.close();
            return;
        }

        while (cursor.moveToNext()) {
            int id = cursor.getInt(idColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            String weight = cursor.getString(weightColumnIndex);
            weightsList.add(new Weight(id, userId, weight, date));
        }
        cursor.close();
        weightsAdapter = new WeightsAdapter(this, weightsList, this);
        weightsRecyclerView.setAdapter(weightsAdapter);
    }

    @Override
    public void onDeleteClick(int position) {
        Weight weight = weightsList.get(position);
        db.deleteWeight(weight.getId()); // Assuming your DatabaseHelper has a method to delete weights by ID
        weightsList.remove(position); // Remove the weight from the list
        weightsAdapter.notifyItemRemoved(position); // Notify the adapter of the item removed
        weightsAdapter.notifyItemRangeChanged(position, weightsList.size()); // Correct the position of remaining items
        loadData();
    }


    private void addWeight() {
        String weight = addTextWeight.getText().toString().trim();
        if (!weight.isEmpty()) {
            // Assume you also want to store the current date with the weight
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String currentDate = sdf.format(new Date());
            db.addWeight(userId, weight, currentDate);
            addTextWeight.setText("");  // Clear the EditText after submitting
            loadData();  // Refresh the list (assuming this method reloads the data)
        } else {
            Toast.makeText(this, "Please enter a weight", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEditClick(int position) {
        Weight weight = weightsList.get(position);
        Intent intent = new Intent(this, EditWeightActivity.class);
        intent.putExtra("weight_id", weight.getId());
        intent.putExtra("weight_value", weight.getWeight());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();  // Reload your data here
    }

    private int getUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return sharedPreferences.getInt("UserId", -1); // Return -1 or any invalid ID if not found
    }

    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSION_SEND_SMS);
        } else {
            // Permission has already been granted, send SMS
            Toast.makeText(UserActivity.this, "SMS already enabled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send SMS
                Toast.makeText(UserActivity.this, "SMS enabled", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied, handle the failure
                Toast.makeText(UserActivity.this, "SMS disabled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
