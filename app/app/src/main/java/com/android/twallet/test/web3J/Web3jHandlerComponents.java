/*
 * %W% %E% Zain-Ul-Abedin
 *
 * Copyright (c) 2017-2018 Miranz Technology. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Miranz
 * technology. You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with Miranz.
 *
 */

package com.android.twallet.test.web3J;

import com.android.twallet.test.exceptions.InvalidPasswordException;
import com.android.twallet.test.exceptions.WalletDeleteException;
import com.android.twallet.test.utils.TWalletUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import java8.util.concurrent.CompletableFuture;


/**
 * This class is written for basic functions of Ethereum and web3j integration
 * Because it's handling all the basic function of web3j so that's why we named
 * it as a web3Handler.
 *
 * @author Zain-Ul-Abedin
 * @version 1.10 24 Aug 2017
 */

public class Web3jHandlerComponents {

    private static final String ETH_NETWORK = "https://rinkeby.infura.io/v3/16625fd0e5ca41c481255fe77e02eee0";
    private static final String ETHER_SCAN_NETWORK = "https://api-rinkeby.etherscan.io/api?module=account&action=txlist&address=%s&startblock=0&endblock=99999999&sort=desc&apikey=IPKTZE9U5NE95EHX5VD1C74H5AIWB5C5G8";
    private static final int TX_MAX = 10;

    /* Web3j variable 'web3' is used to implement all the functions, exist in Web3j Library */
    private static Web3j web3;

    /* TwalletUtils used to access all functions inside the framework TWallet */
    private static TWalletUtils tWalletUtils;

    /* Credentials variable 'credentials' is used to implement all the functions, exist in Credentials Library */
    private static Credentials credentials;

    /* accountInfo variable which contains the info stored in secure memory, related to the current logged in account */
    private static AccountInfo accountInfo;

    /* Transaction History Variable, used to present */
    private static List<Map<String, String>> txHistory;

    /**
     * web3Connection function is used to create the connection with the end-client node.
     *
     * @return condition
     */
    public static boolean web3Connection() {
        tWalletUtils = new TWalletUtils();
        web3 = Web3j.build(new HttpService(ETH_NETWORK));
        return true;
    }

    /**
     * setTxHistory function is used to initialize the list of transaction history
     */
    public static void setAccountInfo() {
        accountInfo = tWalletUtils.readData(credentials.getAddress());
        txHistory = new ArrayList<>();
    }

    /**
     * createWallet function is used to create a new UTC-JSON file and load into memory.
     *
     * @param password is used to access your UTC-JSON file.
     * @param path     is the location of the UTC-JSON file.
     * @throws IOException
     * @throws CipherException
     */
    public static String createWallet(String id, String password, String path) throws IOException, CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, InvalidPasswordException {

        if (password.isEmpty())
            throw new InvalidPasswordException();

        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.addProvider(new BouncyCastleProvider());

        String filename = WalletUtils.generateFullNewWalletFile(password, new File(path));
        credentials = WalletUtils.loadCredentials(password, path + "/" + filename);
        tWalletUtils.storeCredentials(id, credentials.getAddress(), password);

        return credentials.getAddress();
    }

    /**
     * deleteWallet function is used to create a new UTC-JSON file and load into memory.
     *
     * @param password is used to access your UTC-JSON file.
     * @param path     is the location of the UTC-JSON file.
     * @throws WalletDeleteException    if delete operation fails
     * @throws InvalidPasswordException if password is invalid
     */
    public static void deleteWallet(String id, String password, String path) throws WalletDeleteException, InvalidPasswordException {

        if (password.isEmpty() || !password.equalsIgnoreCase(password))
            throw new InvalidPasswordException();

        File wallet = new File(path);
        if (!wallet.delete())
            throw new WalletDeleteException();
        tWalletUtils.deleteCredentials(id);
        tWalletUtils.deleteData(id);
    }

    /**
     * loadCredentials function is used to load the UTC-JSON file from a particular path.
     *
     * @param path is the location of the UTC-JSON file.
     * @throws IOException
     * @throws CipherException
     */
    public static void loadCredentials(String id, String path) throws IOException, CipherException {
        String[] user = tWalletUtils.loadCredentials(id);
        File directory = new File(path);
        String filename = "";

        for (File file: Objects.requireNonNull(directory.listFiles()))
            if (file.getName().contains(user[0].substring(2))) {
                filename = file.getName();
                break;
            }

        credentials = WalletUtils.loadCredentials(user[1], path + "/" + filename);
    }

    /**
     * transaction function is used to send funds from your address to another Ethereum address.
     *
     * @param address    is a TO address or a address where you want to transfer funds.
     * @param ethBalance is a amount you want to send.
     * @return
     * @throws Exception
     */
    public static void transaction(String address, double ethBalance) throws Exception {
        TransactionReceipt receipt = Transfer.sendFunds(web3, credentials, address, BigDecimal.valueOf(ethBalance), Convert.Unit.ETHER).send();

        Timestamp time = new Timestamp(System.currentTimeMillis());
        addTransaction(receipt.getFrom(), address, Double.toString(ethBalance), false, time.getTime());
        accountInfo.transactions.add(new Transaction(receipt.getFrom(), address, Double.toString(ethBalance), false, time.toString()));
        tWalletUtils.writeData(credentials.getAddress(), accountInfo);
    }


    /**
     * getBalance function is used to get Balance and it returns the BigInteger value.
     *
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static void getBalance() {
        CompletableFuture<EthGetBalance> ethGetBalanceFuture = web3.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync();
        try {
            BigInteger balance = ethGetBalanceFuture.get().getBalance();
            accountInfo.balance = balance;
            tWalletUtils.writeData(credentials.getAddress(), accountInfo);
        } catch (Exception ignored) {
        }
    }

    /**
     * This function is returning transactions done by the current logged in user, in the last block
     *
     * @return address in String
     */
    public static List<Map<String, String>> getPastTransactions() {

        List<Transaction> list;
        try {
            if (!(list = getTransactions()).isEmpty()) {
                accountInfo.transactions = list;
                tWalletUtils.writeData(credentials.getAddress(), accountInfo);

                txHistory.clear();
            }

            for (Transaction tx : accountInfo.transactions)
                if (!tx.getValue().equalsIgnoreCase("0"))
                    addTransaction(tx.getFrom(), tx.getTo(), tx.getValue(), true, Long.parseLong(tx.getTimeStamp()));

        } catch (IOException ignored) {
        }

        return txHistory;
    }

    private static void addTransaction(String from, String to, String value, boolean completed, long timeStamp) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
        Map<String, String> item = new HashMap<>();

        if (completed) {
            item.put("status", "status: Completed");
        } else {
            item.put("status", "status: Pending");
        }

        Date date = new Date(timeStamp * 1000);
        item.put("date", dateFormat.format(date));

        if (from.contains(credentials.getAddress()))
            item.put("address", "to: " + to);
        else if (to.contains(credentials.getAddress()))
            item.put("address", "from: " + from);

        item.put("value", "value: " +
                Convert.toWei(value, Convert.Unit.WEI).divide(new BigDecimal("1000000000000000000")).toString());
        txHistory.add(item);
    }

    private static List<Transaction> getTransactions() throws IOException {

        List<Transaction> list = new ArrayList<>();
        Document doc = Jsoup.connect(String.format(ETHER_SCAN_NETWORK, credentials.getAddress())).ignoreContentType(true).get();
        Elements links = doc.select("body");
        Gson gson = new Gson();

        for (Element link : links) {
            JsonObject json = JsonParser.parseString(link.text()).getAsJsonObject();
            JsonArray array = json.get("result").getAsJsonArray();
            for (int i = 0; i < TX_MAX; i++)
                list.add(gson.fromJson(array.get(i), Transaction.class));
        }

        return list;
    }
}
