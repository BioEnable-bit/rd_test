package in.bioenable.rdservice.fp.helper;

import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;

public class KeysCreator extends AsyncTask<String,Void, KeyPair> {

    private AlgorithmParameterSpec spec;

    private static final String TAG = "KeysCreator";

    @Nullable
    private OnKeysCreatedCallback callback;

    public KeysCreator(AlgorithmParameterSpec spec, @Nullable OnKeysCreatedCallback callback){
        Log.e(TAG, "KeysCreator: ");
        this.spec = spec;
        this.callback = callback;
    }

    @Override
    protected KeyPair doInBackground(String... strings) {
        Log.e(TAG, "doInBackground: ");
        try {
            return CryptoUtil.getInstance().createKeys(spec);
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            Log.e(TAG, "doInBackground: "+e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(KeyPair keyPair) {
        Log.e(TAG, "onPostExecute: ");
        if(callback==null)return;
        if(keyPair==null) callback.onFailure();
        else callback.onKeysCreated(keyPair);
    }

    public void create(){
        this.execute();
    }

    public interface OnKeysCreatedCallback {

        void onKeysCreated(@NonNull KeyPair keyPair);

        void onFailure();

    }
}
