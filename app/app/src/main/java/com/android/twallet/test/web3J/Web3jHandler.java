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

import android.os.FileUtils;
import android.widget.Toast;

import com.android.twallet.test.exceptions.InvalidPasswordException;
import com.android.twallet.test.exceptions.WalletDeleteException;
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
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

public class Web3jHandler {

    private static final String ETH_NETWORK = "https://rinkeby.infura.io/v3/16625fd0e5ca41c481255fe77e02eee0";
    private static final String ETHER_SCAN_NETWORK = "https://api-rinkeby.etherscan.io/api?module=account&action=txlist&address=%s&startblock=0&endblock=99999999&sort=desc&apikey=IPKTZE9U5NE95EHX5VD1C74H5AIWB5C5G8";
    private static final int TX_MAX = 10;

    /* Web3j variable 'web3' is used to implement all the functions, exist in Web3j Library */
    private static Web3j web3;

    /* Credentials variable 'credentials' is used to implement all the functions, exist in Credentials Library */
    private static Credentials credentials;

    private static List<Map<String, String>> txHistory;

    /**
     * web3Connection function is used to create the connection with the end-client node.
     *
     * @return condition
     */
    public static boolean web3Connection() {
        web3 = Web3j.build(new HttpService(ETH_NETWORK));
        return web3 != null;
    }

    /**
     * setTxHistory function is used to initialize the list of transaction history
     *
     * @return condition
     */
    public static boolean setTxHistory() {
        txHistory = new ArrayList<>();
        return true;
    }

    /**
     * createWallet function is used to create a new UTC-JSON file and load into memory.
     *
     * @param password is used to access your UTC-JSON file.
     * @param path     is the location of the UTC-JSON file.
     * @throws IOException
     * @throws CipherException
     */
    public static String createWallet(String password, String path) throws IOException, CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, InvalidPasswordException {

        if (password.isEmpty())
            throw new InvalidPasswordException();

        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.addProvider(new BouncyCastleProvider());

        String filename = WalletUtils.generateFullNewWalletFile(password, new File(path));
        credentials = WalletUtils.loadCredentials(password, path + "/" + filename);
        return credentials.getAddress();
    }

    /**
     * exportWallet function is used to create a new UTC-JSON file and load into memory.
     *
     * @param password is used to access your UTC-JSON file.
     * @param opath    is the location of the UTC-JSON file.
     * @param dpath    location of the exported UTC-JSON file.
     * @throws IOException
     * @throws CipherException
     */
    public static void exportWallet(String password, String opath, String dpath) throws WalletDeleteException, IOException, InvalidPasswordException {

        if (password.isEmpty() || !password.equalsIgnoreCase(password))
            throw new InvalidPasswordException();

        File directory = new File(opath);
        if (Objects.requireNonNull(directory.listFiles()).length > 1)
            throw new WalletDeleteException();
        if (!Objects.requireNonNull(directory.listFiles())[0].canRead())
            throw new WalletDeleteException();

        File destiny = new File(dpath + Objects.requireNonNull(directory.listFiles())[0].getName());
        FileUtils.copy(new FileInputStream(Objects.requireNonNull(directory.listFiles())[0]), new FileOutputStream(destiny));
    }

    /**
     * deleteWallet function is used to create a new UTC-JSON file and load into memory.
     *
     * @param password is used to access your UTC-JSON file.
     * @param path     is the location of the UTC-JSON file.
     * @throws IOException
     * @throws CipherException
     */
    public static void deleteWallet(String password, String path) throws WalletDeleteException, InvalidPasswordException {

        if (password.isEmpty() || !password.equalsIgnoreCase(password))
            throw new InvalidPasswordException();

        File wallet = new File(path);
        if (!wallet.delete())
            throw new WalletDeleteException();
    }

    /**
     * loadCredentials function is used to load the UTC-JSON file from a particular path.
     *
     * @param password is used to access your UTC-JSON file.
     * @param path     is the location of the UTC-JSON file.
     * @throws IOException
     * @throws CipherException
     */
    public static void loadCredentials(String password, String path) throws IOException, CipherException {
        credentials = WalletUtils.loadCredentials(password, path);
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
        CompletableFuture<TransactionReceipt> receipt = Transfer.sendFunds(web3, credentials, address, BigDecimal.valueOf(ethBalance), Convert.Unit.ETHER).sendAsync();
        //addTransaction(receipt.getFrom(), receipt.getTo(), Double.toString(ethBalance), false, new Timestamp(System.currentTimeMillis()).getTime());
    }


    /**
     * getBalance function is used to get Balance and it returns the BigInteger value.
     *
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static void getBalance() {
        CompletableFuture<EthGetBalance> ethGetBalanceFuture = web3.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync();
        try {
            ethGetBalanceFuture.get().getBalance();
        } catch (Exception ignored) {

        }
    }

    /**
     * This function is returning transactions done by the current logged in user, in the last block
     *
     */
    public static void getPastTransactions() {

        try {
            for (Transaction tx : getTransactions())
                if (!tx.getValue().equalsIgnoreCase("0"))
                    addTransaction(tx.getFrom(), tx.getTo(), tx.getValue(), true, Long.parseLong(tx.getTimeStamp()));

        } catch (IOException ignored) {
        }

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
            for (int i = 0; i < TX_MAX && i < array.size(); i++)
                list.add(gson.fromJson(array.get(i), Transaction.class));
            txHistory.clear();
        }

        return list;
    }

}
