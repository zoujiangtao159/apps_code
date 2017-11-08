package com.appsflyer;

import java.security.MessageDigest;
import java.util.Formatter;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gilmeroz
 * Date: 2/25/14
 * Time: 3:00 PM
 * To change this template use File | Settings | File Templates.
 */
 class HashUtils {

    static {
  //      System.loadLibrary("appsflyer");
    }

    public native String getNativeCode(String afDevKey, String timestamp, String uid);

    public String getHashCode(Map<String,Object> params) {
        String afDevKey = (String) params.get(ServerParameters.AF_DEV_KEY);
        String timestamp = (String) params.get(ServerParameters.TIMESTAMP);
        String uid = (String) params.get(ServerParameters.AF_USER_ID);
        String nativeSha1 = null;//getNativeCode(afDevKey,timestamp,uid);
     /* for checking the SHA1 algorithm */
        nativeSha1 = toSHA1(afDevKey.substring(0,7)+uid.substring(0,7)+timestamp.substring(timestamp.length()-7));
        return nativeSha1;
    }

    public String getHashCodeV2(Map<String, Object> params) {
        String toHash = (String) params.get(ServerParameters.AF_DEV_KEY);
        toHash += params.get(ServerParameters.TIMESTAMP);
        toHash += params.get(ServerParameters.AF_USER_ID);
        toHash += params.get("installDate");
        toHash += params.get("counter");
        toHash += params.get("iaecounter");
        return toSHA1(toMD5(toHash));
    }

    public static String toSHA1(String input) {
        String nativeSha1 = null;
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
//            crypt.update(nativeCode.getBytes("UTF-8"));
            crypt.update(input.getBytes("UTF-8"));
            nativeSha1 = byteToHex(crypt.digest());
        } catch(Exception e) {
            AFLogger.afLogE("Error turning "+input.substring(0,6)+".. to SHA1",e);
        }
        return nativeSha1;
    }

    public static String toMD5(String input) {
        String nativeMd5 = null;
        try {
            MessageDigest crypt = MessageDigest.getInstance("MD5");
            crypt.reset();
            crypt.update(input.getBytes("UTF-8"));
            nativeMd5 = byteToHex(crypt.digest());
        } catch(Exception e) {
            AFLogger.afLogE("Error turning "+input.substring(0,6)+".. to MD5",e);
        }
        return nativeMd5;
    }


    public static String toSha256(String data) {

        String nativeSha256 = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes());
            nativeSha256 = bytesToHex(md.digest());
        }
        catch (Exception nse) {
            AFLogger.afLogE("Error turning "+data.substring(0,6)+".. to SHA-256",nse);
        }
        return nativeSha256;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
            return result.toString();
    }


    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }



}
