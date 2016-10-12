package gcp.com.smartnotepad.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;

import gcp.com.smartnotepad.activity.RemindActivity;
import gcp.com.smartnotepad.global.GlobalParams;
import gcp.com.smartnotepad.receiver.TimeReceiver;

public class MainService extends Service {
    TimeReceiver receiver;
    RemindReceiver remindReceiver;
    public MainService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registeReceiver();
        registeRemindReceiver();
    }
    private void registeRemindReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalParams.START_REMIND_ACTIVITY_ACTION);
        filter.setPriority(Integer.MAX_VALUE);
        remindReceiver = new RemindReceiver();
        registerReceiver(remindReceiver,filter);
    }
    private void registeReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.setPriority(Integer.MAX_VALUE);
        receiver = new TimeReceiver();
        registerReceiver(receiver,filter);
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unregisterReceiver(remindReceiver);
    }
    public class RemindReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent data) {
            String action=data.getAction();
            if(null!=action){
                switch (action){
                    case GlobalParams.START_REMIND_ACTIVITY_ACTION:
                        Intent intent=new Intent(context, RemindActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Bundle bundle=new Bundle();
                        bundle.putString(GlobalParams.CONTENT_KEY,data.getExtras().getString(GlobalParams.CONTENT_KEY));
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                }
            }
        }
    }
}
