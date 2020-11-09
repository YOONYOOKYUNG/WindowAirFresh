package com.cookandroid.windowairfresh;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

public class WindowlistActivity extends AppCompatActivity  {


    private DatabaseManager databaseManager;
    private ProgressDialog mProgressDlg; //로딩중 화면
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<>(); //블루투스 주소를 여기에 저장
    private BluetoothAdapter mBluetoothAdapter; // 블루투스 어댑터
    ImageButton btn1;
    ImageView backarrow;
    GridView gridView;
    WindowListAdapter adapter;
    TextView main_label;


    @SuppressLint("HandlerLeak")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseManager = DatabaseManager.getInstance(this);
        adapter = new WindowListAdapter();
        adapter.setDatabaseManager(databaseManager);
        setContentView(R.layout.activity_windowlist);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        //GPS permission 허용
        btn1 = findViewById(R.id.btn1);
        // 커스텀 다이얼로그에서 입력한 메시지를 출력할 TextView 를 준비한다.
        main_label = (TextView) findViewById(R.id.main_label);
        //final Switch switch1 = findViewById(R.id.switch1);
        gridView = findViewById(R.id.listview1);

        adapter.setListener(new WindowListAdapter.OnWindowButtonClickListener() {
            @Override
            public void onWindowButtonClick(int pos) {
                WindowDetails listViewItem = adapter.listViewItemList.get(pos);
                Boolean state=listViewItem.getState();
                SharedPreferences sf = getSharedPreferences("autoset", 0);
                Boolean mode = sf.getBoolean("modestate", false); // 키값으로
                if(mode) {
                    Intent intent = new Intent(WindowlistActivity.this,Popup_warning.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                }
                else if(state) {
                    listViewItem.setState(false);
                    ((MainActivity)MainActivity.mContext).closewindow(pos);
                    if (databaseManager != null) {
                        ContentValues updateRowValue = new ContentValues();
                        updateRowValue.put("state", "false");
                        databaseManager.update(updateRowValue,listViewItem.getName());

                        //timeline에 추가
                        Calendar cal =Calendar.getInstance();
                        SimpleDateFormat sdf_date = new SimpleDateFormat("MM/dd E");
                        SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");

                        //날짜, 시간
                        String date = sdf_date.format(cal.getTime());
                        String time = sdf_time.format(cal.getTime());

                        //내용
                        String content = "사용자가 " + listViewItem.getName() + "을/를 닫았습니다.";

                        databaseManager.timeline_insert(date, time, content, "닫힘");
                    }
                }

                else {
                    listViewItem.setState(true);
                    ((MainActivity)MainActivity.mContext).openwindow(pos);
                    if (databaseManager != null) {
                        ContentValues updateRowValue = new ContentValues();
                        updateRowValue.put("state", "true");
                        databaseManager.update(updateRowValue,listViewItem.getName());

                        //timeline에 추가
                        Calendar cal =Calendar.getInstance();
                        SimpleDateFormat sdf_date = new SimpleDateFormat("MM/dd E");
                        SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");

                        //날짜, 시간
                        String date = sdf_date.format(cal.getTime());
                        String time = sdf_time.format(cal.getTime());

                        //내용
                        String content = "사용자가 " + listViewItem.getName() + "을/를 열었습니다.";

                        databaseManager.timeline_insert(date, time, content, "열림");
                    }
                }
            }
        });


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //블루투스 통신을 위해 블루투스 어댑터를 가져옵니다
        mProgressDlg = new ProgressDialog(this);
        mProgressDlg.setMessage("검색중...");
        mProgressDlg.setCancelable(false);
        //뒤로가기 버튼을 눌러도 false임으로 뒤로가지지 않습니다.
        mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                mBluetoothAdapter.cancelDiscovery();
                //Cancel 버튼을 누르면 블루투스 찾기가 종료됩니다.
            }
        });




        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothAdapter.startDiscovery();

                IntentFilter filter = new IntentFilter();
                //IntentFilter란 다음 작업이 명시되지 않은 상태에서 보내진 intent에 대해
                //어느 activity/sevice/broadcast가 받을 것인가를 찾는 Intent Resolution시
                //참조하는 정보이다

                filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                //디바이스 연결상태 변경
                filter.addAction(BluetoothDevice.ACTION_FOUND);
                //블루투스 디바이스가 검색되었을 때(디바이스 검색 결과)
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                //블루투스 디바이스 검색 시작
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                //블루투스 디바이스 검색이 끝났을 때
                registerReceiver(mReceiver, filter);
                //receiver를 등록한다
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
            }
        });

//마마


      for(int i=0;i<adapter.getCount();i++){
          WindowDetails item =(WindowDetails) adapter.getItem(i);
      }


      // 뒤로가기 버튼
        backarrow = findViewById(R.id.backarrow);
        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
            }
        });

    }

    @Override
    public void onResume() {
        adapter.initialiseList();
        gridView.setAdapter(adapter);
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mBluetoothAdapter != null) {
            //장치가 블루투스를 지원하는 경우
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            //GPS permission 허용
            if (mBluetoothAdapter.isDiscovering()) {
                //블루투스 기기를 발견했다면
                mBluetoothAdapter.cancelDiscovery();
                //블루투스 검색 취소
            }
        }
        super.onPause();
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //각 action에 따른 반응
            if  (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //블루투스 디바이스 검색 시작
                mDeviceList = new ArrayList<>();
                //블루투스 기기 목록 갱신
                mProgressDlg.show();
                //로딩중화면 표시
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //블루투스 디바이스 검색이 끝났을 때
                mProgressDlg.dismiss();
                //로딩중 화면 사라짐
                Intent newIntent = new Intent(WindowlistActivity.this, DeviceListActivity.class);
                newIntent.putParcelableArrayListExtra("device.list", mDeviceList);
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                ArrayList<BluetoothDevice> list = new ArrayList<BluetoothDevice>();
                list.addAll(pairedDevices);
                //연결된 디바이스 목록을 리스트에 모두 추가합니다.
                newIntent.putParcelableArrayListExtra("device.list2", list);
                //추가된 값 저장하기
                startActivity(newIntent);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                return;
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //블루투스 디바이스가 검색되었을 때(디바이스 검색 결과)
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //추가된 값 받아오기
                mDeviceList.add(device);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
            }
        }
    };

    @Override
    public void onBackPressed(){
        WindowlistActivity.this.finish();
        super.onBackPressed();
    }
}
