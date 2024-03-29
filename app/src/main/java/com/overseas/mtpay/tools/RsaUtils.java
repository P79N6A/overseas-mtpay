package com.overseas.mtpay.tools;

import android.text.TextUtils;

import com.log.Logger;
import com.overseas.mtpay.db.AppConfigDef;
import com.overseas.mtpay.db.AppConfigHelper;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RsaUtils {

    /** */
    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";// RSA/NONE/NoPadding,RSA/NONE/PKCS1Padding

    /**
     * String to hold name of the encryption padding.
     */
    public static final String PADDING = "RSA/NONE/PKCS1Padding";// RSA/NONE/NoPadding

    /**
     * String to hold name of the security provider.
     */
    public static final String PROVIDER = "BC";

    /** */
    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /** */
    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /** */
    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /** */
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /** */
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /*
     * 公钥加密
     */
    public static String encryptByPublicKey(String str) throws Exception {

        Cipher cipher = Cipher.getInstance(PADDING, PROVIDER);
        // 获得公钥
        Key publicKey = getPublicKey();

        // 用公钥加密
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        // 读数据源
        byte[] data = str.getBytes("UTF-8");

        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();

        return Base64Util.encode(encryptedData);
    }

    /*
     * 公钥解密
     */
    public static String decryptByPublicKey(String str) throws Exception {
        Cipher cipher = Cipher.getInstance(PADDING, PROVIDER);

        // 获得公钥
        Key publicKey = getPublicKey();

        // 用公钥解密
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        // 读数据源
        byte[] encryptedData = Base64Util.decode(str);

        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher
                        .doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher
                        .doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();

        return new String(decryptedData, "UTF-8");
    }

    /**
     * 读取公钥
     *
     * @return
     * @throws Exception
     * @author kokJuis
     * @date 2016-4-6 下午4:38:22
     * @comment
     */
    private static Key getPublicKey() throws Exception {
        String key = AppConfigHelper.getConfig(AppConfigDef.RsaKey, "");
//        String key = Constant.rsa_public_key;
        Logger.d("RsaKey文件: "+key);
        byte[] keyBytes;
        keyBytes = Base64Util.decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

}
