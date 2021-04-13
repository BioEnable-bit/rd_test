package in.bioenable.rdservice.fp.model;

public final class PidOptions {

    private boolean hasOpts=false;
    private boolean hasDemo=false;
    private boolean hasCustOpts=false;

    private String ver;
    private String fCount;
    private String fType;
    private String iCount;
    private String iType;
    private String pCount;
    private String pType;
    private String format;
    private String pidVer;
    private String timeout;
    private String pTimeout;
    private String pgCount;
    private String otp;
    private String wadh;
    private String posh;
    private String env ;
    private String name;
    private String value;

    private Demo demo;

    public boolean areNotPidOptions(){
        return (ver==null||ver.isEmpty())&&
                (fCount==null||fCount.isEmpty())&&
                (fType==null||fType.isEmpty())&&
                (iCount==null||iCount.isEmpty())&&
                (iType==null||iType.isEmpty())&&
                (pCount==null||pCount.isEmpty())&&
                (pType==null||pType.isEmpty())&&
                (format==null||format.isEmpty())&&
                (pidVer==null||pidVer.isEmpty())&&
                (timeout==null||timeout.isEmpty())&&
                (pTimeout==null||pTimeout.isEmpty())&&
                (pgCount==null||pgCount.isEmpty())&&
                (otp==null||otp.isEmpty())&&
                (wadh==null||wadh.isEmpty())&&
                (posh==null||posh.isEmpty())&&
                (env==null||env.isEmpty())&&
                (name==null||name.isEmpty())&&
                (value==null||value.isEmpty());
        // TODO: 04-05-2018 for CustOpts and Demo elements
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getfCount() {
        return fCount;
    }

    public void setfCount(String fCount) {
        this.fCount = fCount;
    }

    public String getfType() {
        return fType;
    }

    public void setfType(String fType) {
        this.fType = fType;
    }

    public String getiCount() {
        return iCount;
    }

    public void setiCount(String iCount) {
        this.iCount = iCount;
    }

    public String getiType() {
        return iType;
    }

    public void setiType(String iType) {
        this.iType = iType;
    }

    public String getpCount() {
        return pCount;
    }

    public void setpCount(String pCount) {
        this.pCount = pCount;
    }

    public String getpType() {
        return pType;
    }

    public void setpType(String pType) {
        this.pType = pType;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getPidVer() {
        return pidVer;
    }

    public void setPidVer(String pidVer) {
        this.pidVer = pidVer;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getpTimeout() {
        return pTimeout;
    }

    public void setpTimeout(String pTimeout) {
        this.pTimeout = pTimeout;
    }

    public String getPgCount() {
        return pgCount;
    }

    public void setPgCount(String pgCount) {
        this.pgCount = pgCount;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getWadh() {
        return wadh;
    }

    public void setWadh(String wadh) {
        this.wadh = wadh;
    }

    public String getPosh() {
        return posh;
    }

    public void setPosh(String posh) {
        this.posh = posh;
    }

    public String getEnv() {
        return env==null?"P":env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "PidOptions{" +
                "hasOpts=" + hasOpts +
                ", hasDemo=" + hasDemo +
                ", hasCustOpts=" + hasCustOpts +
                ", ver='" + ver + '\'' +
                ", fCount='" + fCount + '\'' +
                ", fType='" + fType + '\'' +
                ", iCount='" + iCount + '\'' +
                ", iType='" + iType + '\'' +
                ", pCount='" + pCount + '\'' +
                ", pType='" + pType + '\'' +
                ", format='" + format + '\'' +
                ", pidVer='" + pidVer + '\'' +
                ", timeout='" + timeout + '\'' +
                ", pTimeout='" + pTimeout + '\'' +
                ", pgCount='" + pgCount + '\'' +
                ", otp='" + otp + '\'' +
                ", wadh='" + wadh + '\'' +
                ", posh='" + posh + '\'' +
                ", env='" + env + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", demo=" + demo +
                '}';
    }

    public Demo getDemo() {
        return demo;//==null?new Demo():demo;
    }

    public void setDemo(Demo demo) {
        this.demo = demo;
    }

    public boolean hasOpts() {
        return hasOpts;
    }

    public void hasOpts(boolean hasOpts) {
        this.hasOpts = hasOpts;
    }

    public boolean hasDemo() {
        return hasDemo;
    }

    public void hasDemo(boolean hasDemo) {
        this.hasDemo = hasDemo;
    }

    public boolean hasCustOpts() {
        return hasCustOpts;
    }

    public void hasCustOpts(boolean hasCustOpts) {
        this.hasCustOpts = hasCustOpts;
    }
}
