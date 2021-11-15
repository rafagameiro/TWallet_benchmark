package com.android.twallet.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.twallet.test.databinding.ActivityMainBinding;
import com.android.twallet.test.tests.ComponentsTests;
import com.android.twallet.test.tests.OperationTests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainActivity extends AppCompatActivity {

    private static final String OUTPUT_DIRECTORY = "/outputs/";

    private ActivityMainBinding binding;

    /* Variable button for each test button */
    private Button operationsButton, componentsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* initializing operationsButton */
        operationsButton = findViewById(R.id.OperationTest);

        /* initializing componentsButton */
        componentsButton = findViewById(R.id.ComponentTest);

        try {
            String path = getFilesDir().getPath() + OUTPUT_DIRECTORY;
            if (Files.notExists(Paths.get(path)))
                Files.createDirectory(Paths.get(path));

        } catch (IOException e) {
            Toast.makeText(this, "Error while creating output directory", Toast.LENGTH_SHORT).show();
            finish();
        }

        /**
         * setting test attestation service
         * @param OnClickListener
         *
         */
        operationsButton.setOnClickListener(new View.OnClickListener() {
            /**
             * method for performing specified logic for testing attestation service
             * @param view
             * */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OperationTests.class));
            }
        });

        /**
         * setting test attestation service
         * @param OnClickListener
         *
         */
        componentsButton.setOnClickListener(new View.OnClickListener() {
            /**
             * method for performing specified logic for testing secure storage
             * @param view
             * */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ComponentsTests.class));
            }
        });

    }

}