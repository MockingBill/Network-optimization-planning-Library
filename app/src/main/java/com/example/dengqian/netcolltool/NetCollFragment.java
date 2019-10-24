package com.example.dengqian.netcolltool;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;

import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dengqian.netcolltool.bean.BaiDuLoactionDeal;
import com.example.dengqian.netcolltool.bean.SystemUtil;
import com.example.dengqian.netcolltool.bean.connNetReq;
import com.example.dengqian.netcolltool.bean.informDBHelper;
import com.example.dengqian.netcolltool.bean.information;

import java.lang.reflect.Method;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;
import static com.example.dengqian.netcolltool.SignConfirmFragment.formatDecimalWithZero;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NetCollFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NetCollFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NetCollFragment extends Fragment {
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
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter2;

    //下拉列表组件
    private Spinner sp;
    private Spinner sp2;
    private Spinner sp3;
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
    public String currentDbmValue = "0";
    public String currentNetType = "4G";
    public EditText CurrentGPS;
    public Button updateGPS;

    public TextView neighborShow;

    //当前imsi
    private String imsi;
    //当前定位类型
    private String provider;
    //手动申请权限列表
    private String[] permissionList = {
            Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.REQUEST_INSTALL_PACKAGES
    };

    //位置信息管理者
    private LocationManager locationManager;

    //位置信息提供者
    private String locationProvider;

    //地理定位显示框
    public TextView GpsAddress;

    //一级场景
    private String[] overlay1 = new String[]{"", "城区", "乡镇", "农村", "交通"};
    //二级场景
    private String[][] overlay2 = new String[][]{
            {""},
            {"学校", "商业区", "景区", "党政军", "住宅", "医院", "酒店", "企事业单位"},
            {"住宅区", "景区", "党政军", "商业区", "企事业单位", "学校"},
            {"行政村", "村寨", "景区", "学校"},
            {"高速", "车站", "高铁", "公路", "机场"}};
    public static final int NETWORK_NONE = 0; // 没有网络连接
    public static final int NETWORK_WIFI = 1; // wifi连接
    public static final int NETWORK_2G = 2; // 2G
    public static final int NETWORK_3G = 3; // 3G
    public static final int NETWORK_4G = 4; // 4G
    public static final int NETWORK_MOBILE = 5; // 手机流量


    private Activity activity;
    public static final int SHOW_RESPONSE = 0;//用于更新操作
    TelephonyManager OnlyTeleMan=null;
    private Runnable timerRun;
    private mylistener listenerSign = null;
    private mylistener listenerNetwork=null;

    public NetCollFragment() {
        // Required empty public constructor
    }

    /**
     * 二级联动一级场景监听器，动态联动二级场景。
     */
    private AdapterView.OnItemSelectedListener selectListener1 = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView parent, View v, int position, long id) {
            int pos = sp.getSelectedItemPosition();

            adapter2 = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, overlay2[pos]);
            sp2.setAdapter(adapter2);
        }

        public void onNothingSelected(AdapterView arg0) {

        }

    };


    public static NetCollFragment newInstance(String param1, String param2) {
        NetCollFragment fragment = new NetCollFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * 上传按钮点击监听。
     */
    class uploadButtonListener implements View.OnClickListener {
        private boolean isUp = false;
        private String alertText = "未知错误";
        private String res = "0";

        @Override
        public void onClick(View v) {
            if (information.checkData()) {
                new Thread() {
                    public void run() {
                        boolean is2G = false;
                        information infoForSave = new information();
                        try {
                            List<information> listAll = new ArrayList<>();

                            if (information.getNetworkOperatorName().indexOf("2G") != -1) {
                                is2G = true;
                                information info1 = new information(information);
                                info1.setID(UUID.randomUUID().toString());
                                info1.setNetworkOperatorName(info1.getNetworkOperatorName().replace("2G", "4G"));
                                info1.setBSSS(Integer.valueOf(getString(R.string.BSSS4GMin)));
                                listAll.add(info1);
                                infoForSave = new information(info1);

                            } else if (information.getNetworkOperatorName().indexOf("3G") != -1) {
                                is2G = true;
                                information info2 = new information(information);
                                info2.setID(UUID.randomUUID().toString());
                                info2.setNetworkOperatorName(info2.getNetworkOperatorName().replace("3G", "4G"));
                                info2.setBSSS(Integer.valueOf(getString(R.string.BSSS4GMin)));
                                listAll.add(info2);
                                infoForSave = new information(info2);

                            }
                            listAll.add(information);

                            res = connNetReq.post(getString(R.string.allObjUpload), connNetReq.beanToJson(listAll));

                        } catch (Exception e) {
                            alertText = "网络错误，上传失败。" + e.toString();
                            Log.e("err", e.toString());
                        }

                        if (res.equals("1")) {
                            if (is2G) {
                                alertText = getString(R.string.is2G4Gwarm);
                            } else {
                                alertText = "上传成功！";
                            }

                            isUp = true;
                            sqLiteOpenHelper.updateIsUpload(db, information.getID());
                            information.setIsUpload("1");
                            if (infoForSave.checkData()) {
                                sqLiteOpenHelper.save(db, infoForSave, activity);
                                sqLiteOpenHelper.updateIsUpload(db, infoForSave.getID());
                            }
                        } else
                            alertText = "远程服务器错误,上传失败。";
                        Looper.prepare();
                        new Handler(context.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (isUp) {
                                    setDisplay();
                                    uploadButton.setEnabled(false);
                                }
                                Toast.makeText(context, alertText, Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        });

                    }
                }.start();
            } else {
                Toast.makeText(context, "采集数据校验失败，请等到信号强度不为0再上传", Toast.LENGTH_SHORT).show();
            }
            ;
        }
    }


    /**
     * 采集按钮点击监听
     */
    class collButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            SharedPreferences sharedPreferences = context.getSharedPreferences("userInformation", Context.MODE_PRIVATE);


            /** 每次点击采集按钮都会创建一个新的数据对象用来保存采集的结果 */
            information = new information();
            if (sp3.getSelectedItem().toString().equals("")) {
                Toast.makeText(activity, "请选择县市名称。", Toast.LENGTH_LONG).show();
                return;
            }
            if (sp.getSelectedItem().toString().equals("") || sp2.getSelectedItem().toString().equals("")) {
                Toast.makeText(activity, "请选择覆盖场景。", Toast.LENGTH_LONG).show();
                return;
            }

            information.setOverlayScene(sp.getSelectedItem().toString() + "_" + sp2.getSelectedItem().toString());
            information.setDistrict(sp3.getSelectedItem().toString());
            information.setAddress(addressText.getText().toString().equals("") ? "未输入" : addressText.getText().toString());

            SharedPreferences.Editor edit = spForAddress.edit();
            edit.putString("lastAddress", addressText.getText().toString());
            edit.commit();


            //判断手机号是否有效
            if (isMobileNO(sharedPreferences.getString("user_phone", "")) && !addressText.getText().toString().equals("") && !sharedPreferences.getString("user_name", "").equals("") && !sharedPreferences.getString("user_department", "").equals("")) {
                uploadButton.setEnabled(true);
                try {

                    //获取系统服务

                    locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);



                    //鉴权后执行GPS采集
                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        information.setGPS(getGPSLocation());
                    } else
                        information.setGPS("No GPS");

                    imsi = OnlyTeleMan.getSubscriberId();

                    /**
                     * 获取邻区信息
                     */
                    int lac=0;
                        int cid=0;
                        int dbm=-1;
                        int arfcn=0;
                        List<CellInfo> cellList=OnlyTeleMan.getAllCellInfo();
                        StringBuffer neiString=new StringBuffer();
                        for(CellInfo statcellinfo:cellList){
                            /**
                             * GSM
                             */
                            if(statcellinfo instanceof CellInfoGsm){
                                CellInfoGsm cellInfoGsm=(CellInfoGsm)statcellinfo;
                                CellIdentityGsm cellIdentity = cellInfoGsm.getCellIdentity();

                                lac = cellIdentity.getLac();
                                cid = cellIdentity.getCid();
                                dbm=cellInfoGsm.getCellSignalStrength().getDbm();


                                String mccnc=String.valueOf(cellIdentity.getMcc())+String.valueOf(cellIdentity.getMnc());
                                String oper=mccnc2oper(mccnc);
                                neiString.append(
                                        "无线协议 : GSM"+"\n"+
                                        "运营商: "+oper+"\n"+
                                        "mcc mnc : "+String.valueOf(cellIdentity.getMcc())+"-"+String.valueOf(cellIdentity.getMnc())+"\n"+
                                        "Lac : "+String.valueOf(lac)+"\n"+
                                        "Cid : "+String.valueOf(cid)+"\n"+
                                        "信号强度 : "+String.valueOf(dbm)+"\n"+
                                        "-----------------------------------\n"
                                );
                            }
                            /**
                             * wCDMA
                             */
                            else if(statcellinfo instanceof CellInfoWcdma){
                                CellInfoWcdma cellInfoWcdma=(CellInfoWcdma)statcellinfo;
                                CellIdentityWcdma cellIdentity = cellInfoWcdma.getCellIdentity();
                                CellSignalStrengthWcdma cssw=cellInfoWcdma.getCellSignalStrength();

                                lac = cellIdentity.getLac();
                                cid = cellIdentity.getCid();
                                String mccnc=String.valueOf(cellIdentity.getMcc())+String.valueOf(cellIdentity.getMnc());
                                String oper=mccnc2oper(mccnc);
                                dbm=cssw.getDbm();
                                neiString.append(
                                        "无线协议 : wCDMA"+"\n"+
                                        "运营商: "+oper+"\n"+
                                        "mcc mnc : "+mccnc+"\n"+
                                        "Lac : "+String.valueOf(lac)+"\n"+
                                        "Cid : "+String.valueOf(cid)+"\n"+
                                        "信号强度 : "+String.valueOf(dbm)+"\n"+
                                        "-----------------------------------"
                                );

                            }
                            /**
                             * LTE
                             */
                            else if(statcellinfo instanceof CellInfoLte){
                                CellInfoLte cellInfoLte=(CellInfoLte)statcellinfo;
                                CellIdentityLte cellIdentity = cellInfoLte.getCellIdentity();
                                lac = cellIdentity.getTac();
                                cid = cellIdentity.getCi();
                                String mccnc=String.valueOf(cellIdentity.getMcc())+String.valueOf(cellIdentity.getMnc());
                                String oper=mccnc2oper(mccnc);
                                CellSignalStrengthLte cssl =  cellInfoLte.getCellSignalStrength();
                                dbm=cssl.getDbm();
                                neiString.append(
                                                "无线协议 : LTE"+"\n"+
                                                "运营商: "+oper+"\n"+
                                                "mcc mnc : "+mccnc+"\n"+
                                                "Tac : "+String.valueOf(lac)+"\n"+
                                                "Ci : "+String.valueOf(cid)+"\n"+
                                                "信号强度 : "+String.valueOf(dbm)+"\n"+
                                                "-----------------------------------"
                                );

                            }
                            /**
                             *  CDMA
                             */
                            else if(statcellinfo instanceof CellInfoCdma ){
                                CellInfoCdma cellInfocdma=(CellInfoCdma)statcellinfo;
                                CellIdentityCdma cellIdentity= cellInfocdma.getCellIdentity();
                                lac=cellIdentity.getNetworkId();
                                cid=cellIdentity.getBasestationId();
                                String oper="中国电信";
                                dbm=cellInfocdma.getCellSignalStrength().getDbm();
                                neiString.append(
                                                "无线协议 : CDMA"+"\n"+
                                                "运营商: "+oper+"\n"+
                                                "NetworkId : "+String.valueOf(lac)+"\n"+
                                                "BasestationId : "+String.valueOf(cid)+"\n"+
                                                "信号强度 : "+String.valueOf(dbm)+"\n"+
                                                "-----------------------------------"
                                );

                            }
                        }
                        neighborShow.setText(neiString.toString());


                        if(OnlyTeleMan!=null&&OnlyTeleMan.getAllCellInfo().size()>0){
                            CellInfo statcellinfo=OnlyTeleMan.getAllCellInfo().get(0);
                            if(statcellinfo instanceof CellInfoGsm){
                                CellInfoGsm cellInfoGsm=(CellInfoGsm)statcellinfo;
                                CellIdentityGsm cellIdentity = cellInfoGsm.getCellIdentity();
                                lac = cellIdentity.getLac();
                                cid = cellIdentity.getCid();

                            }
                            else if(statcellinfo instanceof CellInfoWcdma){
                                CellInfoWcdma cellInfoWcdma=(CellInfoWcdma)statcellinfo;
                                CellIdentityWcdma cellIdentity = cellInfoWcdma.getCellIdentity();
                                lac = cellIdentity.getLac();
                                cid = cellIdentity.getCid();
                            }
                            else if(statcellinfo instanceof CellInfoLte){
                                CellInfoLte cellInfoLte=(CellInfoLte)statcellinfo;
                                CellIdentityLte cellIdentity = cellInfoLte.getCellIdentity();
                                lac = cellIdentity.getTac();
                                cid = cellIdentity.getCi();
                            }
                            else if(statcellinfo instanceof CellInfoCdma ){
                                CellInfoCdma cellInfocdma=(CellInfoCdma)statcellinfo;
                                CellIdentityCdma cellIdentity= cellInfocdma.getCellIdentity();
                                lac=cellIdentity.getNetworkId();
                                cid=cellIdentity.getBasestationId();
                            }
                            information.setTAC(String.valueOf(lac));
                            information.setECI(String.valueOf(cid));
                        }




                    if (currentNetType.equals("无网络")) {
                        information.setBSSS(-130);
                    } else {
                        information.setBSSS(Integer.valueOf(currentDbmValue));
                    }


                    // 中国移动和中国联通获取LAC、CID、BSSS的方式
                    //中国移动（China Mobile）
                    if (imsi.startsWith("46000") || imsi.startsWith("46007") || imsi.startsWith("46002")) {
                        information.setNetworkOperatorName("中国移动_" + currentNetType);
                    }
                    //中国联通（China Unicom）
                    else if (imsi.startsWith("46001") || imsi.startsWith("46006")) {
                        information.setNetworkOperatorName("中国联通_" + currentNetType);
                    }
                    //中国铁通（China Tietong）
                    else if (imsi.startsWith("46020")) {
                        information.setNetworkOperatorName("中国铁通_" + currentNetType);
                    }

                    //中国电信（China Telecom）CDMA网络
                    else if (imsi.startsWith("46003") || imsi.startsWith("46011") || imsi.startsWith("46005")) {
                        information.setNetworkOperatorName("中国电信_" + currentNetType);
                    } else {
                        information.setNetworkOperatorName("未知网络_" + currentNetType);
                    }
                    //手机品牌类型
                    StringBuffer phoneType = new StringBuffer();
                    phoneType.append(SystemUtil.getDeviceBrand() + ";");
                    phoneType.append(SystemUtil.getSystemModel() + ";");
                    phoneType.append(SystemUtil.getIMEI(context));
                    information.setPhoneType(phoneType.toString());

                    Date day = new Date();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    information.setCollTime(df.format(day));
                    SharedPreferences sp = context.getSharedPreferences("userInformation", Context.MODE_PRIVATE);
                    information.setPhoneNumber(sharedPreferences.getString("user_name", "未填写") + "_" + sharedPreferences.getString("user_phone", "未填写") + "_" + sharedPreferences.getString("user_department", "未填写"));
                    information.setIsUpload("0");
                    information.setID(UUID.randomUUID().toString());
                    setDisplay();
                    /** 保存每次采集的数据 **/
                    sqLiteOpenHelper.save(db, information, activity);
                    /** 删除数据采集表 **/
                    //db.execSQL("drop table NetWorkInfor");
                    /** 创建数据采集表 **/
                    //sqLiteOpenHelper.onCreate(db);
                    /** 删除表数据 */
                    //db.execSQL("delete from NetWorkInfor");
                } catch (Exception e) {
                    disp.append("err:" + e.toString());
                }
            } else {
                if (addressText.getText().toString().equals("")) {
                    Toast.makeText(activity, "详细地址不能未空。", Toast.LENGTH_LONG).show();
                } else if (sharedPreferences.getString("user_department", "").equals("")) {
                    Toast.makeText(activity, "请在个人界面选择所属部门", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(activity, "请在个人信息界面填写有效的手机号与姓名，否则无法采集。", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (MainActivity) this.getActivity();
        //requestPower();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        OnlyTeleMan=(TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        /**监听当前信号*/
        listenerSign = new mylistener();
        listenerNetwork=new mylistener();
        boolean isSIM = hasSimCard();
        if (isSIM) {
            try {
                OnlyTeleMan.listen(listenerSign, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
                OnlyTeleMan.listen(listenerNetwork,PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
            } catch (Exception e) {
                Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
            }
        } else {
            //collButton.setEnabled(false);
            Toast.makeText(activity, "未检测到SIM卡,或SIM卡无效", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_net_coll, container, false);


        neighborShow=(TextView)view.findViewById(R.id.neighborShow);
        addressText = (EditText) view.findViewById(R.id.address);
        neighborShow=(TextView)view.findViewById(R.id.neighborShow);
        disp = view.findViewById(R.id.show);
        CurrentBess = view.findViewById(R.id.CurrentBess);
        collButton = view.findViewById(R.id.collButton);

        CurrentGPS = view.findViewById(R.id.CurrentGPS);
        updateGPS = view.findViewById(R.id.updateGPS);

        sqLiteOpenHelper = new informDBHelper(context);
        db = sqLiteOpenHelper.getReadableDatabase();
        uploadButton = (Button) view.findViewById(R.id.upLoadButton);
        uploadButton.setEnabled(false);
        spForAddress = context.getSharedPreferences("address", Context.MODE_PRIVATE);
        addressText.setText(spForAddress.getString("lastAddress", ""));
        /** 二级联动*/
        sp = (Spinner) view.findViewById(R.id.overlayScene1C);
        sp2 = (Spinner) view.findViewById(R.id.overlayScene2C);
        sp3 = (Spinner) view.findViewById(R.id.districtC);
        adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, overlay1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(selectListener1);
        adapter2 = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, overlay2[0]);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp2.setAdapter(adapter2);
        /** */
        collButton = view.findViewById(R.id.collButton);
        collButton.setOnClickListener(new collButtonListener());
        uploadButton.setOnClickListener(new uploadButtonListener());



        locationProvider = locationManager.getBestProvider(createFineCriteria(), true);
        // 设置监听*器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
        locationManager.requestLocationUpdates(locationProvider, 1000, 10, mylocationListener);
        getGPSLocation();

        updateGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocalion();
            }
        });


        return view;
    }

    private void updateUI(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        BigDecimal bg = new BigDecimal(longitude);
        longitude = bg.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();

        bg = new BigDecimal(latitude);
        latitude = bg.setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
        CurrentGPS.setText("(" + String.valueOf(longitude) + "  ,  " + String.valueOf(latitude) + ")");
    }


    private String getGPSLocation() {
        Location location = locationManager.getLastKnownLocation(locationProvider); // 通过GPS获取位置
        if (location != null) {
            updateUI(location);
            return "(" + String.valueOf(location.getLongitude()) + "," + String.valueOf(location.getLatitude()) + ")";
        }

        return "no GPS";
    }

    LocationListener mylocationListener = new LocationListener() {
        /**
         * 位置变化
         * @param location
         */
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(context, "位置更新", Toast.LENGTH_SHORT).show();
            updateUI(location);
        }

        /**
         * 状态变化
         * @param provider
         * @param status
         * @param extras
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                //GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Toast.makeText(context, "当前GPS状态为服务区外状态", Toast.LENGTH_SHORT).show();
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Toast.makeText(context, "当前GPS状态为暂停服务状态", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        /**
         * gps开启时
         * @param provider
         */
        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(context, "GPS已开启", Toast.LENGTH_SHORT).show();
            getGPSLocation();
        }

        /**
         * gps关闭时
         * @param provider
         */
        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(context, "请打开GPS 否则无法定位", Toast.LENGTH_SHORT).show();
        }
    };

    public static Criteria createFineCriteria() {
        Criteria c = new Criteria();

        c.setAccuracy(Criteria.ACCURACY_FINE);//高精度

        c.setAltitudeRequired(true);//包含高度信息

        c.setBearingRequired(true);//包含方位信息

        c.setSpeedRequired(true);//包含速度信息

        c.setCostAllowed(false);//允许付费

        c.setPowerRequirement(Criteria.POWER_HIGH);//高耗电

        return c;

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
        this.context = context;

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
        void onFragmentInteraction(Uri uri);
    }

    /**
     * 更新地理位置
     */
    public void getLocalion() {
        String Gpsstr=getGPSLocation();
        String Gps = Gpsstr.replaceAll("\\(|\\)", "").replaceAll("（", "").replaceAll("）", "");
        String[] GpsArr = Gps.split(",");

        if(GpsArr.length>=2){
            final String latt = GpsArr[1];
            final String lonn = GpsArr[0];
            new Thread() {
                public String res = "未知地址";

                @Override
                public void run() {
                    super.run();
                    try {
                        String jj = BaiDuLoactionDeal.getRequest(lonn, latt);
                        res = BaiDuLoactionDeal.formatAddress(jj);

                    } catch (Exception e) {
                        res = "未知地址";
                    } finally {
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
        }else{
            addressText.setText("未知地址");
        }

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

    @Override
    public void onResume() {
        super.onResume();
        boolean isSIM = hasSimCard();
        if (isSIM) {
            try {
                OnlyTeleMan.listen(listenerSign, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
                OnlyTeleMan.listen(listenerNetwork,PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
            } catch (Exception e) {
                Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
            }
        } else {
            //collButton.setEnabled(false);
            Toast.makeText(activity, "未检测到SIM卡,或SIM卡无效", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        OnlyTeleMan.listen(listenerSign,PhoneStateListener.LISTEN_NONE);
        OnlyTeleMan.listen(listenerNetwork,PhoneStateListener.LISTEN_NONE);
    }

    class mylistener extends PhoneStateListener {
        SignalStrength signal=null;
        int networktype=0;

        /**
         * 监听网络类型变化
         * @param state
         * @param networkType
         */
        @Override
        public void onDataConnectionStateChanged(int state, int networkType) {
            currentNetType=getNetWorkType();
            networktype=networkType;
            Toast.makeText(activity, "检测到网络类型变化", Toast.LENGTH_LONG).show();
            if(signal!=null){
                setDbm(signal,networktype);
            }
        }

        /**
         * 监听信号强度变化
         * @param signalStrength
         */

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            Toast.makeText(activity, "检测到信号强度变化", Toast.LENGTH_LONG).show();
            signal=signalStrength;
            setDbm(signal,networktype);
        }


        private void setDbm(SignalStrength signalStrength,int networktype){
            try {
                Method method = null;
                method = signalStrength.getClass().getMethod("getDbm");
                String dbm = method.invoke(signalStrength).toString();
//                method = signalStrength.getClass().getMethod("getGsmDbm");
//                String dbm2 = method.invoke(signalStrength).toString();
//
//                method = signalStrength.getClass().getMethod("getLteDbm");
//                String dbm3 = method.invoke(signalStrength).toString();
//
//                method = signalStrength.getClass().getMethod("getTdScdmaDbm");
//                String dbm4 = method.invoke(signalStrength).toString();
//                Log.e("getDbm",dbm);
//                Log.e("getGsmDbm",dbm2);
//                Log.e("getLteDbm",dbm3);
//                Log.e("getTdScdmaDbm",dbm4);
                currentNetType = getNetWorkType();
                CurrentBess.setText(" " + currentNetType + "信号强度:" + dbm);
                currentDbmValue = dbm;
            } catch (Exception e) {
                currentDbmValue = "-1";
                CurrentBess.setText("-1");
            }
        }
    }


    /**
     * 查看当前是否存在SIM卡
     *
     * @param telMgr
     * @return
     */
    public boolean hasSimCard() {
        int simState = OnlyTeleMan.getSimState();
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
     *
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
    public void setDisplay() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:s");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        StringBuffer sb = new StringBuffer();
        sb.append("---------------" + str + "------------------\n");
        sb.append("TAC:" + information.getTAC() + "\n");
        sb.append("ECI:" + information.getECI() + "\n");
        sb.append("GPS:" + information.getGPS() + "\n");
        sb.append("信号强度:" + information.getBSSS() + "\n");
        sb.append("手机类型:" + information.getPhoneType() + "\n");
        sb.append("手机号码:" + information.getPhoneNumber() + "\n");
        sb.append("覆盖场景:" + information.getOverlayScene() + "\n");
        sb.append("区县:" + information.getDistrict() + "\n");
        sb.append("地址:" + information.getAddress() + "\n");
        sb.append("运营商信息:" + information.getNetworkOperatorName() + "\n");
        sb.append("是否上传:" + (information.getIsUpload().equals("0") ? "未上传" : "已上传") + "\n");
        sb.append("IMSI:" + imsi + "\n");
        sb.append("-----------------------------------------");
        disp.setText(sb.toString());
    }
    private String getNetWorkType() {
        int networkType = OnlyTeleMan.getNetworkType();
        switch (networkType) {

            // 2G网络
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            case TelephonyManager.NETWORK_TYPE_GSM:

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
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return "3G";
            // 4G网络
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "未知网络";
            default:
                return "未知网络";
        }
    }





    public String mccnc2oper(String ccnc){

        if("46000".equals(ccnc)||"46007".equals(ccnc)||"46002".equals(ccnc)){
            return "中国移动";
        }else if("46001".equals(ccnc)||"46006".equals(ccnc)){
            return "中国联通";
        }else if("46020".equals(ccnc)){
            return "中国铁通";
        }else if("46003".equals(ccnc)||"46011".equals(ccnc)||"46005".equals(ccnc)){
            return "中国电信";
        }else{
            return ccnc;
        }
    }


}
