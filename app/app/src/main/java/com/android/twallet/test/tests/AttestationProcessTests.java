package com.android.twallet.test.tests;

import static com.android.twallet.test.utils.CryptoSchemes.SignMode.SHA1withRSA;
import static com.android.twallet.test.utils.CryptoSchemes.SignMode.SHA1withRSAPSS;
import static com.android.twallet.test.utils.CryptoSchemes.SignMode.SHA224withRSA;
import static com.android.twallet.test.utils.CryptoSchemes.SignMode.SHA224withRSAPSS;
import static com.android.twallet.test.utils.CryptoSchemes.SignMode.SHA256withECDSA;
import static com.android.twallet.test.utils.CryptoSchemes.SignMode.SHA256withRSA;
import static com.android.twallet.test.utils.CryptoSchemes.SignMode.SHA256withRSAPSS;
import static com.android.twallet.test.utils.CryptoSchemes.SignMode.SHA384withRSA;
import static com.android.twallet.test.utils.CryptoSchemes.SignMode.SHA384withRSAPSS;
import static com.android.twallet.test.utils.CryptoSchemes.SignMode.SHA512withRSA;
import static com.android.twallet.test.utils.CryptoSchemes.SignMode.SHA512withRSAPSS;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.twallet.test.R;
import com.android.twallet.test.utils.TWalletUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class AttestationProcessTests extends AppCompatActivity {

    private static final String FILENAME_OUTPUT = "/outputs/attestation-%s-%s-output-%s.json";
    private static final int RSA_KEY_SIZE = 4096;
    private static final int ECDSA_KEY_SIZE = 256;
    private static final int NUM_OF_TESTS = 11;

    /* Variable button for starting tests */
    Button startTestButton;

    /* Variable text that says which tests are going to be done */
    CheckBox[] testAttestation;

    /* Variable for changing repetitions */
    EditText numOfReps;

    /* Variable progress bar for tests progress track */
    ProgressBar progressBar;

    /**
     * onCreate method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_attestation_process);

        /* initializing unlockButton */
        startTestButton = findViewById(R.id.startTest);

        /* initializing testAttestation */
        testAttestation = new CheckBox[NUM_OF_TESTS];

        testAttestation[0] = findViewById(R.id.checkDescriptionCipher1);
        testAttestation[1] = findViewById(R.id.checkDescriptionCipher2);
        testAttestation[2] = findViewById(R.id.checkDescriptionCipher3);
        testAttestation[3] = findViewById(R.id.checkDescriptionCipher4);
        testAttestation[4] = findViewById(R.id.checkDescriptionCipher5);
        testAttestation[5] = findViewById(R.id.checkDescriptionCipher6);
        testAttestation[6] = findViewById(R.id.checkDescriptionCipher7);
        testAttestation[7] = findViewById(R.id.checkDescriptionCipher8);
        testAttestation[8] = findViewById(R.id.checkDescriptionCipher9);
        testAttestation[9] = findViewById(R.id.checkDescriptionCipher10);
        testAttestation[10] = findViewById(R.id.checkDescriptionCipher11);

        resetTests();

        /* Initializing numOfReps*/
        numOfReps = findViewById(R.id.editTextNumber);

        /* initializing progressBar */
        progressBar = findViewById(R.id.progressBar);

        TWalletUtils tWalletUtils = new TWalletUtils();

        /**
         * method on click action performed for starting tests
         * @param OnClickListener
         */
        startTestButton.setOnClickListener(new View.OnClickListener() {
            /**
             * @override onClick
             * @param view
             * @return void
             */
            @Override
            public void onClick(View view) {
                int iterations = Integer.valueOf(String.valueOf(numOfReps.getText()));
                resetTests();
                startTestButton.setEnabled(false);
                disableEditText(numOfReps);
                if (iterations <= 0) {
                    Toast.makeText(getApplicationContext(), "Invalid number of repetitions", Toast.LENGTH_SHORT).show();
                    startTestButton.setEnabled(true);
                    enableEditText(numOfReps);
                    return;
                }

                long[] values = new long[iterations];
                progressBar.setMax(iterations * NUM_OF_TESTS);
                new Thread(() -> {
                    int progressStatus = 1;

                    /* Pre-generate the keys that will be used*/
                    tWalletUtils.prepareAttestBenchmark();

                    if (testAttestation[0].isChecked()) {
                        for (int i = 0; i < iterations; i++) {
                            long startTime = System.currentTimeMillis();
                            tWalletUtils.attestBenchmark(SHA1withRSA.getCode(), RSA_KEY_SIZE, 12);
                            values[i] = System.currentTimeMillis() - startTime;

                            int finalProgressStatus = progressStatus;
                            runOnUiThread(() -> progressBar.setProgress(finalProgressStatus));
                            progressStatus++;
                        }
                        writeIntoFile(testAttestation[0].getText().toString(), RSA_KEY_SIZE, values);
                        testAttestation[0].setText(testAttestation[0].getText() + "\t \u2713");
                    } else {
                        progressStatus += iterations;
                        progressBar.setProgress(progressStatus);
                    }


                    if (testAttestation[1].isChecked()) {
                        for (int i = 0; i < iterations; i++) {
                            long startTime = System.currentTimeMillis();
                            tWalletUtils.attestBenchmark(SHA224withRSA.getCode(), RSA_KEY_SIZE, 12);
                            values[i] = System.currentTimeMillis() - startTime;

                            int finalProgressStatus = progressStatus;
                            runOnUiThread(() -> progressBar.setProgress(finalProgressStatus));
                            progressStatus++;
                        }
                        writeIntoFile(testAttestation[1].getText().toString(), RSA_KEY_SIZE, values);
                        testAttestation[1].setText(testAttestation[1].getText() + "\t \u2713");
                    } else {
                        progressStatus += iterations;
                        progressBar.setProgress(progressStatus);
                    }


                    if (testAttestation[2].isChecked()) {
                        for (int i = 0; i < iterations; i++) {
                            long startTime = System.currentTimeMillis();
                            tWalletUtils.attestBenchmark(SHA256withRSA.getCode(), RSA_KEY_SIZE, 12);
                            values[i] = System.currentTimeMillis() - startTime;

                            int finalProgressStatus = progressStatus;
                            runOnUiThread(() -> progressBar.setProgress(finalProgressStatus));
                            progressStatus++;
                        }
                        writeIntoFile(testAttestation[2].getText().toString(), RSA_KEY_SIZE, values);
                        testAttestation[2].setText(testAttestation[2].getText() + "\t \u2713");
                    } else {
                        progressStatus += iterations;
                        progressBar.setProgress(progressStatus);
                    }


                    if (testAttestation[3].isChecked()) {
                        for (int i = 0; i < iterations; i++) {
                            long startTime = System.currentTimeMillis();
                            tWalletUtils.attestBenchmark(SHA384withRSA.getCode(), RSA_KEY_SIZE, 12);
                            values[i] = System.currentTimeMillis() - startTime;

                            int finalProgressStatus = progressStatus;
                            runOnUiThread(() -> progressBar.setProgress(finalProgressStatus));
                            progressStatus++;
                        }
                        writeIntoFile(testAttestation[3].getText().toString(), RSA_KEY_SIZE, values);
                        testAttestation[3].setText(testAttestation[3].getText() + "\t \u2713");
                    } else {
                        progressStatus += iterations;
                        progressBar.setProgress(progressStatus);
                    }


                    if (testAttestation[4].isChecked()) {
                        for (int i = 0; i < iterations; i++) {
                            long startTime = System.currentTimeMillis();
                            tWalletUtils.attestBenchmark(SHA512withRSA.getCode(), RSA_KEY_SIZE, 12);
                            values[i] = System.currentTimeMillis() - startTime;

                            int finalProgressStatus = progressStatus;
                            runOnUiThread(() -> progressBar.setProgress(finalProgressStatus));
                            progressStatus++;

                        }
                        writeIntoFile(testAttestation[4].getText().toString(), RSA_KEY_SIZE, values);
                        testAttestation[4].setText(testAttestation[4].getText() + "\t \u2713");
                    } else {
                        progressStatus += iterations;
                        progressBar.setProgress(progressStatus);
                    }


                    if (testAttestation[5].isChecked()) {
                        for (int i = 0; i < iterations; i++) {
                            long startTime = System.currentTimeMillis();
                            tWalletUtils.attestBenchmark(SHA1withRSAPSS.getCode(), RSA_KEY_SIZE, 12);
                            values[i] = System.currentTimeMillis() - startTime;

                            int finalProgressStatus = progressStatus;
                            runOnUiThread(() -> progressBar.setProgress(finalProgressStatus));
                            progressStatus++;
                        }
                        writeIntoFile(testAttestation[5].getText().toString(), RSA_KEY_SIZE, values);
                        testAttestation[5].setText(testAttestation[5].getText() + "\t \u2713");
                    } else {
                        progressStatus += iterations;
                        progressBar.setProgress(progressStatus);
                    }


                    if (testAttestation[6].isChecked()) {
                        for (int i = 0; i < iterations; i++) {
                            long startTime = System.currentTimeMillis();
                            tWalletUtils.attestBenchmark(SHA224withRSAPSS.getCode(), RSA_KEY_SIZE, 12);
                            values[i] = System.currentTimeMillis() - startTime;

                            int finalProgressStatus = progressStatus;
                            runOnUiThread(() -> progressBar.setProgress(finalProgressStatus));
                            progressStatus++;
                        }
                        writeIntoFile(testAttestation[6].getText().toString(), RSA_KEY_SIZE, values);
                        testAttestation[6].setText(testAttestation[6].getText() + "\t \u2713");
                    } else {
                        progressStatus += iterations;
                        progressBar.setProgress(progressStatus);
                    }


                    if (testAttestation[7].isChecked()) {
                        for (int i = 0; i < iterations; i++) {
                            long startTime = System.currentTimeMillis();
                            tWalletUtils.attestBenchmark(SHA256withRSAPSS.getCode(), RSA_KEY_SIZE, 12);
                            values[i] = System.currentTimeMillis() - startTime;

                            int finalProgressStatus = progressStatus;
                            runOnUiThread(() -> progressBar.setProgress(finalProgressStatus));
                            progressStatus++;
                        }
                        writeIntoFile(testAttestation[7].getText().toString(), RSA_KEY_SIZE, values);
                        testAttestation[7].setText(testAttestation[7].getText() + "\t \u2713");
                    } else {
                        progressStatus += iterations;
                        progressBar.setProgress(progressStatus);
                    }


                    if (testAttestation[8].isChecked()) {
                        for (int i = 0; i < iterations; i++) {
                            long startTime = System.currentTimeMillis();
                            tWalletUtils.attestBenchmark(SHA384withRSAPSS.getCode(), RSA_KEY_SIZE, 12);
                            values[i] = System.currentTimeMillis() - startTime;

                            int finalProgressStatus = progressStatus;
                            runOnUiThread(() -> progressBar.setProgress(finalProgressStatus));
                            progressStatus++;
                        }
                        writeIntoFile(testAttestation[8].getText().toString(), RSA_KEY_SIZE, values);
                        testAttestation[8].setText(testAttestation[8].getText() + "\t \u2713");
                    } else {
                        progressStatus += iterations;
                        progressBar.setProgress(progressStatus);
                    }


                    if (testAttestation[9].isChecked()) {
                        for (int i = 0; i < iterations; i++) {
                            long startTime = System.currentTimeMillis();
                            tWalletUtils.attestBenchmark(SHA512withRSAPSS.getCode(), RSA_KEY_SIZE, 12);
                            values[i] = System.currentTimeMillis() - startTime;

                            int finalProgressStatus = progressStatus;
                            runOnUiThread(() -> progressBar.setProgress(finalProgressStatus));
                            progressStatus++;
                        }
                        writeIntoFile(testAttestation[9].getText().toString(), RSA_KEY_SIZE, values);
                        testAttestation[9].setText(testAttestation[9].getText() + "\t \u2713");
                    } else {
                        progressStatus += iterations;
                        progressBar.setProgress(progressStatus);
                    }


                    if (testAttestation[10].isChecked()) {
                        for (int i = 0; i < iterations; i++) {
                            long startTime = System.currentTimeMillis();
                            tWalletUtils.attestBenchmark(SHA256withECDSA.getCode(), ECDSA_KEY_SIZE, 12);
                            values[i] = System.currentTimeMillis() - startTime;

                            int finalProgressStatus = progressStatus;
                            runOnUiThread(() -> progressBar.setProgress(finalProgressStatus));
                            progressStatus++;
                        }
                        writeIntoFile(testAttestation[10].getText().toString(), ECDSA_KEY_SIZE, values);
                        testAttestation[10].setText(testAttestation[10].getText() + "\t \u2713");
                    } else {
                        progressStatus += iterations;
                        progressBar.setProgress(progressStatus);
                    }


                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        startTestButton.setEnabled(true);
                    });
                }).start();
            }
        });
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
    }

    private void enableEditText(EditText editText) {
        editText.setFocusableInTouchMode(true);
        editText.setEnabled(true);
        editText.setCursorVisible(true);
    }

    private void resetTests() {
        testAttestation[0].setText("SHA1withRSA");
        testAttestation[1].setText("SHA224withRSA");
        testAttestation[2].setText("SHA256withRSA");
        testAttestation[3].setText("SHA384withRSA");
        testAttestation[4].setText("SHA512withRSA");
        testAttestation[5].setText("SHA1withRSAPSS");
        testAttestation[6].setText("SHA224withRSAPSS");
        testAttestation[7].setText("SHA256withRSAPSS");
        testAttestation[8].setText("SHA384withRSAPSS");
        testAttestation[9].setText("SHA512withRSAPSS");
        testAttestation[10].setText("SHA256withECDSA");
    }

    private void writeIntoFile(String testname, int keySize, long[] values) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject obj = new JsonObject();
        JsonArray array = new Gson().toJsonTree(values).getAsJsonArray();

        obj.addProperty("length", values.length);
        obj.add("values", array);

        try {
            Writer writer = new FileWriter(getFilesDir().getPath() + String.format(FILENAME_OUTPUT, testname, keySize, values.length));

            gson.toJson(obj, writer);
            writer.close();

        } catch (IOException e) {
            runOnUiThread(() -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
