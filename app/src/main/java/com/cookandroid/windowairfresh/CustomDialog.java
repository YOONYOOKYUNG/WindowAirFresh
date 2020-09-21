package com.cookandroid.windowairfresh;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2017-08-07.
 */


public class CustomDialog extends DeviceListActivity {
    private Context context;
    private ListView listview;
    private WindowListAdapter adapter;

    public void setAdapter(WindowListAdapter _adapter) {
        adapter = _adapter;
    }

    public CustomDialog(Context context) {
        this.context = context;
    }

    //dhkim start ===================================

    public void callFunction(final TextView main_label) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.custom_dialog);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final EditText message = (EditText) dlg.findViewById(R.id.messagse);
        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //dhkim start =========================
                String inputText =  message.getText().toString();


            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "취소 했습니다.", Toast.LENGTH_SHORT).show();
                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
    }

    //dhkim end ===================================

    //기존코드 (dhkim)  주석처리 ============
    // 호출할 다이얼로그 함수를 정의한다.
    /*public void callFunction(final TextView main_label) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.custom_dialog);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final EditText message = (EditText) dlg.findViewById(R.id.messagse);
        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // '확인' 버튼 클릭시 메인 액티비티에서 설정한 main_label에
                // 커스텀 다이얼로그에서 입력한 메시지를 대입한다.
                //main_label.setText(message.getText().toString());
                Toast.makeText(context, "\"" + message.getText().toString() + "\" 을 입력하였습니다.", Toast.LENGTH_SHORT).show();
                //String temp = message.getText().toString();

                //dhkim start =================================

//                adapter.addItem(message.getText().toString(), true);
//                adapter.notifyDataSetChanged();

                //dhkim end ====================================



                // 커스텀 다이얼로그를 종료한다.

                Toast.makeText(context, "완료 했습니다.", Toast.LENGTH_SHORT).show();
                Message msg = aHandler.obtainMessage();
                msg.what = 0;
                aHandler.sendMessage(msg);

                dlg.dismiss();

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "취소 했습니다.", Toast.LENGTH_SHORT).show();



//                Message msg = aHandler.obtainMessage();
//                msg.what = 0;
//                aHandler.sendMessage(msg);



                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
    }

   Thread NewThread = new Thread() {
        @Override
        public void run() {

            while (true) {
                Message msg = aHandler.obtainMessage();
                msg.what = 0;
                aHandler.sendMessage(msg);

            }
        }
    };
    */
    //기존코드 (dhkim)  주석처리 ============


}


