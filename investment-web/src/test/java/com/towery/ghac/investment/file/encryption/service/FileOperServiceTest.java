package com.towery.ghac.investment.file.encryption.service;

import com.towery.ghac.investment.file.encryption.entitys.EncryptionInfo;
import com.towery.ghac.investment.file.encryption.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Date;

/** 文件操作测试
 * Created by User on 2017/6/21.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class FileOperServiceTest {

    @Autowired
    FileOperService fileOperService;

    //CA证书加密解密文件测试
    @Test
    public void  testEncryptDecryptFile() throws Exception {
        String basePath = "G:\\test";
        String FilePath = "G:\\test\\sunny day.mp4";
        String EnFilePath = "G:\\test\\sunny day-en.mp4";
        String DeFilePath = "G:\\test\\sunny day-de.mp4";

        if(new File(EnFilePath).exists()){
            new File(EnFilePath).delete();
        }
        if(new File(DeFilePath).exists()){
            new File(DeFilePath).delete();
        }

        Date startDate =new Date();
        EncryptionInfo info = fileOperService.encryptFile(FilePath, EnFilePath, "testpassword", basePath);
        Date endDate =new Date();
        long runtime =(endDate.getTime() -startDate.getTime()) ;
        log.info("加密总文件耗时:"+runtime+"ms");


        startDate =new Date();
        fileOperService.decryptFile(EnFilePath,DeFilePath,info,basePath);
        endDate =new Date();
        runtime =(endDate.getTime() -startDate.getTime()) ;
        log.info("解密文件总耗时:"+runtime+"ms");

        //删除测试临时文件
        new File(info.getCerPath()).delete();
        new File(info.getJksPath()).delete();

        Assert.assertEquals(FileUtils.getHash(FilePath, "SHA1"), FileUtils.getHash(DeFilePath, "SHA1"));

    }
}
