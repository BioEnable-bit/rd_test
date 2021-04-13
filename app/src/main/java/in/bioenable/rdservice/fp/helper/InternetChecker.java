package in.bioenable.rdservice.fp.helper;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by RND on 9/22/2018.
 */

public class InternetChecker extends AsyncTask<Void,Void,InternetChecker.Status> {

    public enum Status {
        CHECKING,AVAILABLE,NOT_AVAILABLE
    }

    private Callback callback;

    public interface Callback {
        void onInternetChecked(Status status);
    }

    public InternetChecker(Callback callback){
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        if(callback!=null)callback.onInternetChecked(Status.CHECKING);
    }

    @Override
    protected Status doInBackground(Void... voids) {
        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(5000);
            urlc.connect();
            return (urlc.getResponseCode() == 200)?Status.AVAILABLE:Status.NOT_AVAILABLE;
        } catch (IOException e) {
            e.printStackTrace();
            return Status.NOT_AVAILABLE;
        }
    }

    @Override
    protected void onPostExecute(Status status) {
        if(this.callback!=null)this.callback.onInternetChecked(status);
    }
}
