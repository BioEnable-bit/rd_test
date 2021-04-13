package in.bioenable.rdservice.fp.network;

import com.google.gson.annotations.SerializedName;

public class Init {

    public static class Request {

        @SerializedName("q")
        private final String q = "init";

        @SerializedName("info")
        private Info info;

        public Request(Info info) {
            this.info = info;
        }

        @Override
        public String toString() {
            return "Request{" +
                    "q='" + q + '\'' +
                    ", info=" + info +
                    '}';
        }
    }

    public static class Response {

        @SerializedName("result_code")
        private int resultCode;

        @SerializedName("result")
        private String result;

        @SerializedName("data")
        private Data data;

        @SerializedName("activation_link")
        private String activationLink;

        public int getResultCode() {
            return resultCode;
        }

        public String getResult() {
            return result;
        }

        public Data getData() {
            return data;
        }

        public String getActivationLink() {
            return activationLink;
        }
    }

}
