package in.bioenable.rdservice.fp.network;

import com.google.gson.annotations.SerializedName;

public class MachineInfo {

    @SerializedName("machine_id")
    private String machineId;

    @SerializedName("machine_ip")
    private String machineIp;

    @SerializedName("os")
    private String os;

    @SerializedName("os_ver")
    private String osVer;

    public MachineInfo(String machineId, String machineIp, String os, String osVer) {
        this.machineId = machineId;
        this.machineIp = machineIp;
        this.os = os;
        this.osVer = osVer;
    }

    @Override
    public String toString() {
        return "MachineInfo{" +
                "machineId='" + machineId + '\'' +
                ", machineIp='" + machineIp + '\'' +
                ", os='" + os + '\'' +
                ", osVer='" + osVer + '\'' +
                '}';
    }
}
