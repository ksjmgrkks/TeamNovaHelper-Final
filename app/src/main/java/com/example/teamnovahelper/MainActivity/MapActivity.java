package com.example.teamnovahelper.MainActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.teamnovahelper.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;


public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback,
        PlacesListener, GoogleMap.OnMarkerClickListener{

    private long back_button_time = 0;

    private GoogleMap mMap;
    List<Marker> previous_marker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초


    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소


    Location mCurrentLocatiion;
    LatLng currentPosition;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;


    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)

    LatLng teamNovaOffice1 = new LatLng(37.484127757637786, 126.97264762656714);
    LatLng teamNovaOffice2 = new LatLng(37.483878266696436, 126.97222235644934);
    LatLng teamNovaOffice3 = new LatLng(37.48424535608887, 126.9738164746693);
    LatLng teamNovaOffice45 = new LatLng(37.48292269122235, 126.97394338640267);
    LatLng teamNovaOffice6 = new LatLng(37.48285578202186, 126.97509429985752);
    LatLng teamNovaOffice78 = new LatLng(37.48564149415553, 126.97209158332478);
    LatLng teamNovaOffice9 = new LatLng(37.485619764469895, 126.97209928758906);

    int siteSelect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mLayout = findViewById(R.id.layout_main);
        setTitle("팀노바 사무실 지도");

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        previous_marker = new ArrayList<Marker>();
        ImageView ImageViewConvenienceStore = (ImageView) findViewById(R.id.imageView_cv);
        ImageViewConvenienceStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siteSelect = 0;
                showPlaceInformation();
            }
        });
        ImageView ImageViewRestaurant = (ImageView) findViewById(R.id.imageView_rest);
        ImageViewRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siteSelect = 1;
                showPlaceInformation();
            }
        });
        ImageView ImageViewPharmacy = (ImageView) findViewById(R.id.imageView_ph);
        ImageViewPharmacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siteSelect = 2;
                showPlaceInformation();
            }
        });


        //TabLayout 을 하단에 배치하여 유저가 어플의 주요 기능을 알아보기 쉽고,
        // 이용이 편리하도록 하였습니다.
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab) ;
        tabLayout.selectTab(tabLayout.getTabAt(3));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition() ;
                changeView(position) ;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        }) ;

    }

    private void changeView(int index) {
        String Login_User_ID = getIntent().getStringExtra("Login_User_ID");
        switch (index) {

            case 0 :
                Intent record_intent = new Intent(MapActivity.this, RecordActivity.class);
                record_intent.putExtra("Login_User_ID", Login_User_ID);
                MapActivity.this.startActivity(record_intent);
                finish();
                break ;
            case 1 :
                Intent stop_watch_intent = new Intent(MapActivity.this, StopWatchActivity.class);
                stop_watch_intent.putExtra("Login_User_ID", Login_User_ID);
                MapActivity.this.startActivity(stop_watch_intent);
                finish();
                break ;
            case 2 :
                Intent information_intent = new Intent(MapActivity.this, InformationActivity.class);
                information_intent.putExtra("Login_User_ID", Login_User_ID);
                MapActivity.this.startActivity(information_intent);
                finish();
                break ;
            case 3 :

        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            startLocationUpdates(); // 3. 위치 업데이트 시작
        }else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions( MapActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d( TAG, "onMapClick :");
            }
        });

        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(teamNovaOffice1);
        markerOptions1.title("팀노바 1사무실\n" +
                " (하브루타실 & 강의실)");
        markerOptions1.snippet(" - 수업중 사용 불가\n" +
                " - 주소 : 서울시 동작구 사당동 252-15 5층,\n" +
                "서울시 동작구 사당로 219 5층");
        markerOptions1.icon(BitmapDescriptorFactory.fromResource(R.drawable.team_nova_icon));

        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(teamNovaOffice2);
        markerOptions2.title("팀노바 2사무실\n" +
                " (창업팀 공간)");
        markerOptions2.snippet(" - 창업팀 공간으로 사용 가능\n" +
                " - 주소 : 서울시 동작구 사당동 265-10 4층,\n" +
                "서울시 동작구 사당로 220 4층");
        markerOptions2.icon(BitmapDescriptorFactory.fromResource(R.drawable.team_nova_icon));

        MarkerOptions markerOptions3 = new MarkerOptions();
        markerOptions3.position(teamNovaOffice3);
        markerOptions3.title("팀노바 3사무실\n" +
                " (파트장 사무실 & 강의실)");
        markerOptions3.snippet(" - 파트장&팀장 공간\n" +
                " - 집중관리가 필요한 팀원만 사용가능(파트장&팀장 재량)\n" +
                " - 주소 : 서울시 동작구 사당동 708-429 2층,\n" +
                "서울시 동작구 사당로17길 21 2층");
        markerOptions3.icon(BitmapDescriptorFactory.fromResource(R.drawable.team_nova_icon));

        MarkerOptions markerOptions45 = new MarkerOptions();
        markerOptions45.position(teamNovaOffice45);
        markerOptions45.title("팀노바 4,5사무실\n" +
                " (자습실 & 강의실)");
        markerOptions45.snippet(" - 강의실은 수업중 사용 불가\n" +
                " - 하브루타 가능 \n" +
                " - 주소 : 서울시 동작구 사당동 318-13 3층 302,303호");
        markerOptions45.icon(BitmapDescriptorFactory.fromResource(R.drawable.team_nova_icon));

        MarkerOptions markerOptions6 = new MarkerOptions();
        markerOptions6.position(teamNovaOffice6);
        markerOptions6.title("팀노바 6사무실 (자습실)");
        markerOptions6.snippet(" - 언제나 사용 가능\n" +
                " - 하브루타 불가(도서관급 조용한 사무실)\n" +
                " - 주소 : 서울시 동작구 사당동 318-8 2층");
        markerOptions6.icon(BitmapDescriptorFactory.fromResource(R.drawable.team_nova_icon));

        MarkerOptions markerOptions78 = new MarkerOptions();
        markerOptions78.position(teamNovaOffice78);
        markerOptions78.title("팀노바 7,8사무실\n" +
                " (강의실&자습실)");
        markerOptions78.snippet("- 1강의실, 2강의실은 수업중 사용 불가\n" +
                "- 자습 불가\n" +
                "- 하브루타 가능\n" +
                "- 주소 : 서울시 동작구 사당로 13길 36 3층 ");
        markerOptions78.icon(BitmapDescriptorFactory.fromResource(R.drawable.team_nova_icon));

        MarkerOptions markerOptions9 = new MarkerOptions();
        markerOptions9.position(teamNovaOffice9);
        markerOptions9.title("팀노바 9사무실 (연구실)");
        markerOptions9.snippet("- 연구하는 팀원, 응용 2단계 팀프로젝트하는 팀원만 사용가능\n" +
                "- 주소 : 서울시 동작구 사당로 13길 36 5층");
        markerOptions9.icon(BitmapDescriptorFactory.fromResource(R.drawable.team_nova_icon));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(teamNovaOffice3, 17));
        mMap.addMarker(markerOptions1);
        mMap.addMarker(markerOptions2);
        mMap.addMarker(markerOptions3);
        mMap.addMarker(markerOptions45);
        mMap.addMarker(markerOptions6);
        mMap.addMarker(markerOptions78);
        mMap.addMarker(markerOptions9);

        mMap.setOnMarkerClickListener(this);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        // 레이아웃 파일 marker_view.xml 을 불러와서 화면에 다이얼로그를 보여줍니다.
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        View view = LayoutInflater.from(MapActivity.this)
                .inflate(R.layout.marker_view, null, false);
        builder.setView(view);
        final ImageView imageViewMarker = (ImageView) view.findViewById(R.id.imageView_marker);
        final TextView textViewMarkerName = (TextView) view.findViewById(R.id.textView_markerName);
        final TextView textViewMarkerAddress = (TextView) view.findViewById(R.id.textView_address);
        final AlertDialog dialog = builder.create();
        Log.d("좌표값", String.valueOf(marker.getPosition()));
        Log.d("좌표값", String.valueOf(teamNovaOffice1));
        if(String.valueOf(marker.getPosition()).equals(String.valueOf(teamNovaOffice1))){
            imageViewMarker.setImageResource(R.drawable.one);
        }
        if(String.valueOf(marker.getPosition()).equals(String.valueOf(teamNovaOffice2))){
            imageViewMarker.setImageResource(R.drawable.two);
        }
        if(String.valueOf(marker.getPosition()).equals(String.valueOf(teamNovaOffice3))){
            imageViewMarker.setImageResource(R.drawable.background);
        }
        if(String.valueOf(marker.getPosition()).equals(String.valueOf(teamNovaOffice45))){
            imageViewMarker.setImageResource(R.drawable.four);
        }
        if(String.valueOf(marker.getPosition()).equals(String.valueOf(teamNovaOffice6))){
            imageViewMarker.setImageResource(R.drawable.six);
        }
        if(String.valueOf(marker.getPosition()).equals(String.valueOf(teamNovaOffice78))){
            imageViewMarker.setImageResource(R.drawable.seven);
        }
        if(String.valueOf(marker.getPosition()).equals(String.valueOf(teamNovaOffice9))){
            imageViewMarker.setImageResource(R.drawable.seven);
        }
        textViewMarkerName.setText(marker.getTitle());
        textViewMarkerAddress.setText(marker.getSnippet());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17));

        dialog.show();
        return true;
        //return false; //기본 타이틀,스니펫 표시하기
    }
    @Override
    public void onBackPressed() {
        //뒤로가기를 한번 눌렀을 때 어플이 종료된다면,
        //유저가 뒤로가기를 잘못 눌렀을 때 불편할 수 있기 때문에 이 기능을 추가했습니다.
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - back_button_time;

        if(0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        }
        else {
            back_button_time = curTime;
            Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르면 앱이 종료됩니다.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(siteSelect == 0){
                    for (noman.googleplaces.Place place : places) {
                        LatLng latLng
                                = new LatLng(place.getLatitude()
                                , place.getLongitude());

                        String markerSnippet = getCurrentAddress(latLng);

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(place.getName());
                        markerOptions.snippet(markerSnippet);
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.cvicon));
                        Marker item = mMap.addMarker(markerOptions);
                        previous_marker.add(item);

                    }
                }
                if(siteSelect == 1){
                    for (noman.googleplaces.Place place : places) {
                        LatLng latLng
                                = new LatLng(place.getLatitude()
                                , place.getLongitude());

                        String markerSnippet = getCurrentAddress(latLng);

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(place.getName());
                        markerOptions.snippet(markerSnippet);
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.resicon));
                        Marker item = mMap.addMarker(markerOptions);
                        previous_marker.add(item);
                    }
                }
                if(siteSelect == 2){
                    for (noman.googleplaces.Place place : places) {
                        LatLng latLng
                                = new LatLng(place.getLatitude()
                                , place.getLongitude());

                        String markerSnippet = getCurrentAddress(latLng);

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(place.getName());
                        markerOptions.snippet(markerSnippet);
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.phicon));
                        Marker item = mMap.addMarker(markerOptions);
                        previous_marker.add(item);
                    }
                }
                //중복 마커 제거
                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);
            }
        });
    }

    @Override
    public void onPlacesFinished() {

    }
    public void showPlaceInformation()
    {
        if (previous_marker != null) {
            previous_marker.clear();//지역정보 마커 클리어
        }
        if(siteSelect == 0){
            new NRPlaces.Builder()
                    .listener(this)
                    .key("AIzaSyCbtgsQ7-HSgjDRnzoSCVpvj9z97IZ12mI")
                    .latlng(37.48424535608887, 126.9738164746693)//3사무실 좌표
                    .radius(500) //500 미터 내에서 검색
                    .type(PlaceType.CONVENIENCE_STORE) //편의점
                    .build()
                    .execute();
        }
        if(siteSelect == 1){
            new NRPlaces.Builder()
                    .listener(this)
                    .key("AIzaSyCbtgsQ7-HSgjDRnzoSCVpvj9z97IZ12mI")
                    .latlng(37.48424535608887, 126.9738164746693)
                    .radius(500) //500 미터 내에서 검색
                    .type(PlaceType.RESTAURANT) //식당
                    .build()
                    .execute();
        }
        if(siteSelect == 2){
            new NRPlaces.Builder()
                    .listener(this)
                    .key("AIzaSyCbtgsQ7-HSgjDRnzoSCVpvj9z97IZ12mI")
                    .latlng(37.48424535608887, 126.9738164746693)
                    .radius(500) //500 미터 내에서 검색
                    .type(PlaceType.PHARMACY) //약국
                    .build()
                    .execute();
        }

    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());


                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                //현재 위치에 마커 생성하고 이동 (이 주석을 풀면 계속 현재 위치로 감)
//                setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocatiion = location;
            }


        }

    };



    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);

        }

    }

    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            return "주소 미발견";
        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }
        return false;
    }
    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                }else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");


                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }

}