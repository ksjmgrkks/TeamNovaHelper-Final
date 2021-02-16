package com.example.teamnovahelper.MainActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.teamnovahelper.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class COVID19Activity extends AppCompatActivity {
    //COVID19Activity 는 팀노바에 출근하기 전에 코로나 현황을 미리 확인하는 액티비티입니다.
    TextView textView_today;
    TextView textView_definite_diagnosis;
    TextView textView_examination;
    TextView textView_clear;
    TextView textView_death;

    // 현재시간을 msec 으로 구한다.
    long now = System.currentTimeMillis();
    // 현재시간을 date 변수에 저장한다.
    Date date = new Date(now-(86400*1000));
    // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMdd");
    // nowDate 변수에 값을 저장한다.
    String formatDate = sdfNow.format(date);

    String url = "http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19InfStateJson?serviceKey=46qK4LQNiBjcXHuSxfq55PdckIOk5YC5ZvNtMju7iAYV1edsAr1s5Kmh3KmmcJb96GdH%2BilIfy6DNwrWog8iAw%3D%3D&pageNo=1&numOfRows=10&startCreateDt="+formatDate+"&endCreateDt="+formatDate;
    DATA task;

    HashMap<String, String> Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid19);
        setTitle("코로나19 국내 현황");

        //저장한 배경색을 나타내는 부분입니다.
        ConstraintLayout constraint_layout = (ConstraintLayout) findViewById(R.id.COVID_layout);
        SharedPreferences layout_sharedPreferences =  getSharedPreferences("saveColor", MODE_PRIVATE);
        int background_color = layout_sharedPreferences.getInt("Background color", 0);
        constraint_layout.setBackgroundColor(background_color);

        Init();
    }

    void Init(){
        Data = new HashMap<String, String>();
        textView_today = (TextView) findViewById(R.id.textView_today);
        textView_definite_diagnosis = (TextView) findViewById(R.id.textView_definite_diagnosis);
        textView_examination = (TextView) findViewById(R.id.textView_examination);
        textView_clear = (TextView) findViewById(R.id.textView_clear);
        textView_death = (TextView) findViewById(R.id.textView_death);


        task = new DATA();
        task.execute(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    public void Pasing(String strXML) {
        XmlPullParser xmlPullParser;
        XmlPullParserFactory xmlPullParserFactory;
        String pasing_decide = "";
        String pasing_today = "";
        String pasing_examination = "";
        String pasing_clear = "";
        String pasing_death = "";

        try {
            xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParserFactory.setNamespaceAware(true);

            xmlPullParser = xmlPullParserFactory.newPullParser();
            xmlPullParser.setInput(new StringReader(strXML));

            String key = "";
            String value = "";

            while(xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT){
                if(xmlPullParser.getEventType() == XmlPullParser.START_TAG){
                    key = xmlPullParser.getName();
                }
                if(xmlPullParser.getEventType() == XmlPullParser.TEXT)
                {
                    value = xmlPullParser.getText();
                    Data.put(key, value);
                }
                xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        pasing_decide = Data.get("decideCnt");
        pasing_today = Data.get("createDt");
        pasing_examination = Data.get("examCnt");
        pasing_clear = Data.get("clearCnt");
        pasing_death = Data.get("deathCnt");

        String decideFirst = pasing_decide.substring(0,2); //문자열  start위치 부터 end전까지 문자열 발췌
        String decideSecond = pasing_decide.substring(2); //문자열  start위치부터 끝까지 문자열 자르기
        String examFirst = pasing_examination.substring(0,2);
        String examSecond = pasing_examination.substring(2);
        String clearFirst = pasing_clear.substring(0,2);
        String clearSecond =pasing_clear.substring(2);
        String deathFirst = pasing_death.substring(0,1);
        String deathSecond = pasing_death.substring(1);
        String today = pasing_today.substring(0,10);

        textView_today.setText(today);
        textView_definite_diagnosis.setText(decideFirst+","+decideSecond+"명");
        textView_examination.setText(examFirst+","+examSecond+"명");
        textView_clear.setText(clearFirst+","+clearSecond+"명");
        textView_death.setText(deathFirst+","+deathSecond+"명");
    }

    private class DATA extends AsyncTask<String, Integer, String> {

        private ProgressDialog progressDialog;

        public DATA() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(COVID19Activity.this);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String s = "";

            try {
                URL url = new URL(params[0]);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                if(httpURLConnection != null){

                    httpURLConnection.setDefaultUseCaches(false);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestMethod("GET");

                    httpURLConnection.setRequestProperty("Accept", "application/xml");

                    //수신
                    InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    String r;
                    while((r = bufferedReader.readLine()) != null){
                        s += r;
                    }

                    bufferedReader.close();
                }
                else{
                    s += "not connected";
                }

                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return s;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();

            Pasing(s);
        }
    }
}