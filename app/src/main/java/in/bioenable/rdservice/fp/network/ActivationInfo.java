package in.bioenable.rdservice.fp.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ActivationInfo implements Serializable {

    @SerializedName("client")
    private String client;

    @SerializedName("from")
    private String from;

    @SerializedName("to")
    private String to;

    @SerializedName("days_for_warning")
    private int daysForWarning;

    @SerializedName("warning_msg")
    private String warningMsg;

    @SerializedName("activation_link")
    private String activationLink;

    public String getClient() {
        return client;
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

    public String getActivationLink() {
        return activationLink;
    }
}
