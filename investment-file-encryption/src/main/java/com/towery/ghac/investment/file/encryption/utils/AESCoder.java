package com.towery.ghac.investment.file.encryption.utils;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.GeneralSecurityException;
import java.security.Key;

/**
 * Created by User on 2017/6/19.
 */
public class AESCoder {
    private static final String KEY_ALGORITHM="AES";
    private static final String CIPHER_ALGORITHM="AES/ECB/PKCS5Padding";
    private static byte[] getKey(String key)throws Exception{
        return Base64.decodeBase64(key);
    }
    private static Key toKey(byte[] key)throws Exception{
        //实例化密钥材料
        SecretKey secretKey=new SecretKeySpec(key, KEY_ALGORITHM);
        return secretKey;
    }
    public static byte[] decrypt(byte[] data,byte[] key)throws Exception{
        //还原密钥
        Key K=toKey(key);
        Cipher cipher=Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, K);
        return cipher.doFinal(data);
    }
    public static byte[] decrypt(byte[] data,String key)throws Exception{

        return decrypt(data, getKey(key));
    }
    public static byte[] encrypt(byte[] data,byte[] key)throws Exception{
        //还原密钥
        Key K=toKey(key);
        Cipher cipher=Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, K);
        return cipher.doFinal(data);
    }
    public static byte[] encrypt(byte[] data,String key)throws Exception{
        return encrypt(data, getKey(key));
    }

    public static void encryptFile(String FilePath,String EnFilePath,byte[] key)throws Exception{
        FileInputStream fileIn= new FileInputStream(FilePath);
        FileOutputStream fileOut=new FileOutputStream(EnFilePath);
        Key K=toKey(key);
        Cipher cipher=Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, K);
        crypt(fileIn, fileOut, cipher);

    }
    public static void encryptFile(String FilePath,String EnFilePath,String key)throws Exception{
        encryptFile(FilePath, EnFilePath, getKey(key));
    }
    public static void decryptFile(String EnFilePath,String DeFilePath,byte[] key)throws Exception{
        FileInputStream fileIn= new FileInputStream(EnFilePath);
        FileOutputStream fileOut=new FileOutputStream(DeFilePath);
        Key K=toKey(key);
        Cipher cipher=Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, K);
        crypt(fileIn, fileOut, cipher);

    }
    public static void decryptFile(String EnFilePath,String DeFilePath,String key)throws Exception{
        decryptFile(EnFilePath, DeFilePath, getKey(key));
    }
    public static byte[] initKey()throws Exception{
        KeyGenerator kg=KeyGenerator.getInstance(KEY_ALGORITHM);
        kg.init(128);
        //生成秘密密钥
        SecretKey secretKey=kg.generateKey();
        return secretKey.getEncoded();
    }
    public static String initKeyString()throws Exception{
        return Base64.encodeBase64String(initKey());
    }
    public static void crypt(FileInputStream in,FileOutputStream out,Cipher cipher)throws IOException,GeneralSecurityException {
        FileChannel fcIn=null;
        MappedByteBuffer mbbfIn=null;
        int blockSize=cipher.getBlockSize();
        int outputSize=cipher.getOutputSize(blockSize);
        byte[] inBytes=new byte[blockSize];
        byte[] outBytes=new byte[outputSize];
        int len=0,outLenth;
        fcIn=in.getChannel();
        mbbfIn=fcIn.map(FileChannel.MapMode.READ_ONLY, 0, fcIn.size());
        boolean more=true;
        while(more){
            len=mbbfIn.limit()-mbbfIn.position();
            if(len>blockSize)
            {   mbbfIn.get(inBytes, 0, blockSize);
                outLenth=cipher.update(inBytes, 0, blockSize, outBytes);
                out.write(outBytes,0,outLenth);
            }else{
                more=false;
            }
        }
        if(len>0){mbbfIn.get(inBytes, 0, len);
            outBytes=cipher.doFinal(inBytes,0,len);
        }else outBytes=cipher.doFinal();
        out.write(outBytes);
        if(fcIn!=null)
            fcIn.close();
    }


    public static void main(String[] args) throws Exception{
        String FilePath="G:\\test\\natapp_windows_amd64_2_1_6.zip";
        String EnFilePath="G:\\test\\natapp_windows_amd64_2_1_6-en.zip";
        String DeFilePath="G:\\test\\natapp_windows_amd64_2_1_6-de.zip";


        //文件加密
        byte[] Key=AESCoder.initKey();//二进制对称密钥key
        AESCoder.encryptFile(FilePath, EnFilePath, Key);


        //文件解密
        AESCoder.decryptFile(EnFilePath, DeFilePath, Key);
    }
}
