package com.appsflyer;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;
import java.util.Enumeration;

import javax.security.auth.x500.X500Principal;

/**
 * Created by shacharaharon on 31/07/2016.
 */
class AFKeystoreWrapper {

    private static final String AF_KEYSTORE_PREFIX = "com.appsflyer";
    static final String AF_KEYSTORE_UID = "KSAppsFlyerId";
    static final String AF_KEYSTORE_REINSTALL_COUNTER = "KSAppsFlyerRICounter";
    private static final String RSA_ALGORITHM = "RSA";
    private static final String PROVIDER_ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String CN_ANDROID_SDK_O_APPS_FLYER = "CN=AndroidSDK, O=AppsFlyer";

    private static final String AF_KEYSTORE_EXTERNAL_DELIMITER = ",";
    private static final int KEYSTORE_CERTIFICATE_VALIDITY_YEARS = 5;
    private static final String AF_KEYSTORE_INTERNAL_DELIMITER = "=";

    private final Object lock = new Object();

    private KeyStore keystore;
    private Context context;

    private String uid;
    private int reInstallCounter;

    public AFKeystoreWrapper(Context context) {
        this.context = context;
        uid = "";
        reInstallCounter=0;
        initKeyStore();
    }

    private void initKeyStore() {
        AFLogger.afLog("Initialising KeyStore..");
        try {
            keystore = KeyStore.getInstance(PROVIDER_ANDROID_KEY_STORE);
            keystore.load(null);
        } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException e) {
            AFLogger.afLogE("Couldn't load keystore instance of type: "+PROVIDER_ANDROID_KEY_STORE,e);
        }
    }

    /**
     * Generates a new key-pair, with an alias string of this format: "<package-name>,KSAppsFlyerId=<AF-UID>,KSAppsFlyerRICounter=<number of re-installs for this app>".
     * @param appsFlyerUID - AppsFlyer UID (unique ID generated per App installation)
     */
    void createFirstInstallData(String appsFlyerUID) {
        uid = appsFlyerUID;
        reInstallCounter = 0;
        createKey(generateAliasString());
    }

    /**
     * Removes the existing key with the alias corresponding to the current number of re-installs, and creates a new similar key with <number of re-installs + 1>
     */
    void incrementReInstallCounter() {
        String currentKeyAlias = generateAliasString();
        synchronized (lock) {
            reInstallCounter++;
            deleteKey(currentKeyAlias);
        }
        createKey(generateAliasString());
    }

    /**
     * Reads the keys saved in the Android KeyStore, and looks for the alias with AF prefix.
     * @return true if a key with an AF-prefixed alias exists in the devices KeyStore, false otherwise.
     */
    boolean loadData() {
        boolean isDataExists = false;
        synchronized (lock) {
            if (keystore != null) {
                try {
                    Enumeration<String> aliases = keystore.aliases();
                    while (aliases.hasMoreElements()) {
                        String alias = aliases.nextElement();
                        if (alias != null && isAppsFlyerPrefix(alias)) {
                            String[] afData = alias.split(AF_KEYSTORE_EXTERNAL_DELIMITER);
                            if (afData.length == 3) {
                                AFLogger.afLog("Found a matching AF key with alias:\n"+alias);
                                isDataExists = true;
                                String[] ksId = afData[1].trim().split(AF_KEYSTORE_INTERNAL_DELIMITER);
                                String[] ksRICounter = afData[2].trim().split(AF_KEYSTORE_INTERNAL_DELIMITER);
                                if (ksId.length == 2 && ksRICounter.length == 2) {
                                    uid = ksId[1].trim();
                                    reInstallCounter = Integer.parseInt(ksRICounter[1].trim());
                                }
                            }
                            break;
                        }
                    }
                } catch (Throwable e) {
                    AFLogger.afLogE("Couldn't list KeyStore Aliases: " + e.getClass().getName(),e);
                }
            }
        }
        return isDataExists;
    }

    private void createKey(String alias) {
        AFLogger.afLog("Creating a new key with alias: "+alias);
        try {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, KEYSTORE_CERTIFICATE_VALIDITY_YEARS);
            AlgorithmParameterSpec spec = null;
            synchronized (lock) {
                if (!keystore.containsAlias(alias)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        spec = new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_DECRYPT | KeyProperties.PURPOSE_ENCRYPT)
                                .setCertificateSubject(new X500Principal(CN_ANDROID_SDK_O_APPS_FLYER))
                                .setCertificateSerialNumber(BigInteger.ONE)
                                .setCertificateNotBefore(start.getTime())
                                .setCertificateNotAfter(end.getTime())
                                .build();
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        spec = new KeyPairGeneratorSpec.Builder(context)
                                .setAlias(alias)
                                .setSubject(new X500Principal(CN_ANDROID_SDK_O_APPS_FLYER))
                                .setSerialNumber(BigInteger.ONE)
                                .setStartDate(start.getTime())
                                .setEndDate(end.getTime())
                                .build();
                    }
                    KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA_ALGORITHM, PROVIDER_ANDROID_KEY_STORE);
                    generator.initialize(spec);
                    generator.generateKeyPair();
                } else {
                    AFLogger.afLog("Alias already exists: " + alias);
                }
            }
        } catch (Throwable e) {
            AFLogger.afLogE("Exception " + e.getMessage() + " occurred",e);
        }
    }

    private void deleteKey(String alias) {
        AFLogger.afLog("Deleting key with alias: "+alias);
        try {
            synchronized (lock) {
                keystore.deleteEntry(alias);
            }
        } catch (KeyStoreException e) {
            AFLogger.afLogE("Exception " + e.getMessage() + " occurred", e);
        }
    }


    private boolean isAppsFlyerPrefix(String alias) {
        return alias.startsWith(AF_KEYSTORE_PREFIX);
    }

    private String generateAliasString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AF_KEYSTORE_PREFIX).append(AF_KEYSTORE_EXTERNAL_DELIMITER);
        synchronized (lock) {
            sb.append(AF_KEYSTORE_UID).append(AF_KEYSTORE_INTERNAL_DELIMITER).append(uid).append(AF_KEYSTORE_EXTERNAL_DELIMITER);
            sb.append(AF_KEYSTORE_REINSTALL_COUNTER).append(AF_KEYSTORE_INTERNAL_DELIMITER).append(reInstallCounter);
        }
        return sb.toString();
    }

    String getUid() {
        synchronized (lock) {
            return uid;
        }
    }

    int getReInstallCounter() {
        synchronized (lock) {
            return reInstallCounter;
        }
    }

    private void clearAllAFKeys() {
        synchronized (lock) {
            if (keystore != null) {
                try {
                    Enumeration<String> aliases = keystore.aliases();
                    while (aliases.hasMoreElements()) {
                        String alias = aliases.nextElement();
                        if (isAppsFlyerPrefix(alias)) {
                            AFLogger.afLog("Found AF key. Removing: " + alias);
                            keystore.deleteEntry(alias);
                        }
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

}
