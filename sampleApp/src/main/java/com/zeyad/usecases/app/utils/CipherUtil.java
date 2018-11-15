package com.zeyad.usecases.app.utils;

import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.support.annotation.RequiresApi;
import android.util.Base64;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author by ZIaDo on 8/15/17.
 */
public class CipherUtil {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static KeyStore.Entry key(Context context)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException,
            NoSuchProviderException, InvalidAlgorithmParameterException, UnrecoverableEntryException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        boolean containAlias = keyStore.containsAlias("com.zeyad.usecases.app");
        if (!containAlias) {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
            Calendar start = Calendar.getInstance(Locale.ENGLISH);
            Calendar end = Calendar.getInstance(Locale.ENGLISH);
            end.add(Calendar.YEAR, 99);
            KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context).setAlias("com.zeyad.usecases.app")
                    .setSubject(new X500Principal(X500Principal.CANONICAL)).setSerialNumber(BigInteger.ONE)
                    .setStartDate(start.getTime()).setEndDate(end.getTime()).build();
            kpg.initialize(spec);
            kpg.generateKeyPair();
        }
        return keyStore.getEntry("com.zeyad.usecases.app", null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String decrypt(String cipherText) throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException, UnrecoverableEntryException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        KeyStore.Entry entry = keyStore.getEntry("com.zeyad.usecases.app", null);
        if (entry instanceof KeyStore.PrivateKeyEntry) {
            return new String(cipherUsingKey(null, ((KeyStore.PrivateKeyEntry) entry).getPrivateKey(), false,
                    Base64.decode(cipherText.getBytes(UTF_8), Base64.DEFAULT)));
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static byte[] decryptToByteArray(String cipherText) throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException, UnrecoverableEntryException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        KeyStore.Entry entry = keyStore.getEntry("com.zeyad.usecases.app", null);
        if (entry instanceof KeyStore.PrivateKeyEntry) {
            return cipherUsingKey(null, ((KeyStore.PrivateKeyEntry) entry).getPrivateKey(), false,
                    Base64.decode(cipherText.getBytes(UTF_8), Base64.DEFAULT));
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String encrypt(Context context, String plainText)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableEntryException,
            NoSuchProviderException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        KeyStore.Entry entry = key(context);
        if (entry instanceof KeyStore.PrivateKeyEntry) {
            return new String(
                    Base64.encode(cipherUsingKey(((KeyStore.PrivateKeyEntry) entry).getCertificate().getPublicKey(),
                            null, true, plainText.getBytes(UTF_8)), Base64.DEFAULT));
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String encrypt(Context context, byte[] plainText)
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableEntryException,
            NoSuchProviderException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        KeyStore.Entry entry = key(context);
        if (entry instanceof KeyStore.PrivateKeyEntry) {
            return new String(
                    Base64.encode(cipherUsingKey(((KeyStore.PrivateKeyEntry) entry).getCertificate().getPublicKey(),
                            null, true, plainText), Base64.DEFAULT));
        }
        return null;
    }

    public static byte[] cipherUsingKey(PublicKey publicKey, PrivateKey privateKey, boolean encrypt, byte[] bytes)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException {
        Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        inCipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.ENCRYPT_MODE, encrypt ? publicKey : privateKey);
        return inCipher.doFinal(bytes);
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public static String createSecureRealmKey(Context context)
//            throws IOException, CertificateException, NoSuchAlgorithmException, InvalidKeyException,
//            UnrecoverableEntryException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
//            NoSuchProviderException, BadPaddingException, NoSuchPaddingException, KeyStoreException {
//        byte[] realmkey = new byte[RealmConfiguration.KEY_LENGTH];
//        new SecureRandom().nextBytes(realmkey);
//        return encrypt(context, realmkey);
//    }
//
//    // disable in manifest allowBackUp = true for encryption
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public static byte[] getRealmKey(Context context)
//            throws IOException, CertificateException, NoSuchAlgorithmException, InvalidKeyException,
//            UnrecoverableEntryException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
//            BadPaddingException, NoSuchPaddingException, KeyStoreException, NoSuchProviderException {
//        String loadedKey = PreferenceManager.getDefaultSharedPreferences(context).getString("realmKey", "");
//        if (loadedKey.isEmpty()) {
//            loadedKey = createSecureRealmKey(context);
//        }
//        return decryptToByteArray(loadedKey);
//    }
}
