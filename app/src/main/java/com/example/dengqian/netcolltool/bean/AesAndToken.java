package com.example.dengqian.netcolltool.bean;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by dengqian on 2018/4/12.
 */
/**
 * AES加密，与Nodejs 保持一致
 * @author lmiky
 * @date 2014-2-25
 */
public class AesAndToken {
    private final static String SALT="QWER!@#$ASDF";
    public static final String DEFAULT_CODING = "utf-8";
    public final static String KEY="bXlEZWFyTFdX132df6bVGhlV2FudGVy";
        /**
         * 解密
         * @author lmiky
         * @date 2014-2-25
         * @param encrypted
         * @param seed
         * @return
         * @throws Exception
         */
        public static String decrypt(String encrypted, String seed) throws Exception {
            byte[] keyb = seed.getBytes(DEFAULT_CODING);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(keyb);
            SecretKeySpec skey = new SecretKeySpec(thedigest, "AES");
            Cipher dcipher = Cipher.getInstance("AES");
            dcipher.init(Cipher.DECRYPT_MODE, skey);

            byte[] clearbyte = dcipher.doFinal(toByte(encrypted));
            return new String(clearbyte);
        }

        /**
         * 加密
         * @author lmiky
         * @date 2014-2-25
         * @param content
         * @param key
         * @return
         * @throws Exception
         */



        public static String encrypt(String content, String key) throws Exception {
            byte[] input = content.getBytes(DEFAULT_CODING);

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(key.getBytes(DEFAULT_CODING));
            SecretKeySpec skc = new SecretKeySpec(thedigest, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");


            byte[] iv="1234567812345678".getBytes();
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, skc,ivSpec);


            byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
            int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
            ctLength += cipher.doFinal(cipherText, ctLength);

            return parseByte2HexStr(cipherText);
        }

        /**
         * 字符串转字节数组
         * @author lmiky
         * @date 2014-2-25
         * @param hexString
         * @return
         */
        private static byte[] toByte(String hexString) {
            int len = hexString.length() / 2;
            byte[] result = new byte[len];
            for (int i = 0; i < len; i++) {
                result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
            }
            return result;
        }

        /**
         * 字节转16进制数组
         * @author lmiky
         * @date 2014-2-25
         * @param buf
         * @return
         */
        private static String parseByte2HexStr(byte buf[]) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < buf.length; i++) {
                String hex = Integer.toHexString(buf[i] & 0xFF);
                if (hex.length() == 1) {
                    hex = '0' + hex;
                }
                sb.append(hex);
            }
            return sb.toString();
        }


    public static String md5() {
        Date date=new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH");
        String  str=SALT+df.format(date)+SALT;
        //Log.e("appOld",str);
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(str.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
