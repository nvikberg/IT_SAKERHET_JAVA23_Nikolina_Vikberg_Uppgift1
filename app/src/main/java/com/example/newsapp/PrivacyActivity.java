package com.example.newsapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PrivacyActivity extends AppCompatActivity {

    private TextView policyText;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_privacy2);


        policyText = findViewById(R.id.policyText);
        policyText.setText("We value your privacy. When you submit your email address and password, we use this information to create your account and communicate with you.\n" +
                "\nYour password is securely stored and never shared. We implement strong security measures to protect your data.\n" +
                "\n" + "You can access or delete your information at any time by contacting us or following the steps to delete your account. By submitting this form, you consent to our data practices.");

        backButton = findViewById(R.id.backButton);
        backButton.setBackgroundColor(Color.BLACK);
        backButton.setOnClickListener(event -> {
            Intent intent = new Intent(PrivacyActivity.this, MainActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}