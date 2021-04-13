//package in.bioenable.rdservice.fp.app;
//
//import android.app.ActivityManager;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//import in.bioenable.rdservice.fp.contracts.PersistentStore;
//import in.bioenable.rdservice.fp.service.ScannerService;
//
//public class ServiceStarter extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        App app = (App)context.getApplicationContext();
//        if(intent==null)return;
//        String action = intent.getAction();
//        if(action==null)return;
//
//        switch (action){
//            case Intent.ACTION_BOOT_COMPLETED:
//                PersistentStore store = app.store();
//                store.deleteEntry(PersistentStore.Key.ATTESTATION);
//                break;
//        }
//
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        if(am==null)return;
//        boolean isRunning = false;
//        for(ActivityManager.RunningAppProcessInfo info: am.getRunningAppProcesses()){
//            if(info.processName.equals("in.bioenable.rdservice.fp:rds")){
//                isRunning=true;
//                break;
//            }
//        }
////        if(!isRunning)context.startService(new Intent(context,RDService.class));
//        if(!isRunning)context.startService(new Intent(context, ScannerService.class));
//    }
//}
