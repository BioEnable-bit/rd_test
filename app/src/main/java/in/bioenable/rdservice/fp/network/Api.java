package in.bioenable.rdservice.fp.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface Api {

    @POST
    Call<RegisterDevice.Response> registerDevice(@Body RegisterDevice.Request request, @Url String url);

    @POST
    Call<Init.Response> init(@Body Init.Request request, @Url String url);

    @POST
    Call<PhoneVerification.Response> requestOtp(@Body PhoneVerification.Request request, @Url String url);

    @POST
    Call<OtpValidation.Response> validateOtp(@Body OtpValidation.Request request, @Url String url);
}
