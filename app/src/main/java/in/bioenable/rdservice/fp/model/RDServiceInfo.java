package in.bioenable.rdservice.fp.model;

public class RDServiceInfo {

    private boolean isDcPresent = false;
    private boolean isMcPresent = false;
    private String serial = null;
    private String status = null;

    public boolean isDcPresent() {
        return isDcPresent;
    }

    public void setDcPresent(boolean dcPresent) {
        isDcPresent = dcPresent;
    }

    public boolean isMcPresent() {
        return isMcPresent;
    }

    public void setMcPresent(boolean mcPresent) {
        isMcPresent = mcPresent;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
