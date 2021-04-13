package in.bioenable.rdservice.fp.network;

import com.google.gson.annotations.SerializedName;

public class PhoneVerification {

    public static class Request {

        @SerializedName("q")
        private final String q = "phone_verification";

        @SerializedName("serial_no")
        private String serialNo;

        @SerializedName("phone_no")
        private String phoneNo;

        public Request(String serialNo, String phoneNo) {
            this.serialNo = serialNo;
            this.phoneNo = phoneNo;
        }

        public String getQ() {
            return q;
        }

        public String getSerialNo() {
            return serialNo;
        }

        public void setSerialNo(String serialNo) {
            this.serialNo = serialNo;
        }

        public String getPhoneNo() {
            return phoneNo;
        }

        public void setPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
        }

        @Override
        public String toString() {
            return "Request{" +
                    "q='" + q + '\'' +
                    ", serialNo='" + serialNo + '\'' +
                    ", phoneNo='" + phoneNo + '\'' +
                    '}';
        }
    }

    public static class Response {

        @SerializedName("timeout")
        private int timeout;

        @SerializedName("result_code")
        private int resultCode;

        @SerializedName("result")
        private String result;

        public int getTimeout() {
            return timeout;
        }

        public int getResultCode() {
            return resultCode;
        }

        public String getResult() {
            return result;
        }
    }
}
