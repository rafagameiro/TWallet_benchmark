package com.android.twallet.test.web3J;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transaction {

    private String blkNumber;

    @Expose
    private String timeStamp;

    private String txHash;

    private String nonce;

    private String blkHash;

    private String txIndex;

    @Expose
    private String from;

    @Expose
    private String to;

    @Expose
    private String value;

    private String gas;

    private String gasPrice;

    private String isError;

    @Expose
    @SerializedName("status")
    private String txReceiptStatus;

    private String input;

    private String contractAddress;

    private String cumulativeGasUsed;

    private String gasUsed;

    private String confirmations;

    public Transaction() {
    }

    public Transaction(String from, String to, String value, boolean status, String timeStamp) {
        this.from = from;
        this.to = to;
        this.value = value;
        this.txReceiptStatus = status ? "complete" : "pending";
        this.timeStamp = timeStamp;
    }

    public String getBlkNumber() {
        return blkNumber;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getTxHash() {
        return txHash;
    }

    public String getNonce() {
        return nonce;
    }

    public String getBlkHash() {
        return blkHash;
    }

    public String getTxIndex() {
        return txIndex;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getValue() {
        return value;
    }

    public String getGas() {
        return gas;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public String getIsError() {
        return isError;
    }

    public String getTxReceiptStatus() {
        return txReceiptStatus;
    }

    public String getInput() {
        return input;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public String getCumulativeGasUsed() {
        return cumulativeGasUsed;
    }

    public String getGasUsed() {
        return gasUsed;
    }

    public String getConfirmations() {
        return confirmations;
    }
}
