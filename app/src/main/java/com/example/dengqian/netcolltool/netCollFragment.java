package com.example.dengqian.netcolltool;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;



import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dengqian.netcolltool.bean.BaiDuLoactionDeal;
import com.example.dengqian.netcolltool.bean.SystemUtil;
import com.example.dengqian.netcolltool.bean.connNetReq;
import com.example.dengqian.netcolltool.bean.informDBHelper;
import com.example.dengqian.netcolltool.bean.information;
import java.lang.reflect.Method;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.dengqian.netcolltool.signTestingFragment.formatDecimalWithZero;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link netCollFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link netCollFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class netCollFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    //显示组件
    private TextView disp;
    //当前界面-用于获取其他组件
    private View view;
    //上下文
    private Context context;
    //采集信息类
    private information information;
    //下拉列表适配器
    private ArrayAdapter<String> adapter ;
    private ArrayAdapter<String> adapter2;

    //下拉列表组件
    private Spinner sp;
    private  Spinner sp2;
    private  Spinner sp3;
    //地址栏组件
    private EditText addressText;
    //小型持久化数据组件
    private SharedPreferences spForAddress;
    //SQLlite示例组件
    private informDBHelper sqLiteOpenHelper;
    private SQLiteDatabase db;
    //上传按钮 采集按钮
    private Button uploadButton;
    private Button collButton;
    //信号强度显示栏
    private TextView CurrentBess;
    public String currentDbmValue="0";
    public String currentNetType="4G";

    //当前imsi
    private String imsi;
    //当前定位类型
    private String provider;
    //手动申请权限列表
    private String []permissionList={Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

    //位置信息管理者
    private LocationManager locationManager;

    //位置信息提供者
    private String locationProvider;

    //地理定位显示框
    public TextView GpsAddress;

    //一级场景
    private String[] overlay1 = new String[] {"","城区", "乡镇","农村","交通"};
    //二级场景
    private String[][] overlay2 = new String[][]{
            {""},
            {"学校","商业区","景区","党政军","住宅","医院","酒店","企事业单位"},
            {"住宅区","景区","党政军","商业区","企事业单位","学校"},
            {"行政村","村寨","景区","学校"},
            {"高速","车站","高铁","公路","机场"}};
    public static final int NETWORK_NONE = 0; // 没有网络连接
    public static final int NETWORK_WIFI = 1; // wifi连接
    public static final int NETWORK_2G = 2; // 2G
    public static final int NETWORK_3G = 3; // 3G
    public static final int NETWORK_4G = 4; // 4G
    public static final int NETWORK_MOBILE = 5; // 手机流量


    private Activity activity;
    public static final int SHOW_RESPONSE=0;//用于更新操作




    public netCollFragment() {
        // Required empty public constructor
    }

    /**
     * 二级联动一级场景监听器，动态联动二级场景。
     */
    private AdapterView.OnItemSelectedListener selectListener1 = new AdapterView.OnItemSelectedListener(){
        public void onItemSelected(AdapterView parent, View v, int position,long id){
            int pos = sp.getSelectedItemPosition();

            adapter2 = new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_item, overlay2[pos]);
            sp2.setAdapter(adapter2);
        }

        public void onNothingSelected(AdapterView arg0){

        }

    };


    public static netCollFragment newInstance(String param1, String param2) {
        netCollFragment fragment = new netCollFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * 上传按钮点击监听。
     */
    class uploadButtonListener implements View.OnClickListener{
        private boolean isUp=false;
        private String alertText="未知错误";
        private String res="0";

        @Override
        public void onClick(View v) {
            if(information.checkData()){
                new Thread() {
                    public void run() {
                        boolean is2G=false;
                        information infoForSave=new information();
                        try {
                            List<information> listAll=new ArrayList<>();

                            if(information.getNetworkOperatorName().indexOf("2G")!=-1){
                                is2G=true;
                                information info1=new information(information);
                                info1.setID(UUID.randomUUID().toString());
                                info1.setNetworkOperatorName(info1.getNetworkOperatorName().replace("2G","4G"));
                                info1.setBSSS(Integer.valueOf(getString(R.string.BSSS4GMin)));
                                listAll.add(info1);
                                infoForSave=new information(info1);

                            }
                            else if(information.getNetworkOperatorName().indexOf("3G")!=-1){
                                is2G=true;
                                information info2=new information(information);
                                info2.setID(UUID.randomUUID().toString());
                                info2.setNetworkOperatorName(info2.getNetworkOperatorName().replace("3G","4G"));
                                info2.setBSSS(Integer.valueOf(getString(R.string.BSSS4GMin)));
                                listAll.add(info2);
                                infoForSave=new information(info2);

                            }
                            listAll.add(information);
                            for(information x:listAll){
                                Log.e("",x.show());
                            }
                            res = connNetReq.post(getString(R.string.allObjUpload), connNetReq.beanToJson(listAll));
                            //res=connNetReq.post(getString(R.string.singleObjUpload),connNetReq.beanToJson(information));

                        } catch (Exception e) {
                            alertText="网络错误，上传失败。"+e.toString();
                            Log.e("err",e.toString());
                        }

                        if(res.equals("1")){
                            if(is2G){
                                alertText =getString(R.string.is2G4Gwarm);
                            }else{
                                alertText="上传成功！";
                            }

                            isUp=true;
                            sqLiteOpenHelper.updateIsUpload(db,information.getID(),context);
                            information.setIsUpload("1");
                            Log.e("info1",information.show());
                            Log.e("infoForSave",infoForSave.show());

                            if(infoForSave.checkData()){
                                sqLiteOpenHelper.save(db,infoForSave,context);
                                sqLiteOpenHelper.updateIsUpload(db,infoForSave.getID(),context);
                            }

                        }
                        else
                            alertText="远程服务器错误,上传失败。";
                            Looper.prepare();
                            new Handler(context.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if(isUp){
                                        setDisplay();
                                        uploadButton.setEnabled(false);
                                    }
                                    Toast.makeText(context, alertText, Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            });

                    }
                }.start();
            }else{
                Toast.makeText(context, "采集数据校验失败，请等到信号强度不为0再上传", Toast.LENGTH_SHORT).show();
            };
        }
    }


    /**
     * 采集按钮点击监听
     */
    class collButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {

            SharedPreferences sharedPreferences=context.getSharedPreferences ("userInformation",Context.MODE_PRIVATE);


            /** 每次点击采集按钮都会创建一个新的数据对象用来保存采集的结果 */
            information=new information();
            if(sp3.getSelectedItem().toString().equals("")){
                Toast.makeText(activity, "请选择县市名称。", Toast.LENGTH_LONG).show();
                return ;
            }
            if(sp.getSelectedItem().toString().equals("")||sp2.getSelectedItem().toString().equals("")){
                Toast.makeText(activity, "请选择覆盖场景。", Toast.LENGTH_LONG).show();
                return ;
            }

            information.setOverlayScene(sp.getSelectedItem().toString()+"_"+sp2.getSelectedItem().toString());
            information.setDistrict(sp3.getSelectedItem().toString());
            information.setAddress(addressText.getText().toString().equals("")?"未输入":addressText.getText().toString());

            SharedPreferences.Editor edit=spForAddress.edit();
            edit.putString("lastAddress",addressText.getText().toString());
            edit.commit();




            //判断手机号是否有效
            if(isMobileNO(sharedPreferences.getString("user_phone",""))&&!addressText.getText().toString().equals("")&&!sharedPreferences.getString("user_name","").equals("")&&!sharedPreferences.getString("user_department","").equals(""))
            {
                uploadButton.setEnabled(true);
            try {

                //获取系统服务
                TelephonyManager mTelephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
                locationManager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
                TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);




                //鉴权后执行GPS采集
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                {
                    information.setGPS(getLocation(context));
                }
                else
                    information.setGPS("No GPS");
                imsi = mTelephonyManager.getSubscriberId();
                if(tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA){
                    CdmaCellLocation cdmacelllocation = (CdmaCellLocation)tm.getCellLocation();
                    information.setTAC(String.valueOf(cdmacelllocation.getNetworkId()));
                    information.setECI(String.valueOf(cdmacelllocation.getBaseStationId()));
                }else{
                    GsmCellLocation gsmCellLocation = (GsmCellLocation) mTelephonyManager.getCellLocation();
                    information.setTAC(String.valueOf(gsmCellLocation.getLac()));
                    information.setECI(String.valueOf(gsmCellLocation.getCid()));
                }

//                information.setTAC("0");
//                information.setECI("0");
                if(currentNetType.equals("无网络")){
                    information.setBSSS(-130);
                }else{
                    information.setBSSS(Integer.valueOf(currentDbmValue));
                }


                // 中国移动和中国联通获取LAC、CID、BSSS的方式
                //中国移动（China Mobile）
                if (imsi.startsWith("46000")||imsi.startsWith("46007")||imsi.startsWith("46002")){
                    information.setNetworkOperatorName("中国移动 "+currentNetType);
                }
                //中国联通（China Unicom）
                else if(imsi.startsWith("46001")||imsi.startsWith("46006")){
                    information.setNetworkOperatorName("中国联通 "+currentNetType);
                }
                //中国铁通（China Tietong）
                else if(imsi.startsWith("46020")){
                    information.setNetworkOperatorName("中国铁通 "+currentNetType);
                }

                //中国电信（China Telecom）CDMA网络
                else if (imsi.startsWith("46003")||imsi.startsWith("46011")||imsi.startsWith("46005")) {
                    information.setNetworkOperatorName("中国电信 "+currentNetType);
                }else{
                    information.setNetworkOperatorName("未知网络 "+currentNetType);
                }
                //手机品牌类型
                StringBuffer phoneType=new StringBuffer();
                phoneType.append(SystemUtil.getDeviceBrand()+";");
                phoneType.append(SystemUtil.getSystemModel()+";");
                phoneType.append(SystemUtil.getIMEI(context));
                information.setPhoneType(phoneType.toString());

                Date day=new Date();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                information.setCollTime(df.format(day));

                SharedPreferences sp=context.getSharedPreferences ("userInformation",Context.MODE_PRIVATE);

                information.setPhoneNumber(sharedPreferences.getString("user_name","未填写")+"_"+sharedPreferences.getString("user_phone","未填写")+"_"+sharedPreferences.getString("user_department","未填写"));
                information.setIsUpload("0");
                information.setID(UUID.randomUUID().toString());
                setDisplay();
                /** 保存每次采集的数据 **/
                sqLiteOpenHelper.save(db,information,context);
                /** 删除数据采集表 **/
                //db.execSQL("drop table NetWorkInfor");
                /** 创建数据采集表 **/
                //sqLiteOpenHelper.onCreate(db);
                /** 删除表数据 */
                //db.execSQL("delete from NetWorkInfor");
            }catch(Exception e){
                disp.append("err:"+e.toString());
            }
            } else {
                if (addressText.getText().toString().equals("")){
                    Toast.makeText(activity, "详细地址不能未空。", Toast.LENGTH_LONG).show();
                }
                else if(sharedPreferences.getString("user_department","").equals("")){
                    Toast.makeText(activity, "请在个人界面选择所属部门", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(activity, "请在个人信息界面填写有效的手机号与姓名，否则无法采集。", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity=(MainActivity) this.getActivity();

        requestPower();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_net_coll, container, false);
        addressText=(EditText)view.findViewById(R.id.address);
        disp=view.findViewById(R.id.show);
        CurrentBess=view.findViewById(R.id.CurrentBess);
        collButton=view.findViewById(R.id.collButton);
        sqLiteOpenHelper=new informDBHelper(context);
        db=sqLiteOpenHelper.getReadableDatabase();
        uploadButton=(Button)view.findViewById(R.id.upLoadButton);
        uploadButton.setEnabled(false);
        spForAddress=context.getSharedPreferences ("address",Context.MODE_PRIVATE);
        addressText.setText(spForAddress.getString("lastAddress",""));
        /** 二级联动*/
        sp = (Spinner) view.findViewById(R.id.overlayScene1C);
        sp2=(Spinner)view.findViewById(R.id.overlayScene2C);
        sp3=(Spinner)view.findViewById(R.id.districtC);
        adapter = new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_item,overlay1 );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(selectListener1);
        adapter2=new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_item, overlay2[0]);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp2.setAdapter(adapter2);
        /** */
        collButton=view.findViewById(R.id.collButton);
        collButton.setOnClickListener(new collButtonListener());
        uploadButton.setOnClickListener(new uploadButtonListener());
        /**监听当前信号*/
        TelephonyManager tmm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        boolean isSIM=hasSimCard(tmm);
        if(isSIM ){
            mylistener listener=new mylistener();
            tmm.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }else{
                collButton.setEnabled(false);
                Toast.makeText(activity, "未检测到SIM卡,或SIM卡无效", Toast.LENGTH_LONG).show();
        }
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationProvider=LocationManager.GPS_PROVIDER;
//        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
//        {
//            locationManager.requestLocationUpdates(locationProvider, 3000, 100, locationListener);
//        }
//        GpsAddress=view.findViewById(R.id.GPSaddress);
        String []gps=getLocation2(context);

        getLocalion(gps[0],gps[1]);

        return view;
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;

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
    public void getLocalion(String lon, final String lat){
        final String latt=lat;
        final String lonn=lon;
        new Thread() {
            public String res="";
            @Override
            public void run() {
                super.run();
                try{
                    String jj=BaiDuLoactionDeal.getRequest(lonn,latt);
                    res=BaiDuLoactionDeal.formatAddress(jj);

                }catch(Exception e){
                    res="未知地址";
                }finally {
                    Looper.prepare();
                    new Handler(context.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            addressText.setText(res);
                            Looper.loop();
                        }
                    });
                }
            }
        }.start();
    }





    /**
     * 动态授权
     */


    public void requestPower() {
        //判断是否已经赋予权限
        for (String permissionInfo : permissionList) {
            if (ContextCompat.checkSelfPermission(activity, permissionInfo) != PackageManager.PERMISSION_GRANTED) {

                //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionInfo)) {
                    Toast.makeText(activity, "请放开权限以保证应用使用正常！", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(activity, new String[]{permissionInfo}, 1);
                } else {
                    //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                    ActivityCompat.requestPermissions(activity,
                            new String[]{permissionInfo}, 1);
                }
            }

        }


    }



    class mylistener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            TelephonyManager mTelephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);


            try{
                Method method1 = null;

                method1 = signalStrength.getClass().getMethod("getDbm");
                String dbm = method1.invoke(signalStrength).toString();

                method1 = signalStrength.getClass().getMethod("getGsmDbm");
                String dbm2 = method1.invoke(signalStrength).toString();

                method1 = signalStrength.getClass().getMethod("getLteDbm");
                String dbm3 = method1.invoke(signalStrength).toString();

                method1 = signalStrength.getClass().getMethod("getTdScdmaDbm");
                String dbm4 = method1.invoke(signalStrength).toString();


                if(getNetWorkType(mTelephonyManager).equals("无网络")){
                    currentNetType="无网络";
                    CurrentBess.setText(" "+currentNetType+"信号强度:"+"-130");
                    currentDbmValue="-130";
                }else{
                    if(Integer.valueOf(dbm2)>=-130&&Integer.valueOf(dbm2)<-1&&dbm.equals(dbm2)){
                        currentNetType="2G";
                    }
                    else if(Integer.valueOf(dbm3)>=-130&&Integer.valueOf(dbm3)<-1&&dbm.equals(dbm3)){
                        currentNetType="4G";
                    }
                    else if(Integer.valueOf(dbm4)>=-130&&Integer.valueOf(dbm4)<-1&&dbm.equals(dbm4)){
                        currentNetType="3G";
                    }else{
                        currentNetType=getNetWorkType(mTelephonyManager);
                    }
                    CurrentBess.setText(" "+currentNetType+"信号强度:"+dbm);
                    currentDbmValue=dbm;
                }
            }catch (Exception e){
                currentDbmValue="-1";
                CurrentBess.setText("-1");
            }
        }
    }


    /**
     * 查看当前是否存在SIM卡
     * @param telMgr
     * @return
     */
    public  boolean hasSimCard(TelephonyManager telMgr) {
        int simState = telMgr.getSimState();
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                result = false; // 没有SIM卡
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false;
                break;
        }
        return result;
    }







    /**
     * 判断手机号是否合法
     * @param mobiles
     * @return
     */

    public boolean isMobileNO(String mobiles) {

        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

        Matcher m = p.matcher(mobiles);

        System.out.println(m.matches() + "---");

        return m.matches();

    }

    /**
     * 将当前类的information显示在面板
     */
    public void setDisplay(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:s");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        StringBuffer sb=new StringBuffer();
        sb.append("---------------"+str+"------------------\n");
        sb.append("TAC:"+information.getTAC()+"\n");
        sb.append("ECI:"+information.getECI()+"\n");
        sb.append("GPS:"+information.getGPS()+"\n");
        sb.append("信号强度:"+information.getBSSS()+"\n");
        sb.append("手机类型:"+information.getPhoneType()+"\n");
        sb.append("手机号码:"+information.getPhoneNumber()+"\n");
        sb.append("覆盖场景:"+information.getOverlayScene()+"\n");
        sb.append("区县:"+information.getDistrict()+"\n");
        sb.append("地址:"+information.getAddress()+"\n");
        sb.append("运营商信息:"+information.getNetworkOperatorName()+"\n");
        sb.append("是否上传:"+(information.getIsUpload().equals("0")?"未上传":"已上传")+"\n");
        sb.append("IMSI:"+imsi+"\n");
        sb.append("-----------------------------------------");
        disp.setText(sb.toString());
    }

    /**
     * 调用本地GPS来获取经纬度
     * @param context
     */
    private String getLocation(Context context) {
        //1.获取位置管理器

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
            return "no GPS..";
        }
        Location location=null;
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            return "("+String.valueOf(location.getLongitude())+","+String.valueOf(location.getLatitude())+")";


        } else {
            return "no GPS.";
        }
    }

    private String[] getLocation2(Context context) {
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





    private String getNetWorkType(TelephonyManager mTelephonyManager){
//        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); // 获取网络服务
//        if (null == connManager) { // 为空则认为无网络
//            return "无网络";
//        }
//
//        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
//        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
//            return "无网络";
//        }
//
//
//        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        if (null != wifiInfo) {
//            NetworkInfo.State state = wifiInfo.getState();
//            if (null != state) {
//                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
//                    return "WIFI";
//                }
//            }
//        }


        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {

            // 2G网络
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            // 3G网络
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            // 4G网络
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            default:
                return "无网络";
        }
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


    public void setGpsView(String gps){




            new Thread(){
                @Override
                public void run() {
                    try{
                        String res=BaiDuLoactionDeal.getRequest("","");
                        Log.e("",res);
                    }catch(Exception e){
                        Log.e("",e.toString());
                    }

                    Looper.prepare();
                    new Handler(context.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            GpsAddress.setText("我去玩邓千我邓千王丹我去单位");
                            Looper.loop();
                        }
                    });

                }
            }.start();





    }




}
