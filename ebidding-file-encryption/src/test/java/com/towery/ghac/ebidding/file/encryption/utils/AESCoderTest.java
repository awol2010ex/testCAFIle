package com.towery.ghac.ebidding.file.encryption.utils;

import org.junit.Assert;

/**
 * 对称加密测试
 * Created by User on 2017/6/19.
 */
public class AESCoderTest {

    /*文件对称加密解密测试*/
    //@Test
    public void testEncryptFileAndDecryptFile() throws Exception {
        String FilePath = "G:\\test\\natapp_windows_amd64_2_1_6.zip";
        String EnFilePath = "G:\\test\\natapp_windows_amd64_2_1_6-en.zip";
        String DeFilePath = "G:\\test\\natapp_windows_amd64_2_1_6-de.zip";


        //文件加密
        byte[] Key = AESCoder.initKey();//二进制对称密钥key
        AESCoder.encryptFile(FilePath, EnFilePath, Key);
        //文件解密
        AESCoder.decryptFile(EnFilePath, DeFilePath, Key);


        Assert.assertEquals(FileUtils.getHash(FilePath, "SHA1"), FileUtils.getHash(DeFilePath, "SHA1"));


    }
}
