package in.bioenable.rdservice.fp.helper;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Base64;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import in.bioenable.rdservice.fp.contracts.IRootChecker;

import static com.google.android.gms.common.ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED;
import static in.bioenable.rdservice.fp.model.Config.mockRootCheck;

public class RootChecker implements IRootChecker {
    private Context context;

    private static final String SAFETYNET_API_KEY = "AIzaSyBHCcl5j1zkfNetW3bMAB8yzhZ_7LT40fM";

    public RootChecker(Context context){
        this.context = context;
    }

    @Override
    public void checkForRoot(@NotNull final IRootChecker.Callback callback){
        if(mockRootCheck &&callback!=null){
//            callback.onRootChecked(Status.NON_ROOTED);
            callback.onFoundNonRooted();
        }
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int serviceAvailabilityCode = apiAvailability.isGooglePlayServicesAvailable(this.context);
        if (serviceAvailabilityCode == ConnectionResult.SUCCESS) {
//            callback.onRootChecked(Status.CHECKING);
            SafetyNet.getClient(this.context)
                    .attest(CryptoUtil.getInstance().getRequestNonce(), SAFETYNET_API_KEY)
                    .addOnSuccessListener(new OnSuccessListener<SafetyNetApi.AttestationResponse>() {
                        @Override
                        public void onSuccess(SafetyNetApi.AttestationResponse attestationResponse) {
                            boolean isRooted = false;
                            try {
                                isRooted = isRooted(attestationResponse.getJwsResult());
//                                callback.onRootChecked(isRooted? Status.ROOTED: Status.NON_ROOTED);
                                if(isRooted) callback.onFoundRooted();
                                else callback.onFoundNonRooted();
                            } catch (Exception e){
//                                if(callback!=null)callback.onRootChecked(Status.ERROR);
                                callback.onErrorOccurred();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
//                            if(callback!=null)callback.onRootChecked(Status.UNKNOWN);
                            callback.onErrorOccurred();
                        }
                    });
        } else {
            if (apiAvailability.isUserResolvableError(serviceAvailabilityCode)){
                switch (serviceAvailabilityCode) {
                    case SERVICE_VERSION_UPDATE_REQUIRED:
//                        if(callback!=null)callback.onRootChecked(Status.ERROR);
                        callback.onErrorOccurred();
                        // TODO: 14-05-2018 finish activity(ies) with user confirmation
                        break;
                }
            }
        }
    }

    private boolean isRooted(String jwsResponse) throws JSONException {
        byte[] resBytes = Base64.decode(jwsResponse.split("[.]")[1], Base64.DEFAULT);
        String json = new String(resBytes, StandardCharsets.UTF_8);
        JSONObject job = new JSONObject(json);
        boolean isCtsProfileMatch = job.getBoolean("ctsProfileMatch");
        boolean isBasicIntegrity = job.getBoolean("basicIntegrity");
        return !(isBasicIntegrity && isCtsProfileMatch);
    }

    @Override
    public boolean isAttestationOlderThanHours(String attestation, int hours){
        try{
            return (System.currentTimeMillis()-Long.parseLong(attestation))>(hours*3600000);
        } catch (Exception e){
            return true;
        }
    }
}
