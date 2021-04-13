package in.bioenable.rdservice.fp.network;

import com.google.gson.annotations.SerializedName;

public class RegisterDevice {

    public static class Request {

        @SerializedName("q")
        private final String q = "register";

        @SerializedName("info")
        private Info info;

        @SerializedName("env")
        private String env;

        @SerializedName("device_public_key")
        private String devicePublicKey;

        public Request(Info info, String env, String devicePublicKey) {
            this.info = info;
            this.env = env;
            this.devicePublicKey = devicePublicKey;
        }

        public String getQ() {
            return q;
        }

        public Info getInfo() {
            return info;
        }

        public void setInfo(Info info) {
            this.info = info;
        }

        public String getEnv() {
            return env;
        }

        public void setEnv(String env) {
            this.env = env;
        }

        public String getDevicePublicKey() {
            return devicePublicKey;
        }

        public void setDevicePublicKey(String devicePublicKey) {
            this.devicePublicKey = devicePublicKey;
        }

        @Override
        public String toString() {
            return "Request{" +
                    "q='" + q + '\'' +
                    ", info=" + info +
                    ", env='" + env + '\'' +
                    ", devicePublicKey='" + devicePublicKey + '\'' +
                    '}';
        }
    }

    public static class Response {

        @SerializedName("result_code")
        private int resultCode;

        @SerializedName("result")
        private String result;

        @SerializedName("env")
        private String env;

        @SerializedName("dc")
        private String dc;

        @SerializedName("mc")
        private String mc;

        @SerializedName("uidai_cert")
        private String uidaiCert;

        public int getResultCode() {
            return resultCode;
        }

        public String getResult() {
            return result;
        }

        public String getEnv() {
            return env;
        }

        public String getDc() {
            return dc;
        }

        public String getMc() {
            return mc;
        }

        public String getUidaiCert() {
            return uidaiCert;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "resultCode=" + resultCode +
                    ", result='" + result + '\'' +
                    ", env='" + env + '\'' +
                    ", dc='" + dc + '\'' +
                    ", mc='" + mc + '\'' +
                    ", uidaiCert='" + uidaiCert + '\'' +
                    '}';
        }
    }
}