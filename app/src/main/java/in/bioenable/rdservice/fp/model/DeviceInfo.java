package in.bioenable.rdservice.fp.model;

/**
 * Created by RND on 3/21/2018.
 */

public class DeviceInfo {
    private String dpId="";
    private String rdsId="";
    private String rdsVer="";
    private String mi="";
    private String dc="";
    private String mc="";

//    private static final DeviceInfo instance = new DeviceInfo();

    public DeviceInfo(){
        reset();
    }

//    public static DeviceInfo getDeviceInfo() {
//        return instance;
//    }

    public String getDpId() {
        return dpId;
    }

    public DeviceInfo setDpId(String dpId) {
        this.dpId = dpId==null?"":dpId;
        return this;
    }

    public String getRdsId() {
        return rdsId;
    }

    public DeviceInfo setRdsId(String rdsId) {
        this.rdsId = rdsId==null?"":rdsId;
        return this;
    }

    public String getRdsVer() {
        return rdsVer;
    }

    public DeviceInfo setRdsVer(String rdsVer) {
        this.rdsVer = rdsVer==null?"":rdsVer;
        return this;
    }

    public String getDc() {
        return dc;
    }

    public DeviceInfo setDc(String dc) {
        this.dc = dc==null?"":dc;
        return this;
    }

    public String getMi() {
        return mi;
    }

    public DeviceInfo setMi(String mi) {
        this.mi = mi==null?"":mi;
        return this;
    }

    public String getMc() {
        return mc;
    }

    public DeviceInfo setMc(String mc) {
        this.mc = mc==null?"":mc;
        return this;
    }

    public void reset(){
        dc="";
        mc="";
        System.out.println("DeviceInfo: resetLastBiometricValues!");
    }

    @Override
    public String toString() {
        return new StringBuilder(DeviceInfo.class.getSimpleName()+":\n")
                .append("dpId: ").append(dpId)
                .append("rdsId: ").append(rdsId)
                .append("rdsVer: ").append(rdsVer)
                .append("mi: ").append(mi)
                .append("mc: ").append(mc)
                .append("dc: ").append(dc)
                .toString();
    }
}
