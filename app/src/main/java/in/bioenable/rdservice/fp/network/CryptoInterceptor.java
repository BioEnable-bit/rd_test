package in.bioenable.rdservice.fp.network;

import android.util.Log;

import java.io.IOException;

import in.bioenable.rdservice.fp.helper.CryptoUtil;
import in.bioenable.rdservice.fp.model.Config;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class CryptoInterceptor implements Interceptor {

    private CryptoUtil util = CryptoUtil.getInstance();
    private static final String TAG = "CryptoInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {

        RequestBody jsonRequestBody = chain.request().body();
        if(jsonRequestBody==null) {
            return chain.proceed(chain.request());
        }

        Log.e(TAG,"base url: "+CryptoUtil.getInstance().getBaseUrl());
        Log.e(TAG,"full url: "+CryptoUtil.getInstance().getFullUrl());

        Buffer buffer = new Buffer();
        jsonRequestBody.writeTo(buffer);
        String jsonRequestString = buffer.readUtf8();

        Log.e(TAG,jsonRequestString);

        String md5str = util.bytesToHex(util.getMD5(jsonRequestString)).toLowerCase();
        Log.e(TAG,"intercept: md5: "+md5str);

        String encryptedRequestBody = util.encryptAES256(jsonRequestString, util.getEncryptionKey(util.createGUID()));

        Log.e(TAG,"intercept: encrypted req: "+encryptedRequestBody);

        RequestBody newBody = RequestBody.create(MediaType.parse("text/xml; charset=utf-8"),encryptedRequestBody);

        Request request = chain.request().newBuilder()
                .addHeader("Content-Length",Integer.toString(encryptedRequestBody.length()))
                //.addHeader("MD5",md5str)
                .addHeader("Rds_Ver", Config.MNGT_REQ_VER)
                .post(newBody)
                .build();

        Response response = chain.proceed(request);

        if(response.body()==null) return response;

        try {

            String encryptedResponseBody = response.body().string();

            Log.e(TAG, "intercept: encrypted response: "+encryptedResponseBody);

            String jsonResponseBody = util.decryptAES256(encryptedResponseBody,util.getEncryptionKey(util.createGUID()));

            Log.e(TAG,"intercept: json response: "+jsonResponseBody);

//        ResponseBody responseBody = ResponseBody.create(MediaType.parse("text/xml"),jsonResponseBody);
            ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json"),jsonResponseBody);

            return response.newBuilder()
                    .body(responseBody)
                    .build();
        } catch (Exception e){
            e.printStackTrace();
            return response.newBuilder().body(null).build();
        }
    }
}