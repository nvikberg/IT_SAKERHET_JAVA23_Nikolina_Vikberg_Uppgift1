package com.example.newsapp;
/*
- Toast issues (not showing uup?)
- store info when switching acitvites
- login function
- add salt to the database in its own row
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private FirebaseFirestore db;

    private Button showAccountsButton;
    private Button logOutButton;
    private Button policyButton;
    private Button submitButton;

    private EditText emailInput;
    private EditText addressInput;
    private EditText passwordInput;
    private int selectedGender;
    private CheckBox policyChecked;

    private String email;
    private String gender;
    private String address;
    private String password;
    private Boolean isPolicyChecked;
    // byte[] salt = generateSalt();
    //String saltedHash = hashPassword(password, salt);


    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public static String hashPassword(String password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt); //adding the salt to the hash

            byte[] hash = md.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    //sending new data to firebase
    private void write() {

        byte[] salt = generateSalt(); //generate a new salt
        String hashedPassword = hashPassword(password, salt); //salting the hashed pw
        Log.d("niko", "Write method called");

        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("address", address);
        user.put("password", hashedPassword);
        //user.put("salt", salt); //this is making the app crash -
        //user.put("gender", gender);
        //user.put("terms", terms);

        System.out.println("in wriTe method" + email);
        System.out.println(password + hashedPassword);
        Log.d("passwordN", hashedPassword);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity.this, "New member added", Toast.LENGTH_SHORT).show();
                        Log.d("niko", "DocumentSnapshot successfully written!" + documentReference.getId());
                        Intent intent = new Intent(MainActivity.this, AccountsActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("niko", "Error writing document", e);
                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        //formen
        emailInput = findViewById(R.id.email);
        addressInput = findViewById(R.id.address);
        passwordInput = findViewById(R.id.password);
        policyButton = findViewById(R.id.policyButton);
        policyButton.setBackgroundColor(Color.BLACK);
        policyChecked = findViewById(R.id.policyChecked);
        submitButton = findViewById(R.id.submitButton);
        submitButton.setBackgroundColor(Color.BLACK);
        showAccountsButton = findViewById(R.id.accountsButton);
        showAccountsButton.setBackgroundColor(Color.BLACK);
        logOutButton = findViewById(R.id.logoutButton);
        logOutButton.setBackgroundColor(Color.BLACK);
        // RadioGroup genderGroup = findViewById(R.id.genderGroup);

        policyButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PrivacyActivity.class);
            startActivity(intent);
        });

        submitButton.setOnClickListener(view -> {
            email = emailInput.getText().toString();
            address = addressInput.getText().toString();
            password = passwordInput.getText().toString();
            isPolicyChecked = policyChecked.isChecked();
            Log.d("niko", "policy chedk" + isPolicyChecked + email + address + password);

            // selectedGender = genderGroup.getCheckedRadioButtonId();
//            if (selectedGender == R.id.female) {
//                gender = "Female";
//            } else if (selectedGender == R.id.male) {
//                gender = "Male";
//            }

            if (email.isEmpty() || address.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_LONG).show();
                Log.d("niko", "fill in all fields");

                return;
            }

            if (!policyChecked.isChecked()) {
                Toast.makeText(this, "Please read the policy and check the box", Toast.LENGTH_LONG).show();
                Log.d("niko", "polcy was not checked");
                return;
            }
            Log.d("niko", "Submitting user data: " + email);
            write();
        });

        showAccountsButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AccountsActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}