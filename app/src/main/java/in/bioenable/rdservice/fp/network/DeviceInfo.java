package in.bioenable.rdservice.fp.network;

import com.google.gson.annotations.SerializedName;

public class DeviceInfo {


    @SerializedName("serial_no")
    private String serialNo;

    public DeviceInfo(String serialNo) {
        this.serialNo = serialNo;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "serialNo='" + serialNo + '\'' +
                '}';
    }
}
