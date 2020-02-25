package net.manmon.pkg.services.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.Collection;

public class LoaderUtils {
    public static String getChecksumForString(String str) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(str.getBytes());
        return bytesToHex(digest.digest());
    }


    public static String getChecksumForFile(String fname, String type) throws Exception {
        byte[] buffer= new byte[8192];
        int count;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fname));
        while ((count = bis.read(buffer)) > 0) {
            digest.update(buffer, 0, count);
        }
        bis.close();

        byte[] hash = digest.digest();
        return bytesToHex(hash);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String getIdStringForIds(Collection<Long> ids) {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (Long id : ids) {
            if (first) {
                first = false;
                sb.append(id);
            } else {
                sb.append("," + id);
            }
        }
        return sb.toString();
    }
}
