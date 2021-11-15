package com.android.twallet.test.tests;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.twallet.test.R;

public class OperationTests extends AppCompatActivity {

    /* Variable button for each test button */
    private Button wComponentsButton, woutComponentsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_operations);

        /* initializing wComponentsButton */
        wComponentsButton = findViewById(R.id.WithComponentsTests);

        /* initializing woutComponentsButton */
        woutComponentsButton = findViewById(R.id.WithoutComponentsTests);

        /**
         * setting test attestation service
         * @param OnClickListener
         *
         */
        wComponentsButton.setOnClickListener(new View.OnClickListener() {
            /**
             * method for performing specified logic for testing attestation service
             * @param view
             * */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OperationTests.this, WithComponentsTests.class));
            }
        });

        /**
         * setting test attestation service
         * @param OnClickListener
         *
         */
        woutComponentsButton.setOnClickListener(new View.OnClickListener() {
            /**
             * method for performing specified logic for testing secure storage
             * @param view
             * */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OperationTests.this, WithoutComponentsTests.class));
            }
        });

    }

}