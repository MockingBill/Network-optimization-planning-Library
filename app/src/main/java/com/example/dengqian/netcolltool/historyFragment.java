package com.example.dengqian.netcolltool;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.dengqian.netcolltool.bean.connNetReq;
import com.example.dengqian.netcolltool.bean.informDBHelper;
import com.example.dengqian.netcolltool.bean.information;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link historyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link historyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class historyFragment extends ListFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;



    /**
     * 声明区
     */
    private Activity activity;
    private View view;
    //地市下拉框适配器
    private ArrayAdapter<String> adapter ;
    //场景下拉框适配器
    private ArrayAdapter<String> adapter2;
    //地市下拉框
    private  Spinner sp;
    //一级场景下拉框
    private  Spinner sp2;
    //二级场景下拉框
    private  Spinner sp3;
    //上传状态下拉框
    private  Spinner sp4;

    private  Context context;
    //一级场景
    private String[] overlay1 = new String[] {"全部","城区", "乡镇","农村","交通"};
    //二级场景
    private String[][] overlay2 = new String[][]{
            {"全部"},
            {"全部","学校","商业区","景区","党政军","住宅","医院","酒店"},
            {"全部","住宅区","景区","党政军"},
            {"全部","行政村","村寨","景区"},
            {"全部","高速","车站","高铁","公路","机场"}};
    //用于显示于listView列表的数据结构
    private ArrayList<HashMap<String,String>> list;
    //当前对象列表用于显示于listView
    private List<information> infoList;
    //列表适配器
    private  SimpleAdapter listAdapter;
    //弹出窗对象
    private PopupWindow mPopWindow;
    //本地数据库操作辅助对象
    private informDBHelper sqLiteOpenHelper;
    //数据库实体对象
    private SQLiteDatabase db;
    //上传按钮
    private  Button buttonUpload;
    //弹窗view对象，用于获取弹窗对象
    private View contentView;
    //用于显示弹窗信息的information
    private  information inf;
    //批量上传按钮
    private Button allUploadButton;
    //弱覆盖详细地址
    private EditText his_address_query;
    //查询按钮
    private Button his_botton_address;
    public historyFragment() {
        // Required empty public constructor
    }

    /**
     * 二级联动一级场景监听器，当一级场景改变时则改变二级场景待选内容
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

    /**分类筛选动态响应，选择后直接从数据库查值显示于列表中*/
    private AdapterView.OnItemSelectedListener selectListener2 = new AdapterView.OnItemSelectedListener(){
        public void onItemSelected(AdapterView parent, View v, int position,long id){
            //刷新
            refreshList();
        }
        public void onNothingSelected(AdapterView arg0){

        }
    };


    /**
     * 点击列表项弹出弹框
     * @param listView
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        showPopupWindow(position);
    }


    /**
     * 弹窗方法，包含按钮的定义和监听器的设置等
     * @param position
     */
    private void showPopupWindow(int position) {
        //设置contentView
        contentView = LayoutInflater.from(activity).inflate(R.layout.window_layout, null);
        mPopWindow = new PopupWindow(contentView,
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);
        inf = infoList.get(position);
        //获取当前点击的对象
        TextView t_address = (TextView) contentView.findViewById(R.id.ValueAddress);
        TextView t_bsss = (TextView) contentView.findViewById(R.id.ValueBSSS);
        TextView t_collTime = (TextView) contentView.findViewById(R.id.ValueCollTime);
        TextView t_district = (TextView) contentView.findViewById(R.id.ValueDistrict);
        TextView t_ECI = (TextView) contentView.findViewById(R.id.ValueECI);
        TextView t_GPS = (TextView) contentView.findViewById(R.id.ValueGPS);
        TextView t_isUpload = (TextView) contentView.findViewById(R.id.ValueIsUpload);
        TextView t_NetworkOperatorName = (TextView) contentView.findViewById(R.id.ValueNetworkOperatorName);
        TextView t_overlayScene = (TextView) contentView.findViewById(R.id.ValueOverlayScene);
        TextView t_phoneNumber = (TextView) contentView.findViewById(R.id.ValuePhoneNumber);
        TextView t_Tac = (TextView) contentView.findViewById(R.id.ValueTAC);
        //将对象值设置到弹框页面
        t_address.setText(inf.getAddress());
        t_bsss.setText(String.valueOf(inf.getBSSS()));
        t_collTime.setText(inf.getCollTime());
        t_district.setText(inf.getDistrict());
        t_ECI.setText(inf.getECI());
        t_GPS.setText(inf.getGPS());
        t_isUpload.setText(inf.getIsUpload());
        t_NetworkOperatorName.setText(inf.getNetworkOperatorName());
        t_overlayScene.setText(inf.getOverlayScene());
        t_phoneNumber.setText(inf.getPhoneNumber());
        t_Tac.setText(inf.getTAC());
        //上传按钮的初始化和设置点击监听
        buttonUpload = (Button) contentView.findViewById(R.id.singleUpload);
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            String alertText = "上传异常";
            String res = "0";

            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        if (inf.checkData() && 1 == Integer.valueOf(inf.getIsUpload())) {
                            alertText = "该记录已经上传，请勿重复上传。";
                        } else {

                            List<information> listAllinfo=new ArrayList<>();
                            boolean is2G=false;
                            information infoForSave=new information();
                            if(inf.getNetworkOperatorName().indexOf("2G")!=-1){
                                is2G=true;
                                information info1=new information(inf);
                                info1.setID(UUID.randomUUID().toString());
                                info1.setNetworkOperatorName(info1.getNetworkOperatorName().replace("2G","4G"));
                                info1.setBSSS(Integer.valueOf(getString(R.string.BSSS4GMin)));
                                listAllinfo.add(info1);
                                infoForSave=new information(info1);
                            }
                            else if(inf.getNetworkOperatorName().indexOf("3G")!=-1){
                                is2G=true;
                                information info2=new information(inf);
                                info2.setID(UUID.randomUUID().toString());
                                info2.setNetworkOperatorName(info2.getNetworkOperatorName().replace("3G","4G"));
                                info2.setBSSS(Integer.valueOf(getString(R.string.BSSS4GMin)));
                                listAllinfo.add(info2);
                                infoForSave=new information(info2);
                            }
                            listAllinfo.add(inf);

                            try {
                                res = connNetReq.post(getString(R.string.allObjUpload), connNetReq.beanToJson(listAllinfo));
                                //res = connNetReq.post(getString(R.string.singleObjUpload), connNetReq.beanToJson(inf));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            if (Integer.valueOf(res) == 1) {
                                if(is2G){
                                    alertText = getString(R.string.is2G4Gwarm);
                                }else{
                                    alertText = "上传成功";
                                }
                                sqLiteOpenHelper.updateIsUpload(db, inf.getID(), context);
                                inf.setIsUpload("1");

                                if(infoForSave.checkData()){
                                    sqLiteOpenHelper.save(db,infoForSave,context,activity);
                                    sqLiteOpenHelper.updateIsUpload(db, infoForSave.getID(), context);
                                }


                            } else
                                alertText = "上传失败";

                            Looper.prepare();
                            //上传后的UI操作放在UI线程中
                            new Handler(context.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if (Integer.valueOf(res) == 1) {
                                        buttonUpload.setEnabled(false);
                                    }
                                    Toast.makeText(context, alertText, Toast.LENGTH_SHORT).show();
                                    refreshList();
                                    mPopWindow.dismiss();
                                    Looper.loop();

                                }
                            });
                        }
                    }
                }.start();
            }
        });







        //删除按钮的初始化和点击监听器实现
        Button buttonDelete=(Button)contentView.findViewById(R.id.singleDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sqLiteOpenHelper.delete(db,inf.getID(),context);
                //Toast.makeText(activity,"删除成功", Toast.LENGTH_LONG).show();
                refreshList();
                mPopWindow.dismiss();
            }
        });


        //显示PopupWindow
        View rootview = LayoutInflater.from(activity).inflate(R.layout.fragment_history, null);
        mPopWindow.showAtLocation(rootview, Gravity.TOP, 0, 30);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=(MainActivity) this.getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        //相关layout组件的初始化
        view=inflater.inflate(R.layout.fragment_history, container, false);
        list=new ArrayList<HashMap<String,String>>();
        sqLiteOpenHelper=new informDBHelper(context);
        db=sqLiteOpenHelper.getReadableDatabase();
        allUploadButton=view.findViewById(R.id.allUploadButton);
        sp = (Spinner) view.findViewById(R.id.overlayScene1);
        sp2=(Spinner)view.findViewById(R.id.overlayScene2);
        sp3=(Spinner)view.findViewById(R.id.district);

        sp4=(Spinner)view.findViewById(R.id.isUpload);





        adapter = new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_item,overlay1 );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(selectListener1);


        adapter2=new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_item, overlay2[0]);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp2.setAdapter(adapter2);
        sp2.setOnItemSelectedListener(selectListener2);
        sp3.setOnItemSelectedListener(selectListener2);
        sp4.setOnItemSelectedListener(selectListener2);

        his_botton_address=(Button) view.findViewById(R.id.his_botton_address);
        his_address_query=(EditText) view.findViewById(R.id.his_address_query);


        //查询所有记录显示于列表（这里查询出的并不会真正的显示在列表中，在下拉列表中定义的项中被加载后会覆盖本次查询记录）
        infoList=sqLiteOpenHelper.query(db,"select * from NetWorkInfor",null);
        HashMap<String,String> map;
        for(information info : infoList){
            map=new HashMap<String,String>();
            map.put("row_address",info.getAddress());
            map.put("CollTime",info.getCollTime());
            list.add(map);
        }


        //适配器的设置
        listAdapter=new SimpleAdapter(activity,list,R.layout.list_view_row_,
                new String[]{"row_address","CollTime"},
                new int[]{R.id.row_address,R.id.CollTime});
        setListAdapter(listAdapter);


        //批量上传按钮的设置
        allUploadButton.setOnClickListener(new View.OnClickListener() {
            String res = "0";
            ArrayList<String> uploadListID = new ArrayList<String>();
            List<information> listAll;
            @Override
            public void onClick(View v) {
                listAll = sqLiteOpenHelper.query(db, "select * from NetWorkInfor where isUpload=0", null);
                if (listAll.isEmpty())
                    Toast.makeText(activity, "没有数据需要上传", Toast.LENGTH_LONG).show();
                else {
                    new Thread() {
                        boolean is2G=false;
                        List<information> listForSave=new ArrayList<>();
                        public void run() {

                            try {
                                List<information> listAllInf=new ArrayList<>();

                                for (information x : listAll){
                                    uploadListID.add(x.getID());
                                }
                                for(information y:listAll){
                                    /**
                                     * 筛选出当前的2G、3G信号，用于生成对应的4G标记。
                                     */
                                    listAllInf.add(y);
                                    if(y.getNetworkOperatorName().indexOf("2G")!=-1){
                                        is2G=true;
                                        information info1=new information(y);
                                        info1.setID(UUID.randomUUID().toString());
                                        info1.setNetworkOperatorName(info1.getNetworkOperatorName().replace("2G","4G"));
                                        info1.setBSSS(Integer.valueOf(getString(R.string.BSSS4GMin)));
                                        listAllInf.add(info1);
                                        listForSave.add(info1);
                                    }
                                    else if(y.getNetworkOperatorName().indexOf("3G")!=-1){
                                        is2G=true;
                                        information info2=new information(y);
                                        info2.setID(UUID.randomUUID().toString());
                                        info2.setNetworkOperatorName(info2.getNetworkOperatorName().replace("3G","4G"));
                                        info2.setBSSS(Integer.valueOf(getString(R.string.BSSS4GMin)));
                                        listAllInf.add(info2);
                                        listForSave.add(info2);
                                    }
                                }

                                res = connNetReq.post(getString(R.string.allObjUpload), connNetReq.beanToJson(listAllInf));
                            } catch (Exception e) {
                                res = "0";
                                Log.e("upload",e.toString());
                            }
                            Looper.prepare();
                            new Handler(context.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    /**
                                     * 这里写成功后的组件控制
                                     */
                                    if (res.equals("1")) {
                                        sqLiteOpenHelper.updateListIsUpload(db, uploadListID, context);
                                        String uploadText="批量上传成功";
                                        //将上传的对应4G数据保存到本地
                                        for (information j : listForSave){
                                            sqLiteOpenHelper.save(db,j,context,activity);
                                            sqLiteOpenHelper.updateIsUpload(db,j.getID(),context);
                                        }
                                        if(is2G){
                                            uploadText=getString(R.string.is2G4Gwarm);
                                        }else{
                                            uploadText="批量上传成功";
                                        }
                                        Toast.makeText(activity,uploadText, Toast.LENGTH_LONG).show();
                                        refreshList();
                                        Looper.loop();
                                    } else {
                                        Toast.makeText(activity, "批量上传失败", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }.start();
                }

            }
        });
        refreshList();

        his_botton_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshListForAddressquery(his_address_query.getText().toString());
            }
        });
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
        this.context=context;
        super.onAttach(context);
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


    /**
     * 根据当前选择刷新列表。用于上传后。删除后。对列表数据进行更新。
     */
    public void refreshList(){
        String a1=sp.getSelectedItem().toString().equals("全部")?"%":sp.getSelectedItem().toString();
        String a2=sp2.getSelectedItem().toString().equals("全部")?"%":sp2.getSelectedItem().toString();

        String sc=a1+"_"+a2;

        String di=sp3.getSelectedItem().toString();
        String up=sp4.getSelectedItem().toString().equals("未上传")?"0":"1";
        if(di.equals("全部"))
            di="%";
        String querySql="select * from  NetWorkInfor where overlayScene like '"+sc+"' and district like '"+di+"' and isUpload="+up+"";
        String []arr={sc,di,up};
        infoList=sqLiteOpenHelper.query(db,querySql,null);
        HashMap<String,String> map;
        list.clear();
        for(information info : infoList){
            map=new HashMap<String,String>();
            map.put("row_address",info.getAddress());
            /*map.put("ECI",info.getECI());
            map.put("BSSS",String.valueOf(info.getBSSS()));*/
            map.put("CollTime",info.getCollTime());
            list.add(map);
        }
        listAdapter.notifyDataSetChanged();
    }

    public static historyFragment newInstance(String param1, String param2) {
        historyFragment fragment = new historyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void refreshListForAddressquery(String addressvalue){
        String up=sp4.getSelectedItem().toString().equals("未上传")?"0":"1";
        if("".equals(addressvalue)){
            addressvalue="%";
        }

        String querySql="select * from  NetWorkInfor where address like '%"+addressvalue+"%' and isUpload="+up+"";

        infoList=sqLiteOpenHelper.query(db,querySql,null);
        HashMap<String,String> map;
        list.clear();
        for(information info : infoList){
            map=new HashMap<String,String>();
            map.put("row_address",info.getAddress());
            /*map.put("ECI",info.getECI());
            map.put("BSSS",String.valueOf(info.getBSSS()));*/
            map.put("CollTime",info.getCollTime());
            list.add(map);
        }
        listAdapter.notifyDataSetChanged();
    }



}
