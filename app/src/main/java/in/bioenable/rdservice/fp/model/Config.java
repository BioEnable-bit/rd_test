package in.bioenable.rdservice.fp.model;

import in.bioenable.rdservice.fp.BuildConfig;

public class Config {

    public static final boolean mockRootCheck = false;

    public static final boolean isFingerPrintsSupported = true;
    public static final boolean isIrisSupported = false;
    public static final boolean isFaceSupported = false;

    public static final int ATTESTATION_VALIDITY = 24;

    public static final String INFO_CALL = "in.gov.uidai.rdservice.fp.INFO";
    public static final String CAPTURE_CALL = "in.gov.uidai.rdservice.fp.CAPTURE";

    public static final String RDS_VER = BuildConfig.VERSION_NAME;// need to change to "1.0.1" for FIR;
    public static final String PID_VER = "2.0";
    public static final String MODEL_ID = "BIOENABLE-BETPV";
    public static final String RDS_ID = "BIOENABLE.AND.001";
    public static final String DP_ID = "BIOENABLE.NITGEN";

    public static final String DEVICE_INFO = "DEVICE_INFO";
    public static final String RD_SERVICE_INFO = "RD_SERVICE_INFO";
    public static final String PID_DATA = "PID_DATA";
    public static final String PID_OPTIONS = "PID_OPTIONS";
    public static final String READY = "READY";
    public static final String NOTREADY = "NOTREADY";

    public static final String MNGT_REQ_VER = "1.0.8";

    public static boolean isSerialValid(String serial) {
        return serial!=null&&!serial.isEmpty()&&serial.matches("[a-zA-Z0-9]{11}");
    }
}