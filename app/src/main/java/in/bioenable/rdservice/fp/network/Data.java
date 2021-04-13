package in.bioenable.rdservice.fp.network;

import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("otpv_info")
    private OtpvInfo otpvInfo;

    @SerializedName("activation_info")
    private ActivationInfo activationInfo;

    @SerializedName("version_info")
    private VersionInfo versionInfo;

    public OtpvInfo getOtpvInfo() {
        return otpvInfo;
    }

    public ActivationInfo getActivationInfo() {
        return activationInfo;
    }

    public VersionInfo getVersionInfo() {
        return versionInfo;
    }

    @Override
    public String toString() {
        return "Data{" +
                "otpvInfo=" + otpvInfo +
                ", activationInfo=" + activationInfo +
                ", versionInfo=" + versionInfo +
                '}';
    }
}
