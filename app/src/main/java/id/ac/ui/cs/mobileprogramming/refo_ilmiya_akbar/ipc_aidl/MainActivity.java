package id.ac.ui.cs.mobileprogramming.refo_ilmiya_akbar.ipc_aidl;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    IBoundService mBoundServiceInterface;
    boolean mServiceConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView timestampText = findViewById(R.id.timestamp_text);
        Button printTimestampButton = findViewById(R.id.print_timestamp);
        Button stopServiceButon = findViewById(R.id.stop_service);
        printTimestampButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mServiceConnected) {
                    try {
                        timestampText.setText((mBoundServiceInterface)
                                .getTimestamp());
                        Log.d("pid", "hello");
                        ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                        assert manager != null;
                        List<ActivityManager.RunningAppProcessInfo> services = manager.getRunningAppProcesses();
                        Log.d("pid activity", String.valueOf(android.os.Process.myPid()));
                        String service1pid = String.valueOf(services.get(1).pid);
                        Log.d("pid service", service1pid);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        stopServiceButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mServiceConnected) {
                    unbindService(mServiceConnection);
                    mServiceConnected = false;
                }
                Intent intent = new Intent(MainActivity.this,
                        BoundService.class);
                stopService(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, BoundService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mServiceConnected) {
            unbindService(mServiceConnection);
            mServiceConnected = false;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceConnected = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundServiceInterface = IBoundService.Stub.asInterface(service);
            mServiceConnected = true;
        }
    };
}
