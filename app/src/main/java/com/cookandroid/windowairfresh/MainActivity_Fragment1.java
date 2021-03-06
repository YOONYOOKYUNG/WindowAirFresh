package com.cookandroid.windowairfresh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity_Fragment1 extends Fragment {
    ViewPager2 viewpager;
    ImageView question2;
    TextView tvdate, temp1, humid1, micro1, location_address;
    RelativeLayout templayout, dustlayout, humidlayout, bg;
    int Start_index, End_index;
    String data, data2;
    SwipeRefreshLayout swipeRefreshLayout;
    AutoWindowListener callback;
    DatabaseManager databaseManager;

    public MainActivity_Fragment1() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        databaseManager = DatabaseManager.getInstance(getContext());

        //fragment 설정
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.activity_main_fragment1, container, false);
        viewpager = getActivity().findViewById(R.id.viewpager);

        //레이아웃 및 속성 정의
        swipeRefreshLayout = view.findViewById(R.id.swipe1);
        temp1 = view.findViewById(R.id.temp1);
        micro1 = view.findViewById(R.id.micro1);
        humid1 = view.findViewById(R.id.humid1);
        question2 = view.findViewById(R.id.question2);
        templayout = view.findViewById(R.id.templayout);
        dustlayout = view.findViewById(R.id.dustlayout);
        humidlayout = view.findViewById(R.id.humidlayout);
        bg = view.findViewById(R.id.bg);

        //현재 날짜 및 시간
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 aa hh시 mm분");
        cal.add(Calendar.DATE, 0);
        String today = sdf.format(cal.getTime());
        tvdate = view.findViewById(R.id.tvdate);
        tvdate.setText(today);

        //저장된 주소 받아옴
        location_address = view.findViewById(R.id.location_address);
        SharedPreferences pf2 = getContext().getSharedPreferences("address", getContext().MODE_PRIVATE);
        location_address.setText(pf2.getString("addr0", "서울시 성동구"));


        //새로고침 이벤트
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((MainActivity) MainActivity.mContext).onRefresh();
            }
        });

        //도움말 아이콘 이벤트
        question2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getContext(), HelpActivity2.class);
                startActivity(intent1);
                getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        //주소창 클릭 시 이벤트 (저장된 주소 변경)
        location_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddressActivity.class));
                getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });


        //온도 레이아웃 이벤트 - popup1_temp
        templayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity_Popup customDialogPopup1 = new MainActivity_Popup(view.getContext());
                customDialogPopup1.settemp(temp1.getText().toString());
                customDialogPopup1.calltemppopup();
            }
        });

        //미세먼지 레이아웃 이벤트 -  popup2_dust
        dustlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity_Popup customDialogPopup1 = new MainActivity_Popup(view.getContext());
                customDialogPopup1.setdust(micro1.getText().toString());
                customDialogPopup1.calldustpopup();
            }
        });

        //습도 레이아웃 이벤트 -  popup3_humid
        humidlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity_Popup customDialogPopup1 = new MainActivity_Popup(view.getContext());
                customDialogPopup1.settemp(temp1.getText().toString());
                customDialogPopup1.sethumid(humid1.getText().toString());
                customDialogPopup1.callhumidpopup();
            }
        });


        final Handler handler = new Handler();
        new Thread(new Runnable() {

            public void run() {

                //API(온,습도) 파싱
                data = getXmlData1();
                //API(미세먼지) 파싱
                data2 = getXmlData2();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 비 오는지 안오는지 파싱  (0:비안옴  / 1:비 감지)
                        SharedPreferences sf = getContext().getSharedPreferences("fragment2", 0);
                        String pty = sf.getString("rain", "0");

                        //습도 파싱
                        Start_index = data.indexOf("REH:");
                        End_index = data.indexOf("/", Start_index);
                        String reh = data.substring(Start_index + 4, End_index);

                        //온도 파싱
                        Start_index = data.indexOf("T1H:");
                        End_index = data.indexOf("/", Start_index);
                        String t1h = data.substring(Start_index + 4, End_index);

                        //보정값 (소수점 빼기)
                        int index = t1h.indexOf(".", 0);
                        if (index != -1)
                            t1h = t1h.substring(0, index);

                        //보정값 (홈페이지 파싱값이 문자일 경우 디폴드 값)
                        if (t1h.equals("-")) {
                            t1h = "18";
                        }
                        if (reh.equals("-")) {
                            reh = "15";
                        }
                        if (data2.equals("-")) {
                            data2 = "32";
                        }

                        //fragment1에 띄움
                        temp1.setText(t1h);
                        humid1.setText(reh);
                        micro1.setText(data2);

                        //자동모드로 데이터 전송
                        callback.onAutoWindowSet(t1h, data2, pty);

                        //도움말 띄우기 (show=true 띄움 / show=false 띄우지않음)
                        SharedPreferences pf1 = getContext().getSharedPreferences("help", getContext().MODE_PRIVATE);
                        if (pf1.getBoolean("show", true) == true) {
                            Intent intent = new Intent(getContext(), HelpActivity.class);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        }

                        //비가 감지되었을때 메인화면 그림 변경
                        if (Integer.parseInt(pty) != 0) {
                            bg.setBackgroundResource(R.drawable.fragment1_rain);

                        }

                    }
                });


            }
        }).start();

        return view;
    }


    public void setListener(AutoWindowListener callback) {
        this.callback = callback;
    }

    //메인에 데이터 보내기
    public interface AutoWindowListener {
        public void onAutoWindowSet(String temp, String dust, String rain);
    }


    //API(온,습도) 파싱
    String getXmlData1() {

        StringBuffer buffer1 = new StringBuffer();

        //온습도 api 서비스키
        String serviceKey1 = "pbjfdUXNOnav6q2Tb%2BrkkjcxUA4dZZVfL2joSHTUXE32G6h%2Fj8ZabTsIin%2Bn7DQ%2BwJt676jVMiEEui560v3UZA%3D%3D";

        //저장한 nx,ny 주소 받아옴
        SharedPreferences pf2 = getContext().getSharedPreferences("address", getContext().MODE_PRIVATE);
        String address1 = pf2.getString("addr1", "61"); //측정소 xy좌표 뽑아왔을때 null이면
        String address2 = pf2.getString("addr2", "127");  // 서울 성동구의 임의 값 넣어줌.

        //today 날짜 , time 시간
        Calendar cal1 = Calendar.getInstance();
        cal1.add(Calendar.DATE, 0);
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH");
        SimpleDateFormat sdf1_1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf1_2 = new SimpleDateFormat("mm");
        String time1_hours = sdf1.format(cal1.getTime());
        String time1_minute = sdf1_2.format(cal1.getTime());
        String today1 = sdf1_1.format(cal1.getTime());
        if (time1_hours.equals("00")) { //만약 시간이 오전 00시일 경우 ---> 날짜:전날, 시간:23:00로 바꿔줌.
            cal1.add(Calendar.DATE, -1);
            today1 = sdf1_1.format(cal1.getTime());
            time1_hours = "2300";
        } else {                        //시간이 1~23시일 경우
            if (Integer.parseInt(time1_minute) <= 40) { //40분 이하일 경우 --> 1시간 전 으로 바꿔줌
                cal1.add(Calendar.HOUR, -1);// api 자체가 매시간 40분에 갱신됨.
                time1_hours = sdf1.format(cal1.getTime());
                time1_hours = time1_hours.concat("00");
            }
            if (time1_hours.length() <= 2)
                time1_hours = time1_hours.concat("00");
        }

        //파싱할 url 저장
        String queryUrl1 = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getUltraSrtNcst?serviceKey=" + serviceKey1 +
                "&numOfRows=10&pageNo=1&base_date=" + today1 + "&base_time=" + time1_hours + "&nx=" + address1 + "&ny=" + address2;

        try {
            URL url1 = new URL(queryUrl1);//문자열로 된 요청 url을 URL 객체로 생성.
            InputStream is1 = url1.openStream(); //url위치로 입력스트림 연결

            //XmlPullParser 사용하여 xml 파싱
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is1, "UTF-8")); //inputstream 으로부터 xml 입력받기

            String tag;

            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    //시작태그
                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();//태그 이름 얻어옴

                        if (tag.equals("item")) ;// 첫번째 검색결과
                            //category(항목 이름)일때 값 얻어옴
                        else if (tag.equals("category")) {
                            xpp.next();
                            buffer1.append(xpp.getText());
                            buffer1.append(":");
                            //obsrValue(값 이름)일때 값 얻어옴
                        } else if (tag.equals("obsrValue")) {
                            xpp.next();
                            buffer1.append(xpp.getText());
                            buffer1.append("/");
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    //종료태그
                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();
                        if (tag.equals("item")) ; //현재 태그의 검색종료 -> 줄바꿈
                        break;
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer1.toString(); //StringBuffer 문자열 객체 반환
    }

    //API(미세먼지) 파싱
    String getXmlData2() {

        StringBuffer buffer2 = new StringBuffer();

        //미세먼지 api 서비스키
        String serviceKey2 = "pbjfdUXNOnav6q2Tb%2BrkkjcxUA4dZZVfL2joSHTUXE32G6h%2Fj8ZabTsIin%2Bn7DQ%2BwJt676jVMiEEui560v3UZA%3D%3D";

        //저장한 측정소 이름 받아옴
        SharedPreferences pf2 = getContext().getSharedPreferences("address", getContext().MODE_PRIVATE);
        String stationName = pf2.getString("station", "성동구"); //측정소이름을 뽑아왔을때 null 이면 임의로 성동구로 넣어줌

        //파싱할 url 저장
        String queryUrl2 = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?" +
                "stationName=" + stationName + "&dataTerm=DAILY&pageNo=1&numOfRows=1&ServiceKey=" + serviceKey2 + "&ver=1.3";

        try {
            URL url2 = new URL(queryUrl2);//문자열로 된 요청 url을 URL 객체로 생성.
            InputStream is2 = url2.openStream(); //url위치로 입력스트림 연결

            //XmlPullParser 사용하여 xml 파싱
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is2, "UTF-8")); //inputstream 으로부터 xml 입력받기

            String tag;

            xpp.next();
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;

                    //시작태그
                    case XmlPullParser.START_TAG:
                        tag = xpp.getName(); //태그 이름 얻어오기

                        if (tag.equals("item")) ;
                            //pm25Value(미세먼지 태그 이름)일때 값 얻어옴
                        else if (tag.equals("pm25Value")) {
                            xpp.next();
                            buffer2.append(xpp.getText());
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    //종료태그
                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();
                        if (tag.equals("item")) ; //현재 태그의 검색종료 -> 줄바꿈
                        break;
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer2.toString(); //StringBuffer 문자열 객체 반환
    }
}