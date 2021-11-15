package com.android.twallet.test.tests;

import static com.android.twallet.test.utils.CryptoSchemes.keyGen.ECDSA;
import static com.android.twallet.test.utils.CryptoSchemes.keyGen.RSA;

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
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class AttestationKeyGenTests extends AppCompatActivity {

    private static final String FILENAME_OUTPUT = "/outputs/attestation-key-gen-%s-%s-output-%s.json";
    private static final int RSA_KEY_SIZE = 2048;
    private static final int ECDSA_KEY_SIZE = 256;
    private static final int NUM_OF_TESTS = 2;

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
        setContentView(R.layout.activity_test_attestation_key_gen);

        /* initializing unlockButton */
        startTestButton = findViewById(R.id.startTest);

        /* initializing testAttestation */
        testAttestation = new CheckBox[NUM_OF_TESTS];

        testAttestation[0] = findViewById(R.id.checkDescriptionKeyGen1);
        testAttestation[1] = findViewById(R.id.checkDescriptionKeyGen2);

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
                    String output;

                    /* Test RSA Key Generation*/
                    if (testAttestation[0].isChecked()) {
                        output = tWalletUtils.keyGenBenchmark(RSA.getCode(), RSA_KEY_SIZE, iterations);

                        runOnUiThread(() -> progressBar.setProgress(iterations));
                        testAttestation[0].setText(testAttestation[0].getText() + "\t \u2713");
                        writeIntoFile("RSA", RSA_KEY_SIZE, output);
                    } else progressBar.setProgress(progressBar.getProgress() + iterations);


                    /* Test ECDSA Key Generation*/
                    if (testAttestation[1].isChecked()) {
                        output = tWalletUtils.keyGenBenchmark(ECDSA.getCode(), ECDSA_KEY_SIZE, iterations);

                        runOnUiThread(() -> progressBar.setProgress(iterations * 2));
                        testAttestation[1].setText(testAttestation[1].getText() + "\t \u2713");
                        writeIntoFile("ECDSA", ECDSA_KEY_SIZE, output);
                    } else progressBar.setProgress(progressBar.getProgress() + iterations);


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
        testAttestation[0].setText("RSA Key Gen");
        testAttestation[1].setText("ECDSA Key Gen");
    }

    private void writeIntoFile(String testname, int keySize, String values) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject obj = gson.fromJson(values, JsonObject.class);

        try {
            Writer writer = new FileWriter(getFilesDir().getPath() + String.format(FILENAME_OUTPUT, testname, keySize, values.length()));

            gson.toJson(obj, writer);
            writer.close();

        } catch (IOException e) {
            runOnUiThread(() -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
