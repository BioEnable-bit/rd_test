package in.bioenable.rdservice.fp.network;

import com.google.gson.annotations.SerializedName;

public class Info {

    @SerializedName("device_info")
    private DeviceInfo deviceInfo;

    @SerializedName("rds_info")
    private RdsInfo rdsInfo;

    @SerializedName("machine_info")
    private MachineInfo machineInfo;

    public Info(DeviceInfo deviceInfo, RdsInfo rdsInfo, MachineInfo machineInfo/*, UserInfo userInfo*/) {
        this.deviceInfo = deviceInfo;
        this.rdsInfo = rdsInfo;
        this.machineInfo = machineInfo;
    }

    @Override
    public String toString() {
        return "Info{" +
                "deviceInfo=" + deviceInfo +
                ", rdsInfo=" + rdsInfo +
                ", machineInfo=" + machineInfo +
                '}';
    }
}
