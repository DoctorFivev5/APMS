package com.huangfuren.amusementparkmanagementsystem.map;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.huangfuren.amusementparkmanagementsystem.R;

import java.util.ArrayList;

public class MapDemoActivity extends AppCompatActivity  implements SensorEventListener {

    private final int SDK_PERMISSION_REQUEST = 127;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private BitmapDescriptor geo;
    private String permissionInfo;

    //防止每次定位都重新设置中心点和marker
    boolean isFirstLoc = true;
    //初始化LocationClient定位类
    private LocationClient mLocationClient;
    //BDAbstractLocationListener为7.2版本新增的Abstract类型的监听接口，原有BDLocationListener接口
    private BDLocationListener myListener;

    //手机方向
    private int mCurrentDirection = 0;
    //手机X轴方向
    private Double lastX = 0.0;
    //
    private MyLocationData locData;
    //纬度
    private double mCurrentLat = 0.0;
    //经度
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getPersimmions();
        initView();
    }

    private void initView() {

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        //描述地图将要发生的变化，使用工厂类MapStatusUpdateFactory创建，设置级别
        //为18，进去就是18了，默认是12
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(18);
        mBaiduMap.setMapStatus(mapStatusUpdate);
        //是否显示缩放按钮
        //mMapView.showZoomControls(false);
        //经纬度(纬度，经度) 我们这里设置南昌万达的位置
        LatLng latlng = new LatLng(28.5836239258,115.7923688465);
        MapStatusUpdate mapStatusUpdate_circle = MapStatusUpdateFactory.newLatLng(latlng);
        mBaiduMap.setMapStatus(mapStatusUpdate_circle);
        /*
        设置地图类型
         */
        //普通地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        //卫星地图
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        //空白地图, 基础地图瓦片将不会被渲染。在地图类型中设置为NONE，将不会使用流量下载基础地图瓦片图层。使用场景：与瓦片图层一起使用，节省流量，提升自定义瓦片图下载速度。
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
        //开启交通图
//        mBaiduMap.setTrafficEnabled(true);
        //关闭缩放按钮
        mMapView.showZoomControls(false);

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        /**
         * 对定位的图标进行配置，需要MyLocationConfiguration实例，这个类是用设置定位图标的显示方式的
         */
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        myListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myListener);
        initLocation();
        //开始定位
        mLocationClient.start();
        //设置地图单击监听
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {

            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0) {
                // TODO Auto-generated method stub

            }
        });

        //覆盖物点击事件
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                // TODO Auto-generated method stub
                return false;
            }
        });
    }

//    private void lacate() {
//        myListener = new MyLocationListener();
//        mLocationClient.registerLocationListener(myListener);
//        LocationClientOption option = new LocationClientOption();
//        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
//        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
//        option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
//        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
//        option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
//        mLocationClient.setLocOption(option);
//        geo = BitmapDescriptorFactory
//                .fromResource(R.drawable.ic_back);
//        MyLocationConfiguration configuration = new MyLocationConfiguration(
//                MyLocationConfiguration.LocationMode.FOLLOWING, true, geo);
//        mBaiduMap.setMyLocationConfigeration(configuration);// 设置定位显示的模式
//        mBaiduMap.setMyLocationEnabled(true);// 打开定位图层
//    }

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
			/*
			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)){
                return true;
            }else{
                permissionsList.add(permission);
                return false;
            }

        }else{
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
    /**
     * 配置定位参数
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("BD09LL");
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        int span = 5000;
        option.setScanSpan(span);
        //可选，设置是否需要设备方向结果
        option.setNeedDeviceDirect(false);
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);
        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setLocationNotify(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIsNeedLocationPoiList(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        option.setEnableSimulateGps(false);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        // 退出时销毁定位
        mLocationClient.unRegisterLocationListener(myListener);
        mLocationClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    class MyListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation result) {
            if (result != null) {
                MyLocationData data = new MyLocationData.Builder()
                        .latitude(result.getLatitude())
                        .longitude(result.getLongitude()).build();
                mBaiduMap.setMyLocationData(data);
            }
        }

    }

    /**
     * 实现定位监听 位置一旦有所改变就会调用这个方法
     * 可以在这个方法里面获取到定位之后获取到的一系列数据
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //获取定位结果
//            location.getTime();    //获取定位时间
//            location.getLocationID();    //获取定位唯一ID，v7.2版本新增，用于排查定位问题
//            location.getLocType();    //获取定位类型
//            location.getLatitude();    //获取纬度信息
//            location.getLongitude();    //获取经度信息
//            location.getRadius();    //获取定位精准度
//            location.getAddrStr();    //获取地址信息
//            location.getCountry();    //获取国家信息
//            location.getCountryCode();    //获取国家码
//            location.getCity();    //获取城市信息
//            location.getCityCode();    //获取城市码
//            location.getDistrict();    //获取区县信息
//            location.getStreet();    //获取街道信息
//            location.getStreetNumber();    //获取街道码
//            location.getLocationDescribe();    //获取当前位置描述信息
//            location.getPoiList();    //获取当前位置周边POI信息
//
//            location.getBuildingID();    //室内精准定位下，获取楼宇ID
//            location.getBuildingName();    //室内精准定位下，获取楼宇名称
//            location.getFloor();    //室内精准定位下，获取当前位置所处的楼层信息

            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder().accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
            Log.e("定位信息：", "纬度："+mCurrentLat+"--经度："+mCurrentLon);
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }

    /**
     * 设置中心点和添加marker
     *
     * @param map
     * @param bdLocation
     * @param isShowLoc
     */
    public void setPosition2Center(BaiduMap map, BDLocation bdLocation, Boolean isShowLoc) {
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                .direction(bdLocation.getRadius())
                .latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude())
                .build();
        map.setMyLocationData(locData);

        if (isShowLoc) {
            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            map.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //每次方向改变，重新给地图设置定位数据，用上一次onReceiveLocation得到的经纬度、精度
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {// 方向改变大于1度才设置，以免地图上的箭头转动过于频繁
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder().accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat).longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);

        }
        lastX = x;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

