package com.towery.ghac.ebidding.file.encryption.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import static sun.security.pkcs11.wrapper.Functions.toHexString;

/**
 * 文件操作
 * Created by User on 2017/6/19.
 */
public class FileUtils {

    /*取得文件hash码
    * hashType的值："MD5"，"SHA1"，"SHA-256"，"SHA-384"，"SHA-512"
    *
    * */
    public static String getHash(String fileName, String hashType)
            throws Exception {
        InputStream fis = new FileInputStream(fileName);
        byte buffer[] = new byte[1024];
        MessageDigest md5 = MessageDigest.getInstance(hashType);
        for (int numRead = 0; (numRead = fis.read(buffer)) > 0; ) {
            md5.update(buffer, 0, numRead);
        }
        fis.close();
        return toHexString(md5.digest());
    }
}
