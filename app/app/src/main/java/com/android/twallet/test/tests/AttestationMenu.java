package com.android.twallet.test.tests;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.twallet.test.R;

public class AttestationMenu extends AppCompatActivity {

    /* Variable button for each test button */
    private Button keyGenButton, attestationProcessButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_attestation_menu);

        /* initializing wComponentsButton */
        keyGenButton = findViewById(R.id.KeyGenButton);

        /* initializing woutComponentsButton */
        attestationProcessButton = findViewById(R.id.AttestationProcessButton);

        /**
         * setting test attestation service
         * @param OnClickListener
         *
         */
        keyGenButton.setOnClickListener(new View.OnClickListener() {
            /**
             * method for performing specified logic for testing attestation service
             * @param view
             * */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AttestationMenu.this, AttestationKeyGenTests.class));
            }
        });

        /**
         * setting test attestation service
         * @param OnClickListener
         *
         */
        attestationProcessButton.setOnClickListener(new View.OnClickListener() {
            /**
             * method for performing specified logic for testing secure storage
             * @param view
             * */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AttestationMenu.this, AttestationProcessTests.class));
            }
        });

    }

}
