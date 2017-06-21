package com.towery.ghac.investment.file.encryption.service;

import com.towery.ghac.investment.file.encryption.entitys.EncryptionInfo;
import com.towery.ghac.investment.file.encryption.utils.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

/** 文件操作测试
 * Created by User on 2017/6/21.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FileOperServiceTest {

    @Autowired
    FileOperService fileOperService;

    //CA证书加密解密文件测试
    @Test
    public void  testEncryptDecryptFile() throws Exception {
        String basePath = "G:\\test";
        String FilePath = "G:\\test\\natapp_windows_amd64_2_1_6.zip";
        String EnFilePath = "G:\\test\\natapp_windows_amd64_2_1_6-en.zip";
        String DeFilePath = "G:\\test\\natapp_windows_amd64_2_1_6-de.zip";

        if(new File(EnFilePath).exists()){
            new File(EnFilePath).delete();
        }
        if(new File(DeFilePath).exists()){
            new File(DeFilePath).delete();
        }
        EncryptionInfo info = fileOperService.encryptFile(FilePath, EnFilePath, "testpassword", basePath);

        fileOperService.decryptFile(EnFilePath,DeFilePath,info,basePath);

        Assert.assertEquals(FileUtils.getHash(FilePath, "SHA1"), FileUtils.getHash(DeFilePath, "SHA1"));

    }
}
