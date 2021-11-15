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

public class LoggingTests extends AppCompatActivity {

    private static final String FILENAME_OUTPUT = "/outputs/logging-%s-output-%s.json";
    private static final int NUM_OF_TESTS = 1;

    /* Variable button for starting tests */
    Button startTestButton;

    /* Variable text that says which tests are going to be done */
    CheckBox[] testLogging;

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
        setContentView(R.layout.activity_test_logging);

        /* initializing unlockButton */
        startTestButton = findViewById(R.id.startTest);

        /* initializing testLogging */
        testLogging = new CheckBox[NUM_OF_TESTS];
        for (int i = 0; i < NUM_OF_TESTS; i++) {
            testLogging[i] = findViewById(R.id.checkDescription + (i + 1));
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
                new Thread(() -> {

                    if (testLogging[0].isChecked()) {
                        String output = tWalletUtils.newEntryBenchmark(iterations);

                        runOnUiThread(() -> progressBar.setProgress(iterations));
                        testLogging[0].setText(testLogging[0].getText() + "\t \u2713");
                        writeIntoFile("new_entry", output, iterations);
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
        testLogging[0].setText("New Entry");
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
