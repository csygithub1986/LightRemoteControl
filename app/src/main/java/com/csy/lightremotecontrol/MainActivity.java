package com.csy.lightremotecontrol;

import android.hardware.ConsumerIrManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    View constraintLayout1;
    View constraintLayout2;

    Button btnBalcony;
    Button btnBedroom;
    Button btnSmallLivingRoom;
    Button btnLivingRoom;
    Button btnLeftLawn;
    Button btnRightLawn;
    Button btnBrighter;
    Button btnDarker;

    //获取红外控制类
    private ConsumerIrManager IR;
    //判断是否有红外功能
    boolean IRBack;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.house1:
                    constraintLayout1.setVisibility(View.VISIBLE);
                    constraintLayout2.setVisibility(View.GONE);
                    return true;
                case R.id.house2:
                    constraintLayout1.setVisibility(View.GONE);
                    constraintLayout2.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        constraintLayout1 = findViewById(R.id.constraintLayout1);
        constraintLayout2 = findViewById(R.id.constraintLayout2);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        btnBalcony = (Button) findViewById(R.id.btnBalcony);
        btnBedroom = (Button) findViewById(R.id.btnBedroom);
        btnSmallLivingRoom = (Button) findViewById(R.id.btnSmallLivingRoom);
        btnLivingRoom = (Button) findViewById(R.id.btnLivingRoom);
        btnLeftLawn = (Button) findViewById(R.id.btnLeftLawn);
        btnRightLawn = (Button) findViewById(R.id.btnRightLawn);
        btnBrighter = (Button) findViewById(R.id.btnBrighter);
        btnDarker = (Button) findViewById(R.id.btnDarker);


        btnBalcony.setOnClickListener(clickListener);
        btnBedroom.setOnClickListener(clickListener);
        btnSmallLivingRoom.setOnClickListener(clickListener);
        btnLivingRoom.setOnClickListener(clickListener);
        btnLeftLawn.setOnClickListener(clickListener);
        btnRightLawn.setOnClickListener(clickListener);
        btnBrighter.setOnClickListener(clickListener);
        btnDarker.setOnClickListener(clickListener);

        inItEvent();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (IRBack) {
                switch (view.getId()) {
                    case R.id.btnBalcony:
                        sendMsg(38000, CodeCommand.CodeBalcony);
                        break;
                    case R.id.btnBedroom:
                        sendMsg(38000, CodeCommand.codeBedroom);
                        break;
                    case R.id.btnSmallLivingRoom:
                        sendMsg(38000, CodeCommand.CodeSmallLivingRoom);
                        break;
                    case R.id.btnLivingRoom:
                        sendMsg(38000, CodeCommand.CodeLivingRoom);
                        break;
                    case R.id.btnLeftLawn:
                        sendMsg(38000, CodeCommand.CodeLeftLawn);
                        break;
                    case R.id.btnRightLawn:
                        sendMsg(38000, CodeCommand.CodeRightLawn);
                        break;
                    case R.id.btnBrighter:
                        sendMsg(38000, CodeCommand.CodeBrighter);
                        break;
                    case R.id.btnDarker:
                        sendMsg(38000, CodeCommand.CodeDarker);
                        break;
                }
            } else {
                Toast.makeText(MainActivity.this, "对不起，该设备上没有红外功能!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    //初始化事务
    private void inItEvent() {
        //获取ConsumerIrManager实例
        IR = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);

        //如果sdk版本大于4.4才进行是否有红外的功能（手机的android版本）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            IRBack = IR.hasIrEmitter();
            if (!IRBack) {
                Toast.makeText(this, "对不起，该设备上没有红外功能!", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(this, "红外设备就绪", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 发射红外信号
     * 可以查看这个标签的log   ConsumerIr
     *
     * @param carrierFrequency 红外传输的频率，一般的遥控板都是38KHz
     * @param pattern          指以微秒为单位的红外开和关的交替时间
     */
    private void sendMsg(int carrierFrequency, int[] pattern) {
        IR.transmit(carrierFrequency, pattern);
//        Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT).show();
    }
}
