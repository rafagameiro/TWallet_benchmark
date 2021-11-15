package com.android.twallet.test.utils;

import static com.android.twallet.test.utils.CryptoSchemes.SignMode.SHA256withECDSA;

import com.android.twallet.test.web3J.AccountInfo;

public class TWalletUtils {

    static {
        System.loadLibrary("twallet");
    }

    public TWalletUtils() {
    }

    public boolean attestComponents(int nonce) {
        String[] values = attestComponentsTA(nonce);
        return AttestationValidator.validateProof(values[0], values[1], values[2], values[3]);
    }

    /* Calls storeCredentialsTA method, using the AUTH_ID as serviceID */
    public boolean storeCredentials(String id, String address, String password) {
        return storeCredentialsTA(id, address, password);
    }

    /* Calls loadCredentialsTA method, using the AUTH_ID as serviceID */
    public String[] loadCredentials(String id) {
        return loadCredentialsTA(id);
    }

    /* Calls loadCredentialsTA method, using the AUTH_ID as serviceID */
    public boolean deleteCredentials(String id) {
        return deleteCredentialsTA(id);
    }

    public AccountInfo readData(String id) {
        return XMLParser.parseFrom(readDataTA(id));
    }

    public boolean writeData(String id, AccountInfo info) {
        return writeDataTA(id, XMLParser.parseTo(info));
    }

    public boolean deleteData(String id) {
        return deleteDataTA(id);
    }

    public String getLoggingData() {
        return getLoggingDataTA();
    }

    public boolean seTrigger(boolean trigger) {
        return setTriggerTA(trigger);
    }

    /*------------------------------- Benchmark operations----------------------------------------*/

    public int contextSwitchBenchmark() {
        return contextSwitchBenchmarkTA();
    }

    public void prepareAttestBenchmark() {
        prepareAttestBenchmarkTA();
    }

    public String attestBenchmark(int signMode, int keySize, int nonce) {
        String[] values;
        if (signMode == SHA256withECDSA.getCode()) {
            values = attestBenchmarkECTA(signMode, keySize, nonce);
            return AttestationValidator.validateProofBenchmarkEC(values[0], values[1], values[2], values[3]) + "";
        } else {
            values = attestBenchmarkTA(signMode, keySize, nonce);
            return AttestationValidator.validateProofBenchmark(signMode, values[0], values[1], values[2], values[3]);
        }
    }

    public String keyGenBenchmark(int keyType, int keySize, int reps) {
        return keyGenBenchmarkTA(keyType, keySize, reps);
    }

    public String storeCredentialsBenchmark(int reps) {
        return storeCredentialsBenchmarkTA(reps);
    }

    public String loadCredentialsBenchmark(int reps) {
        return loadCredentialsBenchmarkTA(reps);
    }

    public String deleteCredentialsBenchmark(int reps) {
        return deleteCredentialsBenchmarkTA(reps);
    }

    public String readDataBenchmark(int reps) {
        return readDataBenchmarkTA(reps);
    }

    public String writeDataBenchmark(int reps) {
        return writeDataBenchmarkTA(reps);
    }

    public String deleteDataBenchmark(int reps) {
        return deleteDataBenchmarkTA(reps);
    }

    public String newEntryBenchmark(int reps) {
        return newEntryBenchmarkTA(reps);
    }

    public String filterOperationBenchmark(int reps) {
        return filterOperationBenchmarkTA(reps);
    }


    /*---------------------------------- Native Methods-------------------------------------------*/

    /* Performs operation to later measure the Context Switch */
    public static native int contextSwitchBenchmarkTA();

    /* Attests Components and verifies if all are secure */
    public static native String[] attestComponentsTA(int nonce);

    /* Writes data into the secure storage component */
    private static native boolean writeDataTA(String id, String content);

    /* Read data from the secure storage component using string id */
    private static native String readDataTA(String id);

    /* Deletes data from the secure storage component using string id */
    private static native boolean deleteDataTA(String id);

    /* Stores a new credentials entry into the authentication service component */
    private static native boolean storeCredentialsTA(String serviceID, String id, String password);

    /* Loads the credentials entry stored in the authentication service component */
    private static native String[] loadCredentialsTA(String serviceID);

    /* Loads the credentials entry stored in the authentication service component */
    private static native boolean deleteCredentialsTA(String serviceID);

    /* Retrieves the generated log, stored inside the Secure World */
    private static native String getLoggingDataTA();

    /* Changes the filtering behaviour, depending on the trigger value */
    private static native boolean setTriggerTA(boolean trigger);

    /*---------------------------------- Benchmark functions-------------------------------------------*/

    /* Attests Components and verifies if all are secure */
    public static native boolean prepareAttestBenchmarkTA();

    /* Attests Components and verifies if all are secure */
    public static native String[] attestBenchmarkTA(int signMode, int keySize, int nonce);

    /* Attests Components and verifies if all are secure */
    public static native String[] attestBenchmarkECTA(int signMode, int keySize, int nonce);

    /* Attests Components and verifies if all are secure */
    public static native String keyGenBenchmarkTA(int keyType, int keySize, int reps);

    /* Based on the reps value, generates new data entries */
    private static native String writeDataBenchmarkTA(int reps);

    /* Based on the reps value, retrieves data */
    private static native String readDataBenchmarkTA(int reps);

    /* Based on the reps value, deletes data */
    private static native String deleteDataBenchmarkTA(int reps);

    /* Based on the reps value, generates new credentials entries */
    private static native String storeCredentialsBenchmarkTA(int reps);

    /* Based on the reps value, loads credentials info */
    private static native String loadCredentialsBenchmarkTA(int reps);

    /* Based on the reps value, deletes credentials */
    private static native String deleteCredentialsBenchmarkTA(int reps);

    /* Based on the reps value, generates new entries in the log */
    private static native String newEntryBenchmarkTA(int reps);

    /* Based on the reps value, verifies if the user has authorization to perform an activity */
    private static native String filterOperationBenchmarkTA(int reps);
}
