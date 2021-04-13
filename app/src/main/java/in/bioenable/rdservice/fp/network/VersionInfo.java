package in.bioenable.rdservice.fp.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VersionInfo implements Serializable {

    @SerializedName("version_code")
    private int versionCode;

    @SerializedName("path")
    private String path;

    @SerializedName("warning_msg")
    private String warningMsg;

    public int getVersionCode() {
        return versionCode;
    }

    public String getPath() {
        return path;
    }

    public String getWarningMsg() {
        return warningMsg;
    }

    @Override
    public String toString() {
        return "VersionInfo{" +
                "versionCode=" + versionCode +
                ", path='" + path + '\'' +
                ", warningMsg='" + warningMsg + '\'' +
                '}';
    }
}
