package com.android.twallet.test.tests;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.twallet.test.R;

public class ComponentsTests extends AppCompatActivity {

    /* Variable button for each test button */
    private Button contextSwitchButton, attestationButton, secureStorageButton, authServiceButton, monitoringButton, loggingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_components);

        /* initializing attestationButton */
        contextSwitchButton = findViewById(R.id.ContextSwitchTest);

        /* initializing attestationButton */
        attestationButton = findViewById(R.id.AttestationTest);

        /* initializing secureStorageButton */
        secureStorageButton = findViewById(R.id.SecureStorageTest);

        /* initializing authServiceButton */
        authServiceButton = findViewById(R.id.AuthServiceTest);

        /* initializing monitoringButton */
        monitoringButton = findViewById(R.id.MonitoringTest);

        /* initializing loggingButton */
        loggingButton = findViewById(R.id.LoggingTest);

        /**
         * setting test context switch
         * @param OnClickListener
         *
         */
        contextSwitchButton.setOnClickListener(new View.OnClickListener() {
            /**
             * method for performing specified logic for testing context switch overhead
             * @param view
             * */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ComponentsTests.this, ContextSwitchTests.class));
            }
        });

        /**
         * setting test attestation service
         * @param OnClickListener
         *
         */
        attestationButton.setOnClickListener(new View.OnClickListener() {
            /**
             * method for performing specified logic for testing attestation service
             * @param view
             * */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ComponentsTests.this, AttestationMenu.class));
            }
        });

        /**
         * setting test attestation service
         * @param OnClickListener
         *
         */
        secureStorageButton.setOnClickListener(new View.OnClickListener() {
            /**
             * method for performing specified logic for testing secure storage
             * @param view
             * */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ComponentsTests.this, SecureStorageMenu.class));
            }
        });

        /**
         * setting test authentication service
         * @param OnClickListener
         *
         */
        authServiceButton.setOnClickListener(new View.OnClickListener() {
            /**
             * method for performing specified logic for testing authentication service
             * @param view
             * */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ComponentsTests.this, AuthServiceMenu.class));
            }
        });

        /**
         * setting test monitoring service
         * @param OnClickListener
         *
         */
        monitoringButton.setOnClickListener(new View.OnClickListener() {
            /**
             * method for performing specified logic for testing monitoring service
             * @param view
             * */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ComponentsTests.this, MonitoringTests.class));
            }
        });

        /**
         * setting test logging service
         * @param OnClickListener
         *
         */
        loggingButton.setOnClickListener(new View.OnClickListener() {
            /**
             * method for performing specified logic for testing logging service
             * @param view
             * */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ComponentsTests.this, LoggingTests.class));
            }
        });

    }

}