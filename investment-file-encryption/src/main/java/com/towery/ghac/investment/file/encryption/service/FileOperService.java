package com.towery.ghac.investment.file.encryption.service;

import com.towery.ghac.investment.file.encryption.entitys.EncryptionInfo;
import com.towery.ghac.investment.file.encryption.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.X509Extension;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.*;

/** 文件加密解密操作
 * Created by User on 2017/6/20.
 */
@Slf4j
@Service
public class FileOperService {

    //加密文件
    public EncryptionInfo encryptFile(String  filePath , String enFilePath , String password ,String basePath) throws Exception {
        EncryptionInfo encryptionInfo = new EncryptionInfo();
        encryptionInfo.setPassword(password);
        //颁发证书 --start

        new File(basePath).mkdirs();
        RsaTool tool = new RsaTool();
        KeyPair caKeyPair = tool.generateKeyPair(1024);
        PrivateKey caprivateKey = caKeyPair.getPrivate();
        PublicKey capublicKey = caKeyPair.getPublic();

        X509Certificate caCert = new X509CertificateTool().generateCaRootCertificate((PublicKey) capublicKey, (PrivateKey) caprivateKey);
        KeyPair keyPair = tool.generateKeyPair(1024);

        X509CertificateTool.SimpleSubjectDN subject = new X509CertificateTool.SimpleSubjectDN();
        subject.username = "GHAC";

        //CER文件路径
        String cerPath = basePath+"\\"+UUIDUtil.genUUID();
        encryptionInfo.setCerPath(cerPath);


        X509Certificate jkSmcgCert = new X509CertificateTool().generateClientCertificate(keyPair.getPublic(),caprivateKey, subject);
        new X509CertificateTool().writeCertificateToPemFile(jkSmcgCert, new File(cerPath));

        //颁发证书 --end

        //生成keystore --start

        List<Extension> extensions = new ArrayList<>();
        extensions.add(new Extension(X509Extension.basicConstraints, false, new BasicConstraints(3)));
        KeyPair rootKeyPair = KeyGen.gen("RSA", 1024);
        X500Name rootSubject = X500NameGen.gen("sk", "dev", "localhost");
        String rootCsr = Csr.genCsr(rootKeyPair, rootSubject);
        X509Certificate rootCert = Csr.genCert(rootSubject, rootKeyPair, rootCsr, cerPath, extensions);
        char[] pwd = password.toCharArray();



        //服务器证书
        //CER文件路径
        String keyStorePath = basePath+"\\"+UUIDUtil.genUUID();
        encryptionInfo.setJksPath(keyStorePath);

        KeyStore keyStore = KeyStore.getInstance("jks");
        keyStore.load(null, null);
        keyStore.setKeyEntry("root", rootKeyPair.getPrivate(), pwd, new Certificate[]{rootCert});
        keyStore.store(new FileOutputStream(keyStorePath), pwd);
        //生成keystore --end



        byte[] Key = AESCoder.initKey();//二进制对称密钥key

        //对称密钥加密--用CA的公钥
        byte[] EnKey = CertificateCoder.encryptByPublicKey(Key, cerPath);
        AESCoder.encryptFile(filePath, enFilePath, Key,EnKey);//文件加密

        return encryptionInfo;
    }


    //解密文件
    public String  decryptFile(String enFilePath,String DeFilePath ,EncryptionInfo encryptionInfo ,String basePath) throws Exception {


        //分解解密文件
        Map<String,Object> divideInfo = this.divide(enFilePath);

        byte[] EnKey = (byte[])divideInfo.get("enKeys"); //加密后的密钥

        //String enFilePathTmp = (String) divideInfo.get("enFilePathTmp");//加密后文件

        Date startDate =new Date();
        //对称密钥解密--用CA的私钥
        byte[] DeKey = CertificateCoder.decryptByPrivateKey(EnKey, encryptionInfo.getJksPath(), "root", encryptionInfo.getPassword());


        //文件解密
        AESCoder.decryptFile(enFilePath, DeFilePath, DeKey);

        //删除临时文件
       // new File(enFilePathTmp).delete();

        Date endDate =new Date();
        long runtime =(endDate.getTime() -startDate.getTime()) ;
        log.info("解密文件耗时:"+runtime+"ms");

        return DeFilePath;

    }
    //分解解密文件
    public Map<String,Object> divide(String enFilePath) throws Exception {
        Date startDate =new Date();
        File enFile = new File(enFilePath);
        // 取得文件的大小
        long enFileLength = enFile.length();
       // String enFilePathTmp= enFilePath+"-tmp-"+new Date().getTime();


        // 构建小文件的输出流
       // FileOutputStream enFileTmpOut = new FileOutputStream(enFilePathTmp);

        Map<String,Object> map =new HashMap<String,Object>();

       // map.put("enFilePathTmp",enFilePathTmp);

        // 输入文件流，即被分割的文件
        FileInputStream in = new FileInputStream(enFile);
// 读输入文件流的开始和结束下标
        long end = enFileLength-128;
        //int begin = 0;
// 从输入流中读取字节存储到输出流中
       // for (; begin < end; begin++) {
          //  enFileTmpOut.write(in.read());
      //  }
        in.skip(end);
        byte[] enKeys =new byte[128];
        in.read(enKeys);

       // map.put("enFilePathTmp",enFilePathTmp);
        map.put("enKeys",enKeys);
        //enFileTmpOut.close();

        Date endDate =new Date();
        long runtime =(endDate.getTime() -startDate.getTime()) ;
        log.info("分解文件耗时:"+runtime+"ms");

        return map;
    }


}
