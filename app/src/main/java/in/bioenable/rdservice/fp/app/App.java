package in.bioenable.rdservice.fp.app;

import android.app.Application;

import org.jetbrains.annotations.NotNull;

import in.bioenable.rdservice.fp.contracts.Manager;
import in.bioenable.rdservice.fp.contracts.CryptoHelper;
import in.bioenable.rdservice.fp.contracts.IRootChecker;
import in.bioenable.rdservice.fp.contracts.NotificationHelper;
import in.bioenable.rdservice.fp.contracts.PersistentStore;
import in.bioenable.rdservice.fp.contracts.WebService;
import in.bioenable.rdservice.fp.helper.CryptoHelperImpl;
import in.bioenable.rdservice.fp.helper.NotificationHelperImpl;
import in.bioenable.rdservice.fp.model.Store;
import in.bioenable.rdservice.fp.network.WebServiceImpl;
import in.bioenable.rdservice.fp.helper.RootChecker;

/**
 * Created by RND on 9/21/2018.
 */

public class App extends Application implements in.bioenable.rdservice.fp.contracts.App {

    private PersistentStore store;
    private CryptoHelper cryptoHelper;
    private WebService webService;
    private NotificationHelper notificationHelper;
    private RootChecker rootChecker;
    private Manager appManager;

    @Override
    public void onCreate() {
        super.onCreate();
        store  = new Store(this);
        cryptoHelper = new CryptoHelperImpl(this);
    }

    @NotNull
    public PersistentStore store(){
        return store;
    }

    @NotNull
    @Override
    public CryptoHelper cryptoHelper() {
        return cryptoHelper;
    }

    @NotNull
    @Override
    public WebService webService() {
        if(webService==null) webService = new WebServiceImpl(this);
        return webService;
    }

    @NotNull
    @Override
    public NotificationHelper notificationHelper() {
        if(notificationHelper==null) notificationHelper = new NotificationHelperImpl(this);
        return notificationHelper;
    }

    //@yogesh changes regarding safetynet update 17/05/23 commented below code
//    @NotNull
//    @Override
//    public IRootChecker rootChecker() {
//        if(rootChecker==null)rootChecker = new RootChecker(this);
//        return rootChecker;
//    }


    @NotNull
    @Override
    public Manager manager() {
        if(appManager==null)appManager = new AppManager(this);
        return appManager;
    }
}