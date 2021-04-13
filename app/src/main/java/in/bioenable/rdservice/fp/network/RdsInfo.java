package in.bioenable.rdservice.fp.network;

import com.google.gson.annotations.SerializedName;

public class RdsInfo {

    @SerializedName("uidai_mi")
    private String uidaiMi;

    @SerializedName("uidai_dpid")
    private String uidaiDpid;

    @SerializedName("uidai_rds_id")
    private String uidaiRdsId;

    @SerializedName("uidai_rds_ver")
    private String uidaiRdsVer;

    @SerializedName("internal_ver_name")
    private String internalVerName;

    @SerializedName("internal_ver_code")
    private int internalVerCode;

    public RdsInfo(String uidaiMi, String uidaiDpid, String uidaiRdsId, String uidaiRdsVer, String internalVerName, int internalVerCode) {
        this.uidaiMi = uidaiMi;
        this.uidaiDpid = uidaiDpid;
        this.uidaiRdsId = uidaiRdsId;
        this.uidaiRdsVer = uidaiRdsVer;
        this.internalVerName = internalVerName;
        this.internalVerCode = internalVerCode;
    }

    @Override
    public String toString() {
        return "RdsInfo{" +
                "uidaiMi='" + uidaiMi + '\'' +
                ", uidaiDpid='" + uidaiDpid + '\'' +
                ", uidaiRdsId='" + uidaiRdsId + '\'' +
                ", uidaiRdsVer='" + uidaiRdsVer + '\'' +
                ", internalVerName='" + internalVerName + '\'' +
                ", internalVerCode=" + internalVerCode +
                '}';
    }
}
