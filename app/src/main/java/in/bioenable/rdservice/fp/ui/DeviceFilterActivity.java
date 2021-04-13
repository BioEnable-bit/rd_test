package in.bioenable.rdservice.fp.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

//import androidx.appcompat.app.AppCompatActivity;

import in.bioenable.rdservice.fp.service.ScannerService;

import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

public class DeviceFilterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        if(window!=null){
            window.setFlags(FLAG_NOT_TOUCH_MODAL,0);
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        startService(new Intent(this,ScannerService.class));
        finish();
    }
}
