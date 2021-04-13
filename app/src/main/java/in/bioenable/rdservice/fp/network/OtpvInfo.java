package in.bioenable.rdservice.fp.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OtpvInfo implements Serializable {

    @SerializedName("phone_no")
    private String phoneNo;

    @SerializedName("from")
    private String from;

    @SerializedName("to")
    private String to;

    @SerializedName("days_for_warning")
    private int daysForWarning;

    @SerializedName("warning_msg")
    private String warningMsg;

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getDaysForWarning() {
        return daysForWarning;
    }

    public String getWarningMsg() {
        return warningMsg;
    }

    @Override
    public String toString() {
        return "OtpvInfo{" +
                "phoneNo='" + phoneNo + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", daysForWarning=" + daysForWarning +
                ", warningMsg='" + warningMsg + '\'' +
                '}';
    }
}
