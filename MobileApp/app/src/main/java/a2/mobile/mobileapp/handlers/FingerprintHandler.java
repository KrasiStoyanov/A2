package a2.mobile.mobileapp.handlers;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import timber.log.Timber;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    private final String KEY_NAME = "Android Key";

    private Context context;
    private Intent activityToStart;

    private KeyStore keyStore;
    private Cipher cipher;

    public FingerprintHandler(Context context, Intent activityToStart) {
        super();

        this.context = context;
        this.activityToStart = activityToStart;
    }

    public void onFingerprintOptionClick() {
        FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

        assert fingerprintManager != null;
        if (!fingerprintManager.isHardwareDetected()) {
            Timber.e("Fingerprint Scanner not detected in Device");
        } else if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.USE_FINGERPRINT
        ) != PackageManager.PERMISSION_GRANTED) {
            Timber.e("Permission not granted to use Fingerprint Scanner");
        } else {
            assert keyguardManager != null;
            if (!keyguardManager.isKeyguardSecure()) {
                Timber.e("Add Lock to your Phone in Settings");
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                Timber.e("You should add at least 1 Fingerprint to use this Feature");
            } else {
                Timber.e("Place your Finger on Scanner to Access the App.");

                generateKey();

                if (cipherInit()) {
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    startAuth(fingerprintManager, cryptoObject);
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyGenerator keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore"
            );

            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(
                    KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build()
            );

            keyGenerator.generateKey();

        } catch (KeyStoreException | IOException | CertificateException
                | NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | NoSuchProviderException e) {

            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);

            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;

        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException |
                IOException | NoSuchAlgorithmException | InvalidKeyException e) {

            throw new RuntimeException("Failed to init Cipher", e);
        }

    }

    private void startAuth(
            FingerprintManager fingerprintManager,
            FingerprintManager.CryptoObject cryptoObject) {

        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(
                cryptoObject,
                cancellationSignal,
                0,
                this,
                null
        );
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        this.update("There was an authentication error! " + errString, false);
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("Authentication failed! ", false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        this.update("Error: " + helpString, false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("You can now access the app.", true);
    }

    private void update(String status, boolean didAuthenticate) {
        Timber.e(status);
        if (didAuthenticate) {
            context.startActivity(activityToStart);
        }
    }
}