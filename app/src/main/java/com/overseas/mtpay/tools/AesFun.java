package com.overseas.mtpay.tools;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.log.Logger;
import com.overseas.mtpay.db.AppConfigDef;
import com.overseas.mtpay.db.AppConfigHelper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AesFun {

    public static String charest = "UTF-8";

    public static byte[] aesEncrypt(byte[] source, byte rawKeyData[])
            throws GeneralSecurityException, IOException {
        // 处理密钥
        SecretKeySpec key = new SecretKeySpec(rawKeyData, "AES");
        // 加密
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] enc = cipher.doFinal(source);
        return enc;
    }

    public static String aesDecrypt(byte[] source, byte rawKeyData[])
            throws GeneralSecurityException, UnsupportedEncodingException {
//		byte data[] = source.getBytes(charest);
//		byte password[] = rawKeyData.getBytes(charest);
        // 处理密钥
        SecretKeySpec key = new SecretKeySpec(rawKeyData, "AES");
        // 解密
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] dec = cipher.doFinal(source);
        String decStr = new String(dec);
        return decStr;
    }

    public static String bytes2hex01(byte[] bytes) {
        /**
         * 第一个参数的解释，记得一定要设置为1
         *  signum of the number (-1 for negative, 0 for zero, 1 for positive).
         */
        BigInteger bigInteger = new BigInteger(1, bytes);
        return bigInteger.toString(16);
    }

    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        // AES算法要求密鈅256位(32字节)
//        byte rawKeyData[] = "5fc87b5d4b611e893b9545cf60e0bc87".getBytes(charest);
//        // 加密内容
//        String data = "{\"sysParam\":{\"sn\":\"WP14521000000711\",\"mid\":\"100305100000001\",\"fid\":\"100305100000001\",\"operatorId\":\"18751975010\",\"token\":\"5372a767-a890-452f-a707-a56184509024\",\"operatorName\":\"张三\"},\"saleOrder\":{\"amount\":6900,\"tranLogId\":\"P100305100000001201812040001\",\"thirdTradeNo\":\"123456790123\",\"singleOddAmount\":600,\"tranAmount\":7500},\"saleOrderItem\":[{\"amount\":3,\"productId\":\"0369ed24-38b4-4d8a-ba1c-3bca43b299c6\",\"price\":2,\"productName\":\"酸辣汤\",\"changePriceFlag\":\"1\",\"qty\":1,\"realPrice\":3},{\"amount\":35000,\"productId\":\"076c7e68-06eb-44e4-8efc-de8a91f24592\",\"price\":3570,\"productName\":\"红烧排骨\",\"changePriceFlag\":\"1\",\"qty\":10,\"realPrice\":3500},{\"amount\":5500,\"productId\":\"3b6ba228-c3db-4f32-8b1f-4dc6e9b89b01\",\"price\":1200,\"productName\":\"粉蒸牛肉\",\"changePriceFlag\":\"0\",\"qty\":5,\"realPrice\":1100}],\"salePays\":[{\"payMode\":1,\"payAmount\":2,\"tranLogId\":\"P100305100000001201812040001\"}]}";
        byte rawKeyData[] = "1zDVFso8fqR317C3EXV55zLXSUwEfVL7".getBytes(charest);
        // 加密内容
        JSONObject json = new JSONObject();
        JSONObject sysParam = new JSONObject();
        sysParam.put("mid", "110505100000017");
        sysParam.put("fid", "110505100000017");
        sysParam.put("sn", "WP18819320190302001");
        json.put("sysParam", sysParam);
        String data = json.toJSONString();
        System.out.println("data:" + data);
        // 加密
        byte[] enc = aesEncrypt(data.getBytes(charest), rawKeyData);
        System.out.println("加密:" + parseByte2HexStr(enc));
//        System.out.println(Arrays.toString(enc));
//        System.out.println("enc"+enc.length);
//        String encStr =  "977c02f2f8ad243f357e88210b3752be7470615a40cebbd24636bf625db39bbf07b3f92790c67c2f77c5e0583d05c4991580d00720bf428c0ce2f02dde91cb2510f54523fa0e7d729e917f4207f59489a7eb6abd6ee30249cc622bf0cbe0c707";//parseByte2HexStr(enc);
//        System.out.println("aesEncrypt:" + data.length() + "->" + parseByte2HexStr(enc));
//        System.out.println("encStr"+encStr.length());
        //解密
//        System.out.println(parseByte2HexStr(enc));
        String decStr = aesDecrypt(parseHexStr2Byte(parseByte2HexStr(enc)), rawKeyData);
        System.out.println("解密:" + "->" + decStr);

        bodyToAes(data);
    }

    public static String bodyToAes(String data) {
        String AesKey = AppConfigHelper.getConfig(AppConfigDef.AesKey, "");
        if (TextUtils.isEmpty(AesKey)) {
            Logger.d("AesKey为空");
            return "";
        }
        try {
            byte rawKeyData[] = AesKey.getBytes(charest);
            byte[] enc = aesEncrypt(data.getBytes(charest), rawKeyData);
            String bodyAes = parseByte2HexStr(enc);
            Logger.d("Aes加密：" + bodyAes);
            return bodyAes;
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
