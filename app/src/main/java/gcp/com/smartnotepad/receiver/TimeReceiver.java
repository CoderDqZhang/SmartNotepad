package gcp.com.smartnotepad.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import gcp.com.smartnotepad.db.DataHelper;
import gcp.com.smartnotepad.global.GlobalParams;
import gcp.com.smartnotepad.model.NotepadBean;
import gcp.com.smartnotepad.model.NotepadWithDataBean;

/**
 * Created by Administrator on 2016/10/11.
 */

public class TimeReceiver extends BroadcastReceiver {
    private List<NotepadWithDataBean> notepadWithDataBeanList;
    private final String PARENT_POSITION_KEY="parentPosition";
    private final String ITEM_POSITION_KEY="item_position_key";
    private final int START_REMIND=1;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext=context;
        Log.d("timechange","广播接收");
        if(Intent.ACTION_TIME_TICK.equals(intent.getAction())){
            Log.d("timechange","timeChange");
            getData();
            for(int i=0;i<notepadWithDataBeanList.size();i++) {
                NotepadWithDataBean notepadWithDataBean = notepadWithDataBeanList.get(i);
                if (notepadWithDataBean.getData() == getDate()) {
                    List<NotepadBean> notepadBeenList = notepadWithDataBeanList.get(i).getNotepadBeenList();
                    for (int j = 0; j < notepadBeenList.size(); j++) {
                        if(getTime().equals(notepadBeenList.get(j).getTime())){
                            Message message=new Message();
                            message.what=START_REMIND;
                            Bundle bundle=new Bundle();
                            bundle.putInt(PARENT_POSITION_KEY,i);
                            bundle.putInt(ITEM_POSITION_KEY,j);
                            message.setData(bundle);
                            handler.sendEmptyMessage(START_REMIND);
                        }
                    }
                }else{
                    continue;
                }
            }
        }
    }
    Handler handler=new Handler(){
        public void handleMessage(Message message){
            switch (message.what){
                case START_REMIND:
                    Bundle bundle=message.getData();
                    int parentPosition=bundle.getInt(PARENT_POSITION_KEY);
                    int itemPosition=bundle.getInt(ITEM_POSITION_KEY);
                    gotoRemindActivity(parentPosition,itemPosition);
                    break;
            }
        }
    };
    private void gotoRemindActivity(int parentPosition,int itemPosition){
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putString(GlobalParams.CONTENT_KEY,notepadWithDataBeanList.get(parentPosition).getNotepadBeenList().get(itemPosition).getContent());
        intent.putExtras(bundle);
        intent.setAction(GlobalParams.START_REMIND_ACTIVITY_ACTION);
       mContext.sendBroadcast(intent);
    }

    private void getData(){
        DataHelper helper = new DataHelper(mContext);
        notepadWithDataBeanList = new ArrayList<NotepadWithDataBean>();
        List<NotepadBean> notepadBeanList = helper.getNotepadList();
        for (int i = 0; i < notepadBeanList.size(); i++) {
            if (0 == notepadWithDataBeanList.size()) {
                NotepadWithDataBean notepadWithDataBean = new NotepadWithDataBean();
                notepadWithDataBean.setData(notepadBeanList.get(0).getDate());
                notepadWithDataBeanList.add(notepadWithDataBean);
            }
            boolean flag = true;
            for (int j = 0; j < notepadWithDataBeanList.size(); j++) {
                int date = notepadWithDataBeanList.get(j).getData();
                if (date == notepadBeanList.get(i).getDate()) {
                    notepadWithDataBeanList.get(j).getNotepadBeenList().add(notepadBeanList.get(i));
                    flag = false;
                    break;
                }
            }
            if (flag) {
                NotepadWithDataBean notepadWithDataBean = new NotepadWithDataBean();
                notepadWithDataBean.setData(notepadBeanList.get(i).getDate());
                notepadWithDataBeanList.add(notepadWithDataBean);
                notepadWithDataBeanList.get(notepadWithDataBeanList.size() - 1).getNotepadBeenList().add(notepadBeanList.get(i));
            }
        }
    }
    private String getTime(){
        Calendar calendar=Calendar.getInstance();
        int hourOfDay=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        String time = formatTime(hourOfDay,minute);
        return time;
    }
    private int getDate(){
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        int date=Integer.parseInt(getDate(year,month,day));
        return date;
    }
    private String  getDate(int year,int month,int day){
        String date="";
        date+=year;
        if(month<9){
            date=date+"0"+(month+1);
        }else{
            date+=(month+1);
        }
        if(day<10){
            date=date+"0"+day;
        }else {
            date+=day;
        }
        return date;
    }
    private String formatTime(int hour,int minute){
        String time=hour+":";
        if(minute<10){
            time=time+"0"+minute;
        }else{
            time+=minute;
        }
        return time;
    }
}
