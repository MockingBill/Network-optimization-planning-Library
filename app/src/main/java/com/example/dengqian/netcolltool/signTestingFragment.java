package com.example.dengqian.netcolltool;


import android.Manifest;
import android.app.Activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dengqian.netcolltool.bean.AesAndToken;
import com.example.dengqian.netcolltool.bean.connNetReq;
import com.example.dengqian.netcolltool.bean.weakInformation;
import com.example.dengqian.netcolltool.widget.CustomDatePicker;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class signTestingFragment extends ListFragment {
    //本页面列表数据
    private ArrayList<HashMap<String,String>> list=null;
    private SimpleAdapter listAdapter=null;
    private netCollFragment.OnFragmentInteractionListener mListener;
    //显示组件
    private TextView disp;
    //当前界面-用于获取其他组件
    private View view;
    //上下文
    private Context context;
    //主active
    private Activity activity;
    //一号窗口
    private TextView t1;


    private LinearLayout selectDate;
    private TextView currentDate;
    private CustomDatePicker customDatePicker1;
    private Button weak_query_button;
    private EditText collTime;
    private Spinner distract;
    private EditText address;
    private LocationManager locationManager;
    private String locationProvider;

    public signTestingFragment() {
        // Required empty public constructor
    }





    public static netCollFragment newInstance(String param1, String param2) {
        netCollFragment fragment = new netCollFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signaltesting, container, false);

        selectDate = (LinearLayout)  view.findViewById(R.id.selectDate);
        currentDate = (TextView) view.findViewById(R.id.currentDate);

        //设置当前GPS的值
        setGpsView(getLocation(context));
        locationProvider=LocationManager.GPS_PROVIDER;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            locationManager.requestLocationUpdates(locationProvider, 3000, 100, locationListener);
        }



        initDatePicker();
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDatePicker1.show(currentDate.getText().toString());
            }
        });



        list=new ArrayList<HashMap<String,String>>();
        for(int i =0;i<=3;i++){

            HashMap map=new HashMap<String,String>();
            map.put("row_weak_address","三穗县八弓镇书香明苑1栋2单元");
            map.put("row_weak_city","凯里市");
            map.put("row_weak_bsss","-130");
            map.put("row_weak_collect_time","2018-07-24 14:33:22");
            map.put("row_weak_network_type","4G");
            map.put("row_weak_dis","20Km");
            list.add(map);
        }


        listAdapter=new SimpleAdapter(activity,list,R.layout.list_view_row_,
                new String[]{
                        "row_weak_address",
                        "row_weak_city",
                        "row_weak_bsss",
                        "row_weak_collect_time",
                        "row_weak_network_type",
                        "row_weak_dis"
                },
                new int[]{
                        R.id.row_weak_address,
                        R.id.row_weak_city,
                        R.id.row_weak_bsss,
                        R.id.row_weak_collect_time,
                        R.id.row_weak_network_type,
                        R.id.row_weak_dis
        });

        setListAdapter(listAdapter);

        distract=view.findViewById(R.id.weakQuery_district_value);
        address=view.findViewById(R.id.weakQuery_address_value);

        weak_query_button=(Button) view.findViewById(R.id.weak_query);
        weak_query_button.setOnClickListener(new QueryListener());



        return view;
    }


    /**
     * 弱覆盖查询按钮点击事件
     */
    public class QueryListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            new Thread(){
                @Override
                public void run() {
                    try{
                        String []gps=getLocation(context);
                        String res = connNetReq.post(getString(R.string.getWeakForConfirm),
                                connNetReq.weakQueryToJson(currentDate.getText().toString(),
                                        address.getText().toString(), distract.getSelectedItem().toString(),gps[0],gps[1]));

                        res= AesAndToken.decrypt(res,AesAndToken.KEY);
                        List<weakInformation> weakList=connNetReq.jsonToWeakInf(res);
                        for(weakInformation wi:weakList){
                            Log.e("",wi.show());
                        }


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    //上传后的UI操作放在UI线程中
                    Looper.prepare();
                    new Handler(context.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "刷新成功", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    });

                }
            }.start();

        }
    }

    public void refreshList(){

        list.clear();
        for(int i =0;i<=3;i++){
            HashMap map=new HashMap<String,String>();
            map.put("row_address","name:"+i+10);
            map.put("CollTime","age"+i+10);
            list.add(map);
        }
        listAdapter.notifyDataSetChanged();
    }

    private LocationListener locationListener=new LocationListener() {

        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            setGpsView(getLocation(context));
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            setGpsView(getLocation(context));

        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            setGpsView(getLocation(context));


        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
            Toast.makeText(activity, "请放开权限以保证应用使用正常！", Toast.LENGTH_LONG).show();
        }
    };





    public void setGpsView(String [] gps){
        TextView lon=view.findViewById(R.id.GPS_lon);
        TextView lat=view.findViewById(R.id.GPS_lat);
        lon.setText(gps[0]);
        lat.setText(gps[1]);
    }




    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        currentDate.setText(now.split(" ")[0]);


        customDatePicker1 = new CustomDatePicker(activity, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                currentDate.setText(time.split(" ")[0]);
            }
        }, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker1.showSpecificTime(false); // 不显示时和分
        customDatePicker1.setIsLoop(false); // 不允许循环滚动

    }

    private String[] getLocation(Context context) {
        //1.获取位置管理器
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider="";
        //2.获取位置提供器，GPS或是NetWork
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是网络定位
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS定位
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.PASSIVE_PROVIDER)) {
            //如果是PASSIVE定位
            locationProvider = LocationManager.PASSIVE_PROVIDER;
        }
        else {
            Toast.makeText(activity, "请放开权限以保证应用使用正常！", Toast.LENGTH_LONG).show();
            return new String[]{"-1","-1"};
        }
        Location location=null;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            String []lonlat={formatDecimalWithZero(String.valueOf(location.getLongitude()),7),formatDecimalWithZero(String.valueOf(location.getLatitude()),7)};
            return lonlat;


        } else {
            return new String[]{"-1","-1"};
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        if (context instanceof netCollFragment.OnFragmentInteractionListener) {
            mListener = (netCollFragment.OnFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public static String formatDecimalWithZero(String d, int newScale) {
        String pattern = "0.";
        for (int i = 0; i < newScale; i++) {
            pattern += "0";
        }
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(Double.valueOf(d));
    }


}
