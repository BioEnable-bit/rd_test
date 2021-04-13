package in.bioenable.rdservice.fp.network;

import java.util.concurrent.TimeUnit;

import in.bioenable.rdservice.fp.helper.CryptoUtil;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private RetrofitClient(){}

    static {}

    private static Retrofit retrofit;
    private static Api api;

    public static Api getApi(){
        if(retrofit==null) retrofit =  new Retrofit.Builder()
                .client(new OkHttpClient.Builder()
                        .connectTimeout(1, TimeUnit.MINUTES)
                        .addInterceptor(new CryptoInterceptor())
                        .build())
                .baseUrl(CryptoUtil.getInstance().getBaseUrl())
//                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        if(api==null)api = retrofit.create(Api.class);
        return api;
    }
}
