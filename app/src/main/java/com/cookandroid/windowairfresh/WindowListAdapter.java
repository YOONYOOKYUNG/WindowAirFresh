package com.cookandroid.windowairfresh;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class WindowListAdapter extends BaseAdapter {
    public  ArrayList<WindowListAdapter> listViewItemList = new ArrayList<>() ;
    public WindowListAdapter() { }
    private OnWindowButtonClickListener wListener;
    public void setListener(OnWindowButtonClickListener listener) { wListener = listener; }
    String name, address;
    Boolean state;

    DatabaseManager databaseManager;

    //set
    public void setName(String name){
        this.name = name;
    }
    public void setAddress(String address){this.address=address;}
    public void setState(Boolean state){this.state=state;}

    public void setDatabaseManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    //get
    public String getName() {
        return name;
    }
    public String getAddress(){return address;}
    public Boolean getState(){return state;}

    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }
    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        // "activity_windowlistitem" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_windowlistitem, parent, false);
        }
        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView titleTextView = (TextView) convertView.findViewById(R.id.main_label) ;
        final TextView addressTextView = (TextView) convertView.findViewById(R.id.address);
        final ImageButton windowstate = (ImageButton) convertView.findViewById(R.id.windowstate);
        final ImageButton windowdelete = (ImageButton) convertView.findViewById(R.id.windowdelete);
        final FrameLayout windowbtnback = (FrameLayout) convertView.findViewById(R.id.windowbtnback);

        //리스트뷰 기본 배경색 지정
        //convertView.setBackgroundColor(Color.parseColor("#B7DBF4"));
        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final WindowListAdapter listViewItem = listViewItemList.get(position);
        // 아이템 내 각 위젯에 데이터 반영
        titleTextView.setText(listViewItem.getName());
        addressTextView.setText(listViewItem.getAddress());
        state=listViewItem.getState();



        windowstate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wListener != null) {
                    wListener.onWindowButtonClick(pos);
                    Log.d("상태", "현재 창문 상태 : " + listViewItem.getState());
                if (state==true){
                    windowstate.setImageResource(R.drawable.windowlist_windowopen);
                    windowbtnback.setBackgroundColor(Color.parseColor("#B7DBF4"));
                    windowstate.setBackgroundColor(Color.parseColor("#B7DBF4"));
                    windowdelete.setBackgroundColor(Color.parseColor("#B7DBF4"));
                    notifyDataSetChanged();
                }else if (state==false){
                    windowstate.setImageResource(R.drawable.windowlist_windowclose);
                    windowbtnback.setBackgroundColor(Color.parseColor("#B9BDBF"));
                    windowstate.setBackgroundColor(Color.parseColor("#B9BDBF"));
                    windowdelete.setBackgroundColor(Color.parseColor("#B9BDBF"));
                    notifyDataSetChanged();
                  }
                }
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("창문삭제")        // 제목 설정
                .setMessage("창문을 삭제하시겠습니까?")        // 메세지 설정
                .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                    // 확인 버튼 클릭시 설정, 오른쪽 버튼
                    public void onClick(DialogInterface dialog, int whichButton){
                        removeitem(position);
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                    // 취소 버튼 클릭시 설정, 왼쪽
                    public void onClick(DialogInterface dialog, int whichButton){
                        dialog.dismiss();
                    }
                });

        windowdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder.show();
            }
        });
        return convertView;
    }


    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String name, String address, Boolean state) {
        WindowListAdapter item = new WindowListAdapter();
        item.setName(name);
        item.setAddress(address);
        item.setState(state);
        listViewItemList.add(item);

        if (databaseManager != null) {
            ContentValues addRowValue = new ContentValues();

            addRowValue.put("name", name);
            addRowValue.put("address", address);
            addRowValue.put("state", state.toString());

            databaseManager.insert(addRowValue);
        }
    }



    public void removeitem(int position) {
        if (databaseManager != null) {
           databaseManager.delete(listViewItemList.get(position).getName());
        }
        listViewItemList.remove(position);
    }

    public void initialiseList()
    {
        if (databaseManager != null){
            listViewItemList = databaseManager.getAll();
        }
    }


    public ArrayList<WindowListAdapter> getListViewItemList(){return listViewItemList;}


    public interface OnWindowButtonClickListener{
        void onWindowButtonClick(int pos);
    }
}

