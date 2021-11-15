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
import com.android.twallet.test.exceptions.InvalidPasswordException;
import com.android.twallet.test.exceptions.WalletDeleteException;
import com.android.twallet.test.utils.TWalletUtils;
import com.android.twallet.test.web3J.Web3jHandlerComponents;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class WithComponentsTests extends AppCompatActivity {

    private static final String NEW_WALLET_DIRECTORY = "/wallets";
    private static final String FILENAME_OUTPUT = "/outputs/operations-%s-twallet-output-%s.json";
    private static final String TEST_WALLET = "0xa37c7fa57fce2160cc109d43968c27a57e4f1b23";
    private static final int NUM_OF_TESTS = 6;
    private static final int INTERVAL_TIME = 10000;


    /* Variable button for starting tests */
    Button startTestButton;

    /* Variable text that says which tests are going to be done */
    CheckBox[] testOperations;

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
        setContentView(R.layout.activity_test_with_components);

        /* initializing unlockButton */
        startTestButton = findViewById(R.id.startTest);

        /* initializing testAttestation */
        testOperations = new CheckBox[NUM_OF_TESTS];
        for (int i = 0; i < NUM_OF_TESTS; i++) {
            testOperations[i] = findViewById(R.id.checkDescription + (i + 1));
        }
        resetTests();

        /* Initializing numOfReps*/
        numOfReps = findViewById(R.id.editTextNumber);

        /* initializing progressBar */
        progressBar = findViewById(R.id.progressBar);

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

                if (!Web3jHandlerComponents.web3Connection()) {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error Connecting to Server", Toast.LENGTH_SHORT).show());
                    startTestButton.setEnabled(true);
                    return;
                }

                long[] values = new long[iterations];
                progressBar.setMax(iterations * NUM_OF_TESTS);
                new Thread(() -> {
                    int progressStatus = 1;

                    if (testOperations[0].isChecked() || testOperations[1].isChecked() || testOperations[2].isChecked()) {

                        /* Test New Wallet Creation */
                        String newWalletPath = getFilesDir().getPath() + NEW_WALLET_DIRECTORY;
                        String[] wallets = new String[iterations];
                        try {
                            if (Files.exists(Paths.get(newWalletPath)))
                                deleteDirectory(newWalletPath);
                            Files.createDirectory(Paths.get(newWalletPath));

                            for (int i = 0; i < iterations; i++) {
                                long startTime = System.currentTimeMillis();
                                wallets[i] = Web3jHandlerComponents.createWallet("id" + i, "pass", newWalletPath);
                                values[i] = System.currentTimeMillis() - startTime;

                                int finalProgressStatus = progressStatus;
                                runOnUiThread(() -> progressBar.setProgress(finalProgressStatus));
                                progressStatus++;
                            }
                            testOperations[0].setText(testOperations[0].getText() + "\t \u2713");
                            writeIntoFile("create", values);
                            Thread.sleep(INTERVAL_TIME);

                        } catch (IOException e) {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error while creating directory", Toast.LENGTH_SHORT).show());
                        } catch (Exception | InvalidPasswordException e) {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error while creating wallets", Toast.LENGTH_SHORT).show());
                        }


                        /* Test Wallet Access */
                        try {
                            for (int i = 0; i < iterations; i++) {
                                long startTime = System.currentTimeMillis();
                                Web3jHandlerComponents.loadCredentials("id" + i, newWalletPath);
                                values[i] = System.currentTimeMillis() - startTime;

                                int finalProgressStatus = progressStatus;
                                runOnUiThread(() -> progressBar.setProgress(finalProgressStatus));
                                progressStatus++;
                            }
                            testOperations[1].setText(testOperations[1].getText() + "\t \u2713");
                            writeIntoFile("load", values);
                            Thread.sleep(INTERVAL_TIME);

                        } catch (Exception e) {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error while accessing wallets", Toast.LENGTH_SHORT).show());
                        }


                        /* Test Wallet Deletion */
                        File directory = new File(newWalletPath);
                        File wallet = null;
                        try {
                            for (int i = 0; i < iterations; i++) {
                                for (File file : Objects.requireNonNull(directory.listFiles()))
                                    if (file.getName().contains(wallets[i].substring(2))) {
                                        wallet = file;
                                        break;
                                    }
                                long startTime = System.currentTimeMillis();
                                Web3jHandlerComponents.deleteWallet("id" + i, "pass", wallet.getPath());
                                values[i] = System.currentTimeMillis() - startTime;

                                int finalProgressStatus = progressStatus;
                                runOnUiThread(() -> progressBar.setProgress(finalProgressStatus));
                                progressStatus++;
                            }
                            testOperations[2].setText(testOperations[2].getText() + "\t \u2713");
                            writeIntoFile("delete", values);
                            Thread.sleep(INTERVAL_TIME);

                        } catch (Exception | InvalidPasswordException | WalletDeleteException e) {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error while deleting wallets", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        progressStatus += 3 * iterations;
                        progressBar.setProgress(progressStatus);
                    }


                    String address = setupWallet();


                    /* Test Wallet Balance Retrieval */
                    if (testOperations[3].isChecked()) {
                        try {
                            Thread.sleep(INTERVAL_TIME);
                            for (int i = 0; i < iterations; i++) {
                                long startTime = System.currentTimeMillis();
                                Web3jHandlerComponents.getBalance();
                                values[i] = System.currentTimeMillis() - startTime;

                                int finalProgressStatus = progressStatus;
                                runOnUiThread(() -> progressBar.setProgress(finalProgressStatus));
                                progressStatus++;
                            }
                            testOperations[3].setText(testOperations[3].getText() + "\t \u2713");
                            writeIntoFile("balance", values);
                            Thread.sleep(INTERVAL_TIME);

                        } catch (Exception e) {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error while getting wallet balance", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        progressStatus += iterations;
                        progressBar.setProgress(progressStatus);
                    }


                    /* Test Wallet Send Ethereum */
                    if (testOperations[4].isChecked()) {
                        try {
                            for (int i = 0; i < iterations; i++) {
                                long startTime = System.currentTimeMillis();
                                Web3jHandlerComponents.transaction(address, 0.01);
                                values[i] = System.currentTimeMillis() - startTime;

                                int finalProgressStatus = progressStatus;
                                runOnUiThread(() -> progressBar.setProgress(finalProgressStatus));
                                progressStatus++;
                            }
                            testOperations[4].setText(testOperations[4].getText() + "\t \u2713");
                            writeIntoFile("transaction", values);
                            Thread.sleep(INTERVAL_TIME);

                        } catch (Exception e) {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error while exchanging Ethereum", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        progressStatus += iterations;
                        progressBar.setProgress(progressStatus);
                    }


                    /* Test Wallet Transactions Retrieval */
                    if (testOperations[5].isChecked()) {
                        try {
                            for (int i = 0; i < iterations; i++) {
                                long startTime = System.currentTimeMillis();
                                Web3jHandlerComponents.getPastTransactions();
                                values[i] = System.currentTimeMillis() - startTime;

                                int finalProgressStatus = progressStatus;
                                runOnUiThread(() -> progressBar.setProgress(finalProgressStatus));
                                progressStatus++;
                                Thread.sleep(500);
                            }
                            testOperations[5].setText(testOperations[5].getText() + "\t \u2713");
                            writeIntoFile("history", values);

                        } catch (Exception e) {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error while retrieving transactions", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        progressStatus += iterations;
                        progressBar.setProgress(progressStatus);
                    }


                    destroyWallet(address);


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
        testOperations[0].setText("Create Wallet");
        testOperations[1].setText("Load Wallet");
        testOperations[2].setText("Delete Wallet");
        testOperations[3].setText("Get Balance");
        testOperations[4].setText("Send Transaction");
        testOperations[5].setText("Transaction History");
    }

    private static void deleteDirectory(String path) {
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null)
            for (File f : files)
                f.delete();

        directory.delete();
    }

    private String setupWallet() {
        String address = "";
        try {
            address = Web3jHandlerComponents.createWallet("dummy", "pass", getFilesDir().getPath());
            TWalletUtils tWalletUtils = new TWalletUtils();
            tWalletUtils.storeCredentials("benchmark", TEST_WALLET, "pass123");
            Web3jHandlerComponents.loadCredentials("benchmark", getFilesDir().getPath());
            Web3jHandlerComponents.setAccountInfo();

        } catch (Exception | InvalidPasswordException e) {
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error while setting up other tests", Toast.LENGTH_SHORT).show());
        }
        return address;
    }

    private void destroyWallet(String address) {
        try {

            File wallet = null;
            for (File file : Objects.requireNonNull(getFilesDir().listFiles()))
                if (file.getName().contains(address.substring(2))) {
                    wallet = file;
                    break;
                }

            Web3jHandlerComponents.deleteWallet("dummy", "pass", wallet.getPath());
            TWalletUtils tWalletUtils = new TWalletUtils();
            tWalletUtils.deleteCredentials("benchmark");
            tWalletUtils.deleteData(TEST_WALLET);

        } catch (Exception | InvalidPasswordException | WalletDeleteException e) {
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error while closing up tests", Toast.LENGTH_SHORT).show());
        }
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
