package com.towery.ghac.investment.file.encryption.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * 数字证书加密解密操作
 * Created by User on 2017/6/19.
 */
public class CertificateCoderTest {

    /*二进制对称密钥key   使用CA证书 加密解密*/
   // @Test
    public void testEncryptKeyAndDecryptKeyByCA() throws Exception {

        //颁发证书 ---start
        X509CertificateTool.main(null);
        JksKeyStore.main(null);
        Csr.main(null);
        //颁发证书 ----end

        byte[] Key = AESCoder.initKey();//二进制对称密钥key

        //对称密钥加密--用CA的公钥
        byte[] EnKey = CertificateCoder.encryptByPublicKey(Key, "E:\\ca-root\\jk_smcg\\jk_smcg.cer");
        //对称密钥解密--用CA的私钥
        byte[] DeKey = CertificateCoder.decryptByPrivateKey(EnKey, "E:\\workspace_new3\\investment\\investment-file-encryption\\keystore.jks", "root", "password");

        Assert.assertArrayEquals(Key, DeKey);


    }
}
