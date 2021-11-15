package com.android.twallet.test.tests;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.twallet.test.R;

public class SecureStorageMenu extends AppCompatActivity {

    /* Variable button for each test button */
    private Button normalWorldButton, secureWorldButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_secure_storage_menu);

        /* initializing wComponentsButton */
        normalWorldButton = findViewById(R.id.FromNormalWorld2);

        /* initializing woutComponentsButton */
        secureWorldButton = findViewById(R.id.FromSecureWorld2);

        /**
         * setting test attestation service
         * @param OnClickListener
         *
         */
        normalWorldButton.setOnClickListener(new View.OnClickListener() {
            /**
             * method for performing specified logic for testing attestation service
             * @param view
             * */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SecureStorageMenu.this, SecureStorageNWTests.class));
            }
        });

        /**
         * setting test attestation service
         * @param OnClickListener
         *
         */
        secureWorldButton.setOnClickListener(new View.OnClickListener() {
            /**
             * method for performing specified logic for testing secure storage
             * @param view
             * */
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SecureStorageMenu.this, SecureStorageSWTests.class));
            }
        });

    }

}