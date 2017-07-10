package com.towery.ghac.ebidding.file.encryption.utils;

import java.util.UUID;

/**
 * Created by User on 2017/6/20.
 */
public class UUIDUtil {
    public static String genUUID(){
        return  UUID.randomUUID().toString().replaceAll("-","");
    }
}
