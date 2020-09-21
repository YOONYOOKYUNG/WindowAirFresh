package com.cookandroid.windowairfresh;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.internal.ContextUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Device list.
 *
 * @author Lorensius W. L. T <lorenz@londatiga.net>
 *
 */


public class DeviceListActivity extends AppCompatActivity {

	//dhkim start ============================================
	final int NEW_WINDOW_REQUEST=1234;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if(resultCode==RESULT_OK && requestCode==NEW_WINDOW_REQUEST){
			Log.d("dhkim", "전달받은 새로운 창문 이름 : " + data.getStringExtra("new_window_name"));
			setResult(RESULT_OK, data);
			//setResult(RESULT_CANCELED);
			DeviceListActivity.this.finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	//dhkim end  ============================================

	String[] WindowList = new String[7];
	private ListView mListView, mListView2;
	private DeviceListAdapter mAdapter,mAdapter2;
	private ArrayList<BluetoothDevice> mDeviceList,mDeviceList2;
	Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_paired_devices);
		//어느 페이지의 레이아웃인가요?

		//dhkim start =====================
		int a=1;
		if(a==1) {

			new Thread(new Runnable() {
				@Override
				public void run() {
					try { Thread.sleep(1000); } catch (InterruptedException e) {e.printStackTrace();} //1초뒤 다이얼로그 띄우기
					Intent intent = new Intent (DeviceListActivity.this, NewCustomDialog.class);
					startActivityForResult(intent, NEW_WINDOW_REQUEST);
				}
			}).start();

		}
		else {
			//dhkim end =====================

			mDeviceList = getIntent().getExtras().getParcelableArrayList("device.list");
			mDeviceList2 = getIntent().getExtras().getParcelableArrayList("device.list2");
			mListView = (ListView) findViewById(R.id.lv_paired);
			mListView2 = (ListView) findViewById(R.id.lv_paired2);
			mAdapter = new DeviceListAdapter(this);
			mAdapter2 = new DeviceListAdapter(this);

			mAdapter.setData(mDeviceList);
			mAdapter2.setData(mDeviceList2);
			mAdapter.setListener(new DeviceListAdapter.OnPairButtonClickListener() {

				@Override
				public void onPairButtonClick(int position) {
					//버튼을 클릭하면
					BluetoothDevice device = mDeviceList.get(position);
					if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
						//BOND_BONDED : 페어링 됐으면
						unpairDevice(device);
						//페어링 해제
					} else {
						showToast("연결중...");
						pairDevice(device); //페어링
						//	Address(device);
					}
				}
			});

			mListView.setAdapter(mAdapter);

			mAdapter2.setListener(new DeviceListAdapter.OnPairButtonClickListener() {
				@Override
				public void onPairButtonClick(int position) {
					//버튼을 클릭하면
					BluetoothDevice device = mDeviceList2.get(position);
					if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
						//BOND_BONDED : 페어링 됐으면
						unpairDevice(device);
						//페어링 해제
					} else {
						showToast("연결중...");
						pairDevice(device); //페어링
						//	Address(device);
					}
				}
			});

			mListView2.setAdapter(mAdapter2);

			registerReceiver(mPairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));

		}

	}

	@Override
	public void onDestroy() {
		//dhkim start ================================
		//나중에 블루투스 연결하면 주석 풀것
		//unregisterReceiver(mPairReceiver);
		//dhkim end ==================================
		super.onDestroy();
	}


	private void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}

	private void Address(BluetoothDevice device) {
		//주소 저장
		for(int i=0;i<=9;i++){
			if(WindowList[i]==""){
				WindowList[i]=device.getAddress();
				break;
			}
		}
	}

	private void pairDevice(BluetoothDevice device) {
		//선택한 디바이스 페어링 요청
		try {
			Method method = device.getClass().getMethod("createBond", (Class[]) null);
			method.invoke(device, (Object[]) null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void unpairDevice(BluetoothDevice device) {
		//선택한 디바이스 언페어링 요청
		try {
			Method method = device.getClass().getMethod("removeBond", (Class[]) null);
			method.invoke(device, (Object[]) null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				//페어링 상태가 변경되면 페어링상태 업데이트
				final int state 		= intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
				final int prevState	= intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

				if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
					showToast("연결됨");
					final WindowlistActivity windowlistActivity = new WindowlistActivity();
					CustomDialog customDialog = new CustomDialog(DeviceListActivity.this);
					customDialog.setAdapter(windowlistActivity.GetAdapter());
					customDialog.callFunction(windowlistActivity.GetMainlabel());

				} else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
					showToast("연결해제됨");
				}

				mAdapter.notifyDataSetChanged();
				//어댑터값 갱신
			}
		}
	};
}