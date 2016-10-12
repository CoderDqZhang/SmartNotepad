package gcp.com.smartnotepad.activity;

import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

import gcp.com.smartnotepad.R;
import gcp.com.smartnotepad.global.GlobalParams;

public class RemindActivity extends AppCompatActivity {

    private TextView tv_content;
    private Button bt_confirm;
    private MediaPlayer mMediaPlayer;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind);
        findViews();
        setListeners();
        Bundle bundle=getIntent().getExtras();
        String content=bundle.getString(GlobalParams.CONTENT_KEY);
        tv_content.setText(content);
        playSound();
    }

    private void findViews(){
        tv_content=(TextView)findViewById(R.id.tv_content);
        bt_confirm=(Button) findViewById(R.id.bt_confirm);
    }
    private void setListeners(){
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(null!=mMediaPlayer){
                  mMediaPlayer.stop();
                  finish();
              }
            }
        });
    }

    public void playSound() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMediaPlayer = MediaPlayer.create(RemindActivity.this, getSystemDefultRingtoneUri());
                mMediaPlayer.setLooping(true);//设置循环
                try {
                    mMediaPlayer.prepare();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMediaPlayer.start();
            }
        }).start();

    }
    //获取系统默认铃声的Uri
    private Uri getSystemDefultRingtoneUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(RemindActivity.this,
                RingtoneManager.TYPE_RINGTONE);
    }

}
