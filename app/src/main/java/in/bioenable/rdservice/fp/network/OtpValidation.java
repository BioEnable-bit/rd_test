package in.bioenable.rdservice.fp.network;

import com.google.gson.annotations.SerializedName;

public class OtpValidation {

    public static class Request {

        @SerializedName("q")
        private final String q = "otp_validation";

        @SerializedName("serial_no")
        private String serialNo;

        @SerializedName("phone_no")
        private String phoneNo;

        @SerializedName("otp")
        private String otp;

        public Request(String serialNo, String phoneNo, String otp) {
            this.serialNo = serialNo;
            this.phoneNo = phoneNo;
            this.otp = otp;
        }

        @Override
        public String toString() {
            return "Request{" +
                    "q='" + q + '\'' +
                    ", serialNo='" + serialNo + '\'' +
                    ", phoneNo='" + phoneNo + '\'' +
                    ", otp='" + otp + '\'' +
                    '}';
        }
    }

    public static class Response {

        @SerializedName("result_code")
        private int resultCode;

        @SerializedName("result")
        private String result;

        @SerializedName("expiry")
        private String expiry;

        public int getResultCode() {
            return resultCode;
        }

        public String getResult() {
            return result;
        }

        public String getExpiry() {
            return expiry;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "resultCode=" + resultCode +
                    ", result='" + result + '\'' +
                    ", expiry='" + expiry + '\'' +
                    '}';
        }
    }
}
