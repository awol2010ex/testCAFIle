package com.towery.ghac.ebidding.file.encryption.entitys;

/** 加密文件相关信息
 * Created by User on 2017/6/20.
 */
public class EncryptionInfo {
    public String getJksPath() {
        return jksPath;
    }

    public String getCerPath() {
        return cerPath;
    }

    public void setCerPath(String cerPath) {
        this.cerPath = cerPath;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setJksPath(String jksPath) {
        this.jksPath = jksPath;
    }

    private String jksPath;//keyStore路径

    private String cerPath ;//cer证书路径

    private String password;//私钥密码
}

