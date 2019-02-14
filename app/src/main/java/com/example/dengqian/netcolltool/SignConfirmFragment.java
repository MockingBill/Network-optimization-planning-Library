package com.example.dengqian.netcolltool;


import android.Manifest;
import android.app.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dengqian.netcolltool.bean.AesAndToken;
import com.example.dengqian.netcolltool.bean.MyApplication;
import com.example.dengqian.netcolltool.bean.connNetReq;
import com.example.dengqian.netcolltool.bean.informDBHelperForWeakConfirm;
import com.example.dengqian.netcolltool.bean.simpleWeakDemandArrayAdapter;
import com.example.dengqian.netcolltool.bean.weakCoverageDemand;
import com.example.dengqian.netcolltool.bean.weakInformation;
import com.example.dengqian.netcolltool.widget.CustomDatePicker;

import net.lemonsoft.lemonbubble.LemonBubble;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


public class SignConfirmFragment extends ListFragment {
    //本页面列表数据
    private ArrayList<HashMap<String,String>> list=null;
    private SimpleAdapter listAdapter=null;
    private NetCollFragment.OnFragmentInteractionListener mListener;
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
    private TextView currentDate2;
    private CustomDatePicker customDatePicker1;
    private CustomDatePicker customDatePicker2;
    private Button weak_query_button;
    private Button weak_save_button;
    private EditText collTime;
    private Spinner distract;
    private EditText address;
    private LocationManager locationManager;
    private String locationProvider;
    private HashMap map=null;

    private int currentPosition=0;


    private MyApplication application=null;
    private List<weakInformation> weakList=new ArrayList<weakInformation>();
    //弹窗view对象，用于获取弹窗对象
    private View contentView;
    //弹出窗对象
    private PopupWindow mPopWindow;
    private weakInformation inf;


    /**
     * window_weak_confirm_layout界面组件
     */

    private TextView netcoll_confirm_district;
    private TextView netcoll_confirm_overlayScene;
    private TextView netcoll_confirm_address;
    private TextView netcoll_confirm_weak_eci;
    private TextView netcoll_confirm_weak_tac;
    private TextView netcoll_confirm_weak_networkType;
    private TextView netcoll_confirm_weak_bsss;
    private EditText netcoll_confirm_eci;
    private EditText netcoll_confirm_tac;
    private EditText netcoll_confirm_networktype;
    private EditText netcoll_confirm_bsss;
    private  Button confirm_last_step_button;
    private  Button confirm_refresh_button;
    private  Button confirm_fault_button;
    private  Button confirm_demand_button;


    private informDBHelperForWeakConfirm dbforweak;
    private SQLiteDatabase db;

    public SignConfirmFragment() {
        // Required empty public constructor
    }





    public static NetCollFragment newInstance(String param1, String param2) {
        NetCollFragment fragment = new NetCollFragment();
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

            dbforweak=new informDBHelperForWeakConfirm(context);
            db=dbforweak.getReadableDatabase();


            selectDate = (LinearLayout)  view.findViewById(R.id.selectDate);
            currentDate = (TextView) view.findViewById(R.id.currentDate);
            currentDate2=(TextView) view.findViewById(R.id.currentDate2);



            //设置当前GPS的值
            setGpsView(getLocation(context));
            locationProvider=LocationManager.GPS_PROVIDER;
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                locationManager.requestLocationUpdates(locationProvider, 3000, 100, locationListener);
            }
            initDatePicker();
            currentDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDatePicker1.show(currentDate.getText().toString());
                }
            });
            currentDate2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDatePicker2.show(currentDate.getText().toString());
                }
            });



            list=new ArrayList<HashMap<String,String>>();
            for(int i =0;i<=0;i++){
                map=new HashMap<String,String>();
                map.put("row_weak_address","详细地址");
                map.put("row_weak_city","地市");
                map.put("row_weak_bsss","信号强度");
                map.put("row_weak_collect_time","采集时间");
                map.put("row_weak_network_type","网络制式");
                map.put("row_weak_dis","距离");
                list.add(map);
            }


            listAdapter=new SimpleAdapter(activity,list,R.layout.list_view_row_weak_confirm,
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

            weak_save_button=(Button)view.findViewById(R.id.weak_save);
            weak_save_button.setOnClickListener(new SaveListener());

            application=(MyApplication) activity.getApplication();
            if(application.getGlobalWeakList()!=null&&application.getGlobalWeakList().size()!=0){
                refreshList(application.getGlobalWeakList());
            }


            return view;


    }


    public void refreshList(List<weakInformation> infoList){
        list.clear();
        DecimalFormat df = new DecimalFormat("#.00");
        for(weakInformation info : infoList){
            map=new HashMap<String,String>();
            map.put("row_weak_address",info.getAddress());
            map.put("row_weak_city",info.getDistrict());
            map.put("row_weak_bsss",info.getBSSS());
            map.put("row_weak_collect_time",info.getCollTime());
            map.put("row_weak_network_type",info.getNetWorkType());
            double dis=Double.valueOf(info.getDis());
            if(dis>=1000){
                map.put("row_weak_dis",df.format(dis/1000)+"公里");
            }else{
                map.put("row_weak_dis",dis+"米");
            }

            list.add(map);
        }
        if(weakList==null||weakList.size()==0){
            weakList=application.getGlobalWeakList();
        }
        listAdapter.notifyDataSetChanged();
    }
    public class SaveListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            MyApplication application = (MyApplication)activity.getApplication();
            application.setGlobalWeakList(weakList);
            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(weakList!=null&&weakList.size()!=0){
            currentPosition=position;
            showPopupWindow(position);
        }
    }




    public void showPopupWindow(int position){
        contentView = LayoutInflater.from(activity).inflate(R.layout.window_weak_query_layout, null);
        mPopWindow = new PopupWindow(contentView,
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);
        inf = weakList.get(position);
        ((TextView)contentView.findViewById(R.id.weak_win_address)).setText(inf.getAddress());
        ((TextView)contentView.findViewById(R.id.weak_win_bsss)).setText(String.valueOf(inf.getBSSS()));
        ((TextView)contentView.findViewById(R.id.weak_win_city)).setText(inf.getCity());
        ((TextView)contentView.findViewById(R.id.weak_win_collectName)).setText(inf.getCollectUsername());
        ((TextView)contentView.findViewById(R.id.weak_win_collectTime)).setText(inf.getCollTime());
        ((TextView)contentView.findViewById(R.id.weak_win_departmentName)).setText(inf.getFromDepartment());
        ((TextView)contentView.findViewById(R.id.weak_win_dirct)).setText(inf.getDistrict());
        ((TextView)contentView.findViewById(R.id.weak_win_ECI)).setText(inf.getECI());
        ((TextView)contentView.findViewById(R.id.weak_win_lat)).setText(String.valueOf(inf.getGpsLat()));
        ((TextView)contentView.findViewById(R.id.weak_win_lon)).setText(String.valueOf(inf.getGpsLon()));
        ((TextView)contentView.findViewById(R.id.weak_win_networkType)).setText(inf.getNetWorkType());
        ((TextView)contentView.findViewById(R.id.weak_win_overlayScene)).setText(inf.getOverlayScene());
        ((TextView)contentView.findViewById(R.id.weak_win_OperatorName)).setText(inf.getNetworkOperatorName());
        ((TextView)contentView.findViewById(R.id.weak_win_phoneNumber)).setText(inf.getPhoneNumber());
        ((TextView)contentView.findViewById(R.id.weak_win_phonetype)).setText(inf.getPhoneType());
        ((TextView)contentView.findViewById(R.id.weak_win_TAC)).setText(inf.getTAC());



        Button buttonReturn=(Button)contentView.findViewById(R.id.weak_return_button);
        buttonReturn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
            }
        });
        Button buttonConfirm=(Button)contentView.findViewById(R.id.weak_confirm_button);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<weakCoverageDemand> hasConfirm=dbforweak.query(db,"select * from bu_weak_coverage_demand",null);
                boolean flag=true;
                for (weakCoverageDemand wx:hasConfirm){
                        if(wx.getWeakCollID().equals(inf.getID()))
                        {
                            flag=false;
                            Toast.makeText(activity, "该条记录已被核查，请在核查历史中删除后再核查。", Toast.LENGTH_LONG).show();
                        }
                }
                if(flag){
                    mPopWindow.dismiss();
                    showConfirmWindow(inf);
                }
            }
        });


        View rootview = LayoutInflater.from(activity).inflate(R.layout.window_weak_query_layout, null);
        mPopWindow.showAtLocation(rootview, Gravity.TOP, 0, 20);






    }




    //弹窗view对象，用于获取弹窗对象
    private View contentView2;
    //弹出窗对象
    private PopupWindow mPopWindow2;

    private LinearLayout dynamic_content;

    private LinearLayout faultview;
    private LinearLayout demand;
    private TextView hint_info;
    private weakInformation currentWeakInf;
    private EditText weak_fault_remark;
    private EditText weak_demand_remark;
    private EditText weak_demand_personTel;
    private EditText weak_demand_personCharge;
    private EditText weak_demand_reqCellNum;
    private EditText weak_demand_stAddress;
    private EditText weak_demand_preStName;
    private Spinner sp1;
    private Spinner sp2;
    private Spinner sp3;
    private Spinner sp4;



    private Button confirm_relation_demand_button;
    private LinearLayout conform_demand_list;
    private ListView demand_list;
    private SimpleAdapter listAdapter2;
    private List<weakCoverageDemand>  demandList;


    public void refreshDemandList(){

        demandList =dbforweak.query(db,"select * from bu_weak_coverage_demand where preStName !='null' and preStName IS NOT NULL;",null);
        List<Map<String,String>> infoList=new ArrayList<>();


        for(weakCoverageDemand demandinfo:demandList){
            HashMap<String,String> map=new HashMap<>();
            map.put("Stname",demandinfo.getPreStName());
            map.put("Staddress",demandinfo.getStAddress());
            map.put("netmodel",demandinfo.getNetModel());
            map.put("prope",demandinfo.getStPrope());
            map.put("buildtype",demandinfo.getBuildType());
            map.put("cellnum",demandinfo.getReqCellNum());
            infoList.add(map);
        }


        listAdapter2=new SimpleAdapter(activity,infoList,R.layout.list_view_demand,
                new String[]{"Stname","Staddress","netmodel","prope","buildtype","cellnum"},
                new int[]{
                        R.id.row_demand_Stname,
                        R.id.row_demand_Staddress,
                        R.id.row_demand_netmodel,
                        R.id.row_demand_prope,
                        R.id.row_demand_buildtype,
                        R.id.row_demand_cellnum
                });
        demand_list.setAdapter(listAdapter2);
        demand_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPopWindow2.dismiss();
                showDemandWin(demandList.get(position));
            }
        });
        listAdapter2.notifyDataSetChanged();
    }


    public void showConfirmWindow(final weakInformation inf){

        /**
         * 数据库组件初始化
         */
        currentWeakInf=new weakInformation(inf);


        /**
         * 弹窗界面View
         */
        contentView2 = LayoutInflater.from(activity).inflate(R.layout.window_weak_confirm_layout, null);
        mPopWindow2 = new PopupWindow(contentView2,FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);


        /**
         * 动态界面填写区域初始化
         */
        faultview=(LinearLayout) contentView2.findViewById(R.id.weak_fault_info);
        demand=(LinearLayout)contentView2.findViewById(R.id.weak_demand_info);
        conform_demand_list=(LinearLayout)contentView2.findViewById(R.id.conform_demand_list);

        /**
         * 动态界面组件初始化(预建站点名、地址、负责人...)
         */

        weak_fault_remark=(EditText)contentView2.findViewById(R.id.weak_fault_remark);


        weak_fault_remark.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
        weak_fault_remark.setLongClickable(true);


        weak_demand_remark=(EditText)contentView2.findViewById(R.id.weak_demand_remark);
        weak_demand_personTel=(EditText)contentView2.findViewById(R.id.weak_demand_personTel);
        weak_demand_personCharge=(EditText)contentView2.findViewById(R.id.weak_demand_personCharge);
        weak_demand_reqCellNum=(EditText)contentView2.findViewById(R.id.weak_demand_reqCellNum);
        weak_demand_stAddress=(EditText)contentView2.findViewById(R.id.weak_demand_stAddress);
        weak_demand_preStName=(EditText)contentView2.findViewById(R.id.weak_demand_preStName);


        /**
         * view被创建时候隐藏故障信息与需求信息填写区域
         */

        faultview.setVisibility(View.GONE);
        demand.setVisibility(View.GONE);
        conform_demand_list.setVisibility(View.GONE);

        /**
         * 动态界面下拉框初始化
         */

        sp1=((Spinner)contentView2.findViewById(R.id.weak_demand_baseSationPro));
        sp2=((Spinner)contentView2.findViewById(R.id.weak_demand_buildType));
        sp3=((Spinner)contentView2.findViewById(R.id.weak_demand_ispass));
        sp4=((Spinner)contentView2.findViewById(R.id.weak_demand_networkType));
        sp1.setAdapter(new simpleWeakDemandArrayAdapter<String>(context,android.R.layout.simple_spinner_item,getArrayToList(R.array.disBaseStationProperty)));
        sp2.setAdapter(new simpleWeakDemandArrayAdapter<String>(context,android.R.layout.simple_spinner_item,getArrayToList(R.array.disBuildType)));
        sp3.setAdapter(new simpleWeakDemandArrayAdapter<String>(context,android.R.layout.simple_spinner_item,getArrayToList(R.array.disIsPass)));
        sp4.setAdapter(new simpleWeakDemandArrayAdapter<String>(context,android.R.layout.simple_spinner_item,getArrayToList(R.array.disNetWorkType)));

        sp1.setSelection(getArrayToList(R.array.disBaseStationProperty).size()-1,true);
        sp2.setSelection(getArrayToList(R.array.disBuildType).size()-1,true);
        sp3.setSelection(getArrayToList(R.array.disIsPass).size()-1,true);
        sp4.setSelection(getArrayToList(R.array.disNetWorkType).size()-1,true);


        /**
         * window_weak_confirm_layout组件初始化
         */
        netcoll_confirm_district=(TextView) contentView2.findViewById(R.id.netcoll_confirm_district);
        netcoll_confirm_overlayScene=(TextView)contentView2.findViewById(R.id.netcoll_confirm_overlayScene);
        netcoll_confirm_address=(TextView)contentView2.findViewById(R.id.netcoll_confirm_address);
        netcoll_confirm_weak_eci=(TextView)contentView2.findViewById(R.id.netcoll_confirm_weak_eci);
        netcoll_confirm_weak_tac=(TextView)contentView2.findViewById(R.id.netcoll_confirm_weak_tac);
        netcoll_confirm_weak_networkType=(TextView)contentView2.findViewById(R.id.netcoll_confirm_weak_networkType);
        netcoll_confirm_weak_bsss=(TextView)contentView2.findViewById(R.id.netcoll_confirm_weak_bsss);
        netcoll_confirm_eci=(EditText) contentView2.findViewById(R.id.netcoll_confirm_eci);
        netcoll_confirm_tac=(EditText)contentView2.findViewById(R.id.netcoll_confirm_tac);
        netcoll_confirm_networktype=(EditText)contentView2.findViewById(R.id.netcoll_confirm_networktype);
        netcoll_confirm_bsss=(EditText)contentView2.findViewById(R.id.netcoll_confirm_bsss);

        confirm_relation_demand_button=(Button)contentView2.findViewById(R.id.confirm_relation_demand_button);
        confirm_last_step_button=(Button)contentView2.findViewById(R.id.confirm_last_step_button);
        confirm_refresh_button=(Button)contentView2.findViewById(R.id.confirm_refresh_button);
        confirm_fault_button=(Button)contentView2.findViewById(R.id.confirm_fault_button);
        confirm_demand_button=(Button)contentView2.findViewById(R.id.confirm_demand_button);
        dynamic_content=(LinearLayout)contentView2.findViewById(R.id.dynamic_content);
        hint_info=(TextView)contentView2.findViewById(R.id.hint_info);


        /**
         * 故障网优备注信息按钮组初始化
         */

        Button weak_fault_last_step= contentView2.findViewById(R.id.weak_fault_last_step);
        Button weak_fault_save=contentView2.findViewById(R.id.weak_fault_save);

        /**
         * 建站需求按钮初始化
         */
        Button weak_demand_last_step=contentView2.findViewById(R.id.weak_demand_last_step);
        Button weak_demand_save=contentView2.findViewById(R.id.weak_demand_save);


        /**
         * 获取数据库信息
         */
        demandList =dbforweak.query(db,"select * from bu_weak_coverage_demand where preStName !='null' and preStName IS NOT NULL;",null);
        /**
         * 列表信息初始化
         */

        demand_list=(ListView) contentView2.findViewById(R.id.demand_list);
        List<Map<String,String>> infoList=new ArrayList<>();
        for(weakCoverageDemand demandinfo:demandList){
            HashMap<String,String> map=new HashMap<>();
            map.put("Stname",demandinfo.getPreStName());
            map.put("Staddress",demandinfo.getStAddress());
            map.put("netmodel",demandinfo.getNetModel());
            map.put("prope",demandinfo.getStPrope());
            map.put("buildtype",demandinfo.getBuildType());
            map.put("cellnum",demandinfo.getReqCellNum());
            infoList.add(map);
        }
        listAdapter2=new SimpleAdapter(activity,infoList,R.layout.list_view_demand,
                new String[]{"Stname","Staddress","netmodel","prope","buildtype","cellnum"},
                new int[]{
                        R.id.row_demand_Stname,
                        R.id.row_demand_Staddress,
                        R.id.row_demand_netmodel,
                        R.id.row_demand_prope,
                        R.id.row_demand_buildtype,
                        R.id.row_demand_cellnum
                        });
        demand_list.setAdapter(listAdapter2);
        demand_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPopWindow2.dismiss();
                showDemandWin(demandList.get(position));
            }
        });

        /**
         * 建站需求保存
         */
        weak_demand_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weakCoverageDemand wd=getDemandData(2);
                if(wd==null){
                    Toast.makeText(activity, "请检查数据完整性。", Toast.LENGTH_LONG).show();
                }else{
                    String checkWeakisconfirm="select * from bu_weak_coverage_demand where weakCollID='"+wd.getWeakCollID()+"';";
                    List<weakCoverageDemand> kk=dbforweak.query(db,checkWeakisconfirm,null);

                    if(kk.size()==0){
                        boolean flag=dbforweak.save(db,wd,context,activity);
                        if(flag){
                            Toast.makeText(activity, "保存成功。", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(activity, "保存失败。", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(activity, "该弱覆盖记录已经被核查确认。", Toast.LENGTH_LONG).show();
                    }

                }

            }
        });
        /**
         * 故障信息保存
         */
        weak_fault_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weakCoverageDemand wd=getDemandData(1);
                if(wd==null){
                    Toast.makeText(activity, "请检查数据完整性与正确性。", Toast.LENGTH_LONG).show();
                }else{
                    String checkWeakisconfirm="select * from bu_weak_coverage_demand where weakCollID='"+wd.getWeakCollID()+"';";
                    List<weakCoverageDemand> kk=dbforweak.query(db,checkWeakisconfirm,null);

                    if(kk.size()==0){
                        boolean flag=dbforweak.save(db,wd,context,activity);
                        if(flag){
                            Toast.makeText(activity, "保存成功。", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(activity, "保存失败。", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(activity, "该弱覆盖记录已经被核查确认。", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });



        /**
         * 在故障填写信息中进行上一步
         */
        weak_fault_last_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faultview.setVisibility(View.GONE);
                ((LinearLayout)contentView2.findViewById(R.id.weak_confirm_button_array1)).setVisibility(View.VISIBLE);
                ((LinearLayout)contentView2.findViewById(R.id.weak_confirm_button_array2)).setVisibility(View.VISIBLE);
            }

        });


        /**
         * 在需求填写信息中进行上一步
         */

        weak_demand_last_step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demand.setVisibility(View.GONE);
                ((LinearLayout)contentView2.findViewById(R.id.weak_confirm_button_array1)).setVisibility(View.VISIBLE);
                ((LinearLayout)contentView2.findViewById(R.id.weak_confirm_button_array2)).setVisibility(View.VISIBLE);
            }
        });

        /**
         * 上一步
         */
        confirm_last_step_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow2.dismiss();
                showPopupWindow(currentPosition);

            }
        });


        /**
         * 刷新按钮
         */
        confirm_refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refershWin();
            }
        });

        /**
         * 填写故障网优备注信息
         */
        confirm_fault_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                faultview.setVisibility(View.VISIBLE);
                ((LinearLayout)contentView2.findViewById(R.id.weak_confirm_button_array1)).setVisibility(View.GONE);
                ((LinearLayout)contentView2.findViewById(R.id.weak_confirm_button_array2)).setVisibility(View.GONE);
            }
        });

        /**
         * 填写基站建设需求信息
         */

        confirm_demand_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demand.setVisibility(View.VISIBLE);
                ((LinearLayout)contentView2.findViewById(R.id.weak_confirm_button_array1)).setVisibility(View.GONE);
                ((LinearLayout)contentView2.findViewById(R.id.weak_confirm_button_array2)).setVisibility(View.GONE);
            }
        });

        /**
         * 关联到已有的弱覆盖按钮
         */
        confirm_relation_demand_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conform_demand_list.setVisibility(View.VISIBLE);
                ((LinearLayout)contentView2.findViewById(R.id.weak_confirm_button_array1)).setVisibility(View.GONE);
                ((LinearLayout)contentView2.findViewById(R.id.weak_confirm_button_array2)).setVisibility(View.GONE);
                refreshDemandList();
            }
        });


        /**监听当前信号*/
        TelephonyManager tmm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        boolean isSIM=hasSimCard(tmm);
        if(isSIM ){
            SignConfirmFragment.mylistener listener=new SignConfirmFragment.mylistener();
            tmm.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }else{
            Toast.makeText(activity, "未检测到SIM卡,或SIM卡无效", Toast.LENGTH_LONG).show();
        }
        /**
         * 设置待确认弱覆盖的具体值
         */
        netcoll_confirm_district.setText(inf.getDistrict());
        netcoll_confirm_overlayScene.setText(inf.getOverlayScene());
        netcoll_confirm_address.setText(inf.getAddress());
        netcoll_confirm_weak_eci.setText(inf.getECI());
        netcoll_confirm_weak_tac.setText(inf.getTAC());
        netcoll_confirm_weak_networkType.setText(inf.getNetWorkType());
        netcoll_confirm_weak_bsss.setText(String.valueOf(inf.getBSSS()));

        refershWin();



        View rootview = LayoutInflater.from(activity).inflate(R.layout.window_weak_confirm_layout, null);
        mPopWindow2.showAtLocation(rootview, Gravity.TOP, 0, 20);
    }


    private View contentView3;
    private PopupWindow mPopWindow3;
    private weakCoverageDemand currentWcdemandInfo;
    public void showDemandWin(weakCoverageDemand wcdemandInfo){
        currentWcdemandInfo=wcdemandInfo;
        contentView3 = LayoutInflater.from(activity).inflate(R.layout.window_demand_layout, null);
        mPopWindow3 = new PopupWindow(contentView3,FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);

        ((TextView)contentView3.findViewById(R.id.win_demand_buildtype)).setText(wcdemandInfo.getBuildType());
        ((TextView)contentView3.findViewById(R.id.win_demand_cellnum)).setText(wcdemandInfo.getReqCellNum());
        ((TextView)contentView3.findViewById(R.id.win_demand_charge)).setText(wcdemandInfo.getPersonCharge());
        ((TextView)contentView3.findViewById(R.id.win_demand_netmodel)).setText(wcdemandInfo.getNetModel());
        ((TextView)contentView3.findViewById(R.id.win_demand_phoneTel)).setText(wcdemandInfo.getPersonTel());
        ((TextView)contentView3.findViewById(R.id.win_demand_staddress)).setText(wcdemandInfo.getStAddress());
        ((TextView)contentView3.findViewById(R.id.win_demand_stname)).setText(wcdemandInfo.getPreStName());
        ((TextView)contentView3.findViewById(R.id.win_demand_stprop)).setText(wcdemandInfo.getStPrope());


        Button win_demand_relation=(Button)contentView3.findViewById(R.id.win_demand_relation);
        Button win_demand_return=(Button)contentView3.findViewById(R.id.win_demand_return);

        /**
         * 查看需求详情后返回
         */
        win_demand_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow3.dismiss();
                showConfirmWindow(currentWeakInf);
            }
        });

        /**
         * 确认关联
         */
        win_demand_relation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weakCoverageDemand wd=relationDemandData(currentWcdemandInfo);
                if(wd==null){
                    Toast.makeText(activity, "数据不完整。", Toast.LENGTH_LONG).show();
                }else{
                    String checkWeakisconfirm="select * from bu_weak_coverage_demand where weakCollID='"+wd.getWeakCollID()+"';";
                    List<weakCoverageDemand> kk=dbforweak.query(db,checkWeakisconfirm,null);

                    if(kk.size()==0){
                        wd.setDemandID(UUID.randomUUID().toString());
                        boolean flag=dbforweak.save(db,wd,context,activity);
                        if(flag){
                            Toast.makeText(activity, "保存成功。", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(activity, "保存失败。", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(activity, "该弱覆盖记录已经被核查确认。", Toast.LENGTH_LONG).show();
                    }



                }
            }
        });


        View rootview = LayoutInflater.from(activity).inflate(R.layout.window_demand_layout, null);
        mPopWindow3.showAtLocation(rootview, Gravity.TOP, 0, 300);
    }


    private List<String> getArrayToList(int id){
        Resources res=getResources();
        List<String> list= new ArrayList<>();
        String[] ArrayList=res.getStringArray(id);
        for(String x:ArrayList){
            list.add(x);
        }

        return list;
    }


    private void refershWin(){
        String Eci="-1";
        String Tac="-1";
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            TelephonyManager mTelephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            if(tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA){
                CdmaCellLocation cdmacelllocation = (CdmaCellLocation)tm.getCellLocation();
                Tac=String.valueOf(cdmacelllocation.getNetworkId());
                Eci=String.valueOf(cdmacelllocation.getBaseStationId());
            }else{
                GsmCellLocation gsmCellLocation = (GsmCellLocation) mTelephonyManager.getCellLocation();
                Tac=String.valueOf(gsmCellLocation.getLac());
                Eci=String.valueOf(gsmCellLocation.getCid());
            }
        }
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        TelephonyManager mTelephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        currentNetType=getNetWorkType(mTelephonyManager);


        netcoll_confirm_eci.setText(Eci);
        netcoll_confirm_tac.setText(Tac);
        netcoll_confirm_bsss.setText(currentDbmValue);
        boolean f1=currentWeakInf.getBSSS()>=-110;
        boolean f2=Integer.valueOf(currentDbmValue)>=-110;
        if(f1&&f2){
            hint_info.setText("当前信号强度与待确认记录信号强度均不属于弱覆盖。");
        }else if(f1&&!f2){
            hint_info.setText("待确认记录信号强度不属于弱覆盖，当前信号强度属于弱覆盖。");
        }else if(!f1&&f2){
            hint_info.setText("待确认记录信号强度属于弱覆盖，当前信号强度不属于弱覆盖。请刷新或移动手机位置。");
        }else if(!f1&&!f2){
            hint_info.setText("当前信号强度与待确认记录信号强度均属于弱覆盖。");
        }



    }




private String currentNetType="无网络";
private String currentDbmValue="-130";
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
                    netcoll_confirm_bsss.setText(String.valueOf(-130));
                    currentDbmValue="-130";
                    netcoll_confirm_networktype.setText(currentNetType);
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
                    netcoll_confirm_bsss.setText(dbm);
                    currentDbmValue=dbm;
                    netcoll_confirm_networktype.setText(currentNetType);
                }
            }catch (Exception e){
                currentNetType="无网络";
                currentDbmValue="-1";
                netcoll_confirm_bsss.setText("-1");
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
     * 获取当前网络类型
     * @param mTelephonyManager
     * @return
     */


    private String getNetWorkType(TelephonyManager mTelephonyManager){
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
    /**
     * 弱覆盖查询按钮点击事件
     */
    public class QueryListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {



            LemonBubble.showRoundProgress(context, "等待中...");

            new Thread(){

                String warmText="刷新成功";
                @Override
                public void run() {
                    try{
                        String []gps=getLocation(context);
                        String res = connNetReq.post(getString(R.string.getWeakForConfirm),
                                connNetReq.weakQueryToJson(currentDate.getText().toString(),currentDate2.getText().toString(),
                                        address.getText().toString(), distract.getSelectedItem().toString(),gps[0],gps[1]));
                        res= AesAndToken.decrypt(res,AesAndToken.KEY);
                        if("0".equals(res)){
                            warmText="网络异常";
                        }else{
                            weakList=connNetReq.jsonToWeakInf(res);
                            if(weakList.size()==0){
                                warmText="未找到满足的数据";
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        warmText="网络错误";
                    }finally {
                        //上传后的UI操作放在UI线程中
                        Looper.prepare();
                        new Handler(context.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                refreshList(weakList);
                                LemonBubble.showRight(context, "成功啦！", 1000);
                                Toast.makeText(context, warmText, Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        });
                    }



                }
            }.start();

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
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        c.add(Calendar.MONTH, -1);
        Date m = c.getTime();
        String day30pre = sdf.format(m);

        String now = sdf.format(new Date());

        currentDate.setText(day30pre.split(" ")[0]);
        currentDate2.setText(now.split(" ")[0]);

        customDatePicker1 = new CustomDatePicker(activity, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                currentDate.setText(time.split(" ")[0]);
            }
        }, "2010-01-01 00:00", now); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker2 = new CustomDatePicker(activity, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                currentDate2.setText(time.split(" ")[0]);
            }
        }, "2010-01-01 00:00", now);


        customDatePicker1.showSpecificTime(false); // 不显示时和分
        customDatePicker1.setIsLoop(false); // 不允许循环滚动

        customDatePicker2.showSpecificTime(false); // 不显示时和分
        customDatePicker2.showSpecificTime(false); // 不显示时和分

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
            String []lonlat={formatDecimalWithZero(String.valueOf(location.getLongitude()),6),formatDecimalWithZero(String.valueOf(location.getLatitude()),6)};
            return lonlat;


        } else {
            return new String[]{"-1","-1"};
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        if (context instanceof NetCollFragment.OnFragmentInteractionListener) {
            mListener = (NetCollFragment.OnFragmentInteractionListener) context;

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

    public  weakCoverageDemand relationDemandData(weakCoverageDemand reled){
        weakCoverageDemand wd=new weakCoverageDemand();
        /**
         * 确认后采集参数
         */
        wd.setWeakCollID(currentWeakInf.getID());
        wd.setDemandID(reled.getDemandID());
        wd.setConfirm_tac(netcoll_confirm_tac.getText().toString());
        wd.setConfirm_eci(netcoll_confirm_eci.getText().toString());
        wd.setConfirm_networktype(netcoll_confirm_networktype.getText().toString());
        wd.setConfirm_bsss(currentDbmValue);

        String []gps=getLocation(context);
        wd.setConfirm_lon(gps[0]);
        wd.setConfirm_lat(gps[1]);

        wd.setWeakAddress(currentWeakInf.getAddress());

        wd.setRemark(reled.getRemark());
        wd.setPersonTel(reled.getPersonTel());
        wd.setPersonCharge(reled.getPersonCharge());
        wd.setReqCellNum(reled.getReqCellNum());
        wd.setStAddress(reled.getStAddress());
        wd.setPreStName(reled.getPreStName());
        wd.setStPrope(reled.getStPrope());
        wd.setBuildType(reled.getBuildType());
        wd.setIsPass(reled.getIsPass());
        wd.setNetModel(reled.getNetModel());

        return wd;
    }


    public weakCoverageDemand getDemandData(int getType){
        weakCoverageDemand wd=new weakCoverageDemand();

        /**
         * 故障网优-数据收集
         */
        if(getType==1){
            String fault_remark=weak_fault_remark.getText().toString();
            if("".equals(fault_remark)){
                return null;
            }
            else{
                /**
                 * 通过sp获取当前使用者的信息
                 */
                SharedPreferences sp=context.getSharedPreferences ("userInformation",Context.MODE_PRIVATE);



                wd.setWeakCollID(currentWeakInf.getID());
                wd.setWeakAddress(currentWeakInf.getAddress());
                wd.setRemark(fault_remark);

                wd.setPersonCharge(sp.getString("user_name","未知上传者"));
                wd.setPersonTel(sp.getString("user_phone","未知手机号"));

                wd.setConfirm_tac(netcoll_confirm_tac.getText().toString());
                wd.setConfirm_eci(netcoll_confirm_eci.getText().toString());
                wd.setConfirm_networktype(netcoll_confirm_networktype.getText().toString());
                wd.setConfirm_bsss(currentDbmValue);
                String []gps=getLocation(context);
                wd.setConfirm_lon(gps[0]);
                wd.setConfirm_lat(gps[1]);
                return wd;
            }
        }

        /**
         * 建站需求数据收集
         */
        else if(getType==2){

            /**
             * 需求信息
             */
            String demand_remark=weak_demand_remark.getText().toString();
            String demand_personTel=weak_demand_personTel.getText().toString();
            String demand_personCharge=weak_demand_personCharge.getText().toString();
            String demand_reqCellNum=weak_demand_reqCellNum.getText().toString();
            String demand_stAddress=weak_demand_stAddress.getText().toString();
            String demand_preStName=weak_demand_preStName.getText().toString();
            String stPrope=sp1.getSelectedItem().toString();
            String buildType=sp2.getSelectedItem().toString();
            String isPass=sp3.getSelectedItem().toString();
            String netModel=sp4.getSelectedItem().toString();
            /**
             * 确认后采集参数
             */
            wd.setWeakCollID(currentWeakInf.getID());
            wd.setWeakAddress(currentWeakInf.getAddress());
            wd.setDemandID(UUID.randomUUID().toString());
            wd.setConfirm_tac(netcoll_confirm_tac.getText().toString());
            wd.setConfirm_eci(netcoll_confirm_eci.getText().toString());
            wd.setConfirm_networktype(netcoll_confirm_networktype.getText().toString());
            wd.setConfirm_bsss(currentDbmValue);

            String []gps=getLocation(context);
            wd.setConfirm_lon(gps[0]);
            wd.setConfirm_lat(gps[1]);


            wd.setRemark(demand_remark);
            wd.setPersonTel(demand_personTel);
            wd.setPersonCharge(demand_personCharge);
            wd.setReqCellNum(demand_reqCellNum);
            wd.setStAddress(demand_stAddress);
            wd.setPreStName(demand_preStName);
            wd.setStPrope(stPrope);
            wd.setBuildType(buildType);
            wd.setIsPass(isPass);
            wd.setNetModel(netModel);
            if(wd.checkData()){
                return wd;
            }else{
                return null;
            }


        }else{
            return null;
        }
    }


}
