package com.android.twallet.test.tests;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class ContextSwitchTests extends AppCompatActivity {

    private static final String FILENAME_OUTPUT = "/outputs/context-switch-%s-output-%s.json";
    private static final int NUM_OF_TESTS = 1;

    /* Variable button for starting tests */
    Button startTestButton;

    /* Variable text that says which tests are going to be done */
    CheckBox[] testContextSwitch;

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
        setContentView(R.layout.activity_test_context_switch);

        /* initializing unlockButton */
        startTestButton = findViewById(R.id.startTest);

        /* initializing testLogging */
        testContextSwitch = new CheckBox[NUM_OF_TESTS];
        for (int i = 0; i < NUM_OF_TESTS; i++) {
            testContextSwitch[i] = findViewById(R.id.checkDescription + (i + 1));
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

                long[] values = new long[iterations];
                progressBar.setMax(iterations * NUM_OF_TESTS);
                new Thread(() -> {
                    int progressStatus = 1;

                    /* Test Context Switch*/
                    if (testContextSwitch[0].isChecked()) {
                        for (int i = 0; i < iterations; i++) {
                            long startTime = System.currentTimeMillis();
                            values[i] = tWalletUtils.contextSwitchBenchmark();
                            values[i] = (System.currentTimeMillis() - startTime) - values[i];

                            int finalProgressStatus = progressStatus;
                            runOnUiThread(() -> progressBar.setProgress(finalProgressStatus));
                            progressStatus++;
                        }
                        testContextSwitch[0].setText(testContextSwitch[0].getText() + "\t \u2713");
                        writeIntoFile("test", values);
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
        testContextSwitch[0].setText("Context Switch");
    }

    private void writeIntoFile(String testname, long[] values) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject obj = new JsonObject();
        JsonArray array = new Gson().toJsonTree(values).getAsJsonArray();

        obj.addProperty("length", values.length);
        obj.add("values", array);

        try {
            Writer writer = new FileWriter(getFilesDir().getPath() + String.format(FILENAME_OUTPUT, testname, values.length));

            gson.toJson(obj, writer);
            writer.close();

        } catch (IOException e) {
            runOnUiThread(() -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
