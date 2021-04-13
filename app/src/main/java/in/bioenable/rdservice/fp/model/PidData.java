package in.bioenable.rdservice.fp.model;

/**
 * Created by RND on 3/21/2018.
 */

public class PidData {

    private String ci;
    private String skey;
    private String hmac;
    private String data;

    private Pid pid;

    public String getCi() {
        return ci;
    }

    public PidData setCi(String ci) {
        this.ci = ci;
        return this;
    }

    public String getHmac() {
        return hmac;
    }

    public PidData setHmac(String hmac) {
        this.hmac = hmac;
        return this;
    }

    public String getSkey() {
        return skey;
    }

    public PidData setSkey(String skey) {
        this.skey = skey;
        return this;
    }

    public Pid getPid() {
        if(pid==null)pid = new Pid();
        return pid;
    }

    public String getEncryptedEncodedPid() {
        return data==null?"":data;
    }

    public void setEncryptedEncodedPid(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new StringBuilder("PidData:\n")
                .append("ci: ").append(ci).append("\n")
                .append("skey: ").append( skey ).append("\n")
                .append("hmac: ").append(hmac).append("\n")
                .append("pid: ").append(pid.toString()).append("\n")
                .toString();
    }
}
