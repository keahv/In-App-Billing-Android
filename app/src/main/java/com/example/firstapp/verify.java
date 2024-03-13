package com.example.firstapp;

import android.text.TextUtils;
import android.util.Base64;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class verify {
    private static final String TAG = "IABUtil/Security";
    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

    public static boolean verifyPurchase(String base64PublicKey, String signData, String signature) throws IOException{
        if (TextUtils.isEmpty(signData) || TextUtils.isEmpty(base64PublicKey) || TextUtils.isEmpty(signature)){
            return false;
        }

        PublicKey key = generatePublicKey(base64PublicKey);
        return verify(key, signData,signature);
    }

    private static PublicKey generatePublicKey(String encodeedPublicKey) throws IOException{
    try{
        byte[] decodeKey = Base64.decode(encodeedPublicKey,Base64.DEFAULT);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
        return keyFactory.generatePublic(new X509EncodedKeySpec(decodeKey));
    }catch (NoSuchAlgorithmException e){
        throw new RuntimeException(e);
    }catch (InvalidKeySpecException e){
        String msg ="Invalid key specification: "+e;
        throw new IOException(msg);
    }
    }

    private static boolean verify(PublicKey publicKey, String signData, String signature){
        byte[] signatureBytes;
        try{
            signatureBytes = Base64.decode(signature,Base64.DEFAULT);
        } catch(IllegalArgumentException e){
            return false;
        }
        try{
            Signature signatureAlgorithm = Signature.getInstance(SIGNATURE_ALGORITHM);
            signatureAlgorithm.initVerify(publicKey);
            signatureAlgorithm.update(signData.getBytes());

            if (!signatureAlgorithm.verify(signatureBytes)){
                return false;
            }
            return true;
        }
        catch (IllegalArgumentException e){
            return false;
        }
        catch (NoSuchAlgorithmException | SignatureException e){
            return false;
        } catch (InvalidKeyException e) {
            return false;
        }
    }
}
