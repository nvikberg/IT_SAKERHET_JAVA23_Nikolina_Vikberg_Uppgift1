package com.example.newsapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Map;

public class AccountsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText emailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_accounts);

        db = FirebaseFirestore.getInstance();
        emailInput = findViewById(R.id.deleteUserText);

        //deleting user data by entering the email
        Button getDeleteButton = findViewById(R.id.deleteButton);
        getDeleteButton.setBackgroundColor(Color.RED);
        getDeleteButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim(); //getting email input
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter an email.", Toast.LENGTH_SHORT).show();
                return;
            }

            //extra safety with alert pop up before deleting account
            new AlertDialog.Builder(this)
                    .setTitle("Delete account")
                    .setMessage("Are you sure you want to delete the user with email: " + email)
                    .setPositiveButton("Yes", (dialog, which) -> deleteUserAccount(email)) //user clicks yes delete user method is implemented
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        //getting user data by button click
        Button getDataButton = findViewById(R.id.getDataButton);
        getDataButton.setBackgroundColor(Color.BLACK);
        getDataButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim(); //getting email input
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter an email.", Toast.LENGTH_SHORT).show();
                return;
            }
            getUserData(email);
        });

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (v, insets) ->
                {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });
    }


    //retrives user data from firebase based on email
    private void getUserData(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(AccountsActivity.this, "Error getting data", Toast.LENGTH_SHORT).show();
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        StringBuilder userResults = new StringBuilder();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String firebaseId = document.getId();
                            Map<String, Object> dataFromFB = document.getData();
                            //string without pw
                            StringBuilder userDataFormatted = new StringBuilder();
                            dataFromFB.remove("password");

                            for (Map.Entry<String, Object> entry : dataFromFB.entrySet()) {
                                //appending each key-value pair
                                userDataFormatted.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                            }
                            userResults.append("User ID: ").append(firebaseId).append("\n").append(userDataFormatted).append("\n\n");
                            Log.d("firebase", "ID: " + firebaseId + " " + userDataFormatted);
                        }

                        TextView resultsTextView = findViewById(R.id.dataBaseText);
                        resultsTextView.setText(userResults.toString());
                    }
                });
    }


    //delete user account by email
    private void deleteUserAccount(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String firebaseId = task.getResult().getDocuments().get(0).getId();
                        db.collection("users").document(firebaseId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "your account was deleted!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "your account was not deleted " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "No account with that email.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "something went wrong " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}


//for admin page
//    //collects user data to map (without pw) and shows user data from firebase
//    private void getAllUserData() {
//        db.collection("users")
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (!task.isSuccessful()) {
//                        Log.e("firebase", "Error getting data", task.getException());
//                    } else {
//                        StringBuilder userResults = new StringBuilder();
//
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            String firebaseId = document.getId();
//                            Map<String, Object> dataFromFB = document.getData();
//                            //new map without the password
//                            Map<String, Object> userDataNoPassword = new HashMap<>(dataFromFB);
//                            userDataNoPassword.remove("password");
//                            userResults.append("User: ").append(userDataNoPassword).append("\n\n\n");
//                            Log.d("firebase", "ID: " + firebaseId + " " + userDataNoPassword);
//                        }
//
//                        TextView resultsTextView = findViewById(R.id.dataBaseText);
//                        resultsTextView.setText(userResults.toString());
//                    }
//                });
//    }