package com.android.twallet.test.tests;

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

public class SecureStorageSWTests extends AppCompatActivity {

    private static final String FILENAME_OUTPUT = "/outputs/storage-sw-%s-output-%s.json";
    private static final int NUM_OF_TESTS = 3;

    /* Variable button for starting tests */
    Button startTestButton;

    /* Variable text that says which tests are going to be done */
    CheckBox[] testStorage;

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
        setContentView(R.layout.activity_test_secure_storage_secure_world);

        /* initializing unlockButton */
        startTestButton = findViewById(R.id.startTest);

        /* initializing testStorage */
        testStorage = new CheckBox[NUM_OF_TESTS];
        for (int i = 0; i < NUM_OF_TESTS; i++) {
            testStorage[i] = findViewById(R.id.checkDescription + (i + 1));
        }
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

                progressBar.setMax(iterations * NUM_OF_TESTS);
                final String[] output = {null};
                new Thread(() -> {

                    /* Test Data Storage*/
                    if (testStorage[0].isChecked()) {
                        output[0] = tWalletUtils.writeDataBenchmark(iterations);

                        runOnUiThread(() -> progressBar.setProgress(iterations));
                        testStorage[0].setText(testStorage[0].getText() + "\t \u2713");
                        writeIntoFile("store", output[0], iterations);
                    } else progressBar.setProgress(progressBar.getProgress() + iterations);


                    /* Test Data Retrieval*/
                    if (testStorage[1].isChecked()) {
                        output[0] = tWalletUtils.readDataBenchmark(iterations);

                        runOnUiThread(() -> progressBar.setProgress(iterations * 2));
                        testStorage[1].setText(testStorage[1].getText() + "\t \u2713");
                        writeIntoFile("load", output[0], iterations);
                    } else progressBar.setProgress(progressBar.getProgress() + iterations);


                    /* Test Data Deletion*/
                    if (testStorage[2].isChecked()) {
                        output[0] = tWalletUtils.deleteDataBenchmark(iterations);

                        runOnUiThread(() -> progressBar.setProgress(iterations * 3));
                        testStorage[2].setText(testStorage[2].getText() + "\t \u2713");
                        writeIntoFile("delete", output[0], iterations);
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
        testStorage[0].setText("Write Data");
        testStorage[1].setText("Read Data");
        testStorage[2].setText("Delete Data");
    }

    private void writeIntoFile(String testname, String values, int iterations) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject obj = gson.fromJson(values, JsonObject.class);

        try {
            Writer writer = new FileWriter(getFilesDir().getPath() + String.format(FILENAME_OUTPUT, testname, iterations));

            gson.toJson(obj, writer);
            writer.close();

        } catch (IOException e) {
            runOnUiThread(() -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

}