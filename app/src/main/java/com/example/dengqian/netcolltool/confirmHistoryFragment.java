package com.example.dengqian.netcolltool;

import android.app.Activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dengqian.netcolltool.bean.AesAndToken;
import com.example.dengqian.netcolltool.bean.connNetReq;
import com.example.dengqian.netcolltool.bean.informDBHelperForWeakConfirm;
import com.example.dengqian.netcolltool.bean.weakCoverageDemand;
import com.example.dengqian.netcolltool.bean.weakInformation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class confirmHistoryFragment extends Fragment {
    //主体activity
    private Activity activity;
    //当前Fragment的view。使用它获取布局组件
    private View view;
    //当前上下文
    private Context context;

    private ArrayList<HashMap<String, String>> list = null;
    private SimpleAdapter listAdapter = null;
    private ListView confirm_history_list;
    private List<weakCoverageDemand> demand_list;
    private informDBHelperForWeakConfirm weakconfirmDBhelp;
    private SQLiteDatabase db;
    private EditText confirm_his_weakAddress;
    private EditText confirm_his_preAddress;
    private Button confirm_his_query_button;
    private Button confirm_his_allupload_button;
    private Spinner confirm_his_isUpload;
    private String weakAddressParam;
    private String weakpreAddressParam;
    private String isup;


    /**
     * 弹窗界面组件
     */

    private View contentView;
    private PopupWindow mPopWindow;

    private TextView win_demand_stname;
    private TextView win_demand_staddress;
    private TextView win_demand_netmodel;
    private TextView win_demand_stprop;
    private TextView win_demand_buildtype;
    private TextView win_demand_cellnum;
    private TextView win_demand_charge;
    private TextView win_demand_phoneTel;
    private TextView win_demand_weak_address;
    private TextView win_demand_weak_eci;
    private TextView win_demand_weak_tac;
    private TextView win_demand_weak_bsss;
    private TextView win_demand_weak_networktype;
    private TextView win_demand_weak_lon;
    private TextView win_demand_weak_lat;
    private TextView win_demand_remark;
    private Button win_demand_upload;
    private Button win_demand_return;
    private Button win_demand_delete;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) this.getActivity();
    }

    //fragment生命周期创建view
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_confirm_history, container, false);
        confirm_his_weakAddress=(EditText) view.findViewById(R.id.confirm_his_weakAddress);
        confirm_his_preAddress=(EditText) view.findViewById(R.id.confirm_his_preAddress);
        confirm_his_query_button=(Button) view.findViewById(R.id.confirm_his_query_button);
        confirm_his_allupload_button=(Button) view.findViewById(R.id.confirm_his_allupload_button);
        confirm_his_isUpload=(Spinner)view.findViewById(R.id.confirm_his_isUpload);
        confirm_his_allupload_button=(Button)view.findViewById(R.id.confirm_his_allupload_button);



        weakAddressParam=confirm_his_weakAddress.getText().toString();
        weakpreAddressParam=confirm_his_preAddress.getText().toString();
        isup=confirm_his_isUpload.getSelectedItem().toString().equals("未上传")?"0":"1";


        confirm_his_allupload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    String res="";
                    List<weakCoverageDemand> list4=new ArrayList<>();
                    @Override
                    public void run() {
                        for(weakCoverageDemand x:demand_list){
                            list4.add(x);
                        }
                        try{
                            res = connNetReq.post(getString(R.string.getAllDemand), connNetReq.beanToJsonDeamdn(list4));
                            res= AesAndToken.decrypt(res,AesAndToken.KEY);
                            Log.e("上传结果",res);
                            mapResult=new HashMap<String, ArrayList<String>>();
                            mapResult=connNetReq.jsonToMap(res);
                            if(mapResult!=null){
                                succNum=0;
                                for(String x:mapResult.get("succ")){
                                    Boolean flag=weakconfirmDBhelp.updateWeakStatus(db,x,context);
                                    if(flag){
                                        succNum++;
                                    }
                                }
                            }
                        }catch(Exception e){
                            Log.e("",e.toString());
                        }finally {
                            //上传后的UI操作放在UI线程中
                            Looper.prepare();
                            new Handler(context.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if(mapResult!=null)
                                        Toast.makeText(context, "上传成功"+mapResult.get("succ").size()+"条\n上传失败"+mapResult.get("fail").size()+"条\n"+"冲突记录"+mapResult.get("has").size()+"条", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(context, "上传异常", Toast.LENGTH_SHORT).show();
                                    if(succNum!=mapResult.get("succ").size()){
                                        Toast.makeText(context, "状态变更失败", Toast.LENGTH_SHORT).show();
                                    }
                                    /**
                                     * 更新列表
                                     */
                                    weakAddressParam=confirm_his_weakAddress.getText().toString();
                                    weakpreAddressParam=confirm_his_preAddress.getText().toString();
                                    isup=confirm_his_isUpload.getSelectedItem().toString().equals("未上传")?"0":"1";
                                    demand_list=weakconfirmDBhelp.query(db,"select * from bu_weak_coverage_demand where isUpload='"+isup+"' and weakAddress like '%"+weakAddressParam+"%' and stAddress like '%"+weakpreAddressParam+"%';",null);
                                    refreshList();
                                    Looper.loop();
                                }
                            });
                        }
                    }
                }.start();
            }
        });



        confirm_his_query_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weakAddressParam=confirm_his_weakAddress.getText().toString();
                weakpreAddressParam=confirm_his_preAddress.getText().toString();
                isup=confirm_his_isUpload.getSelectedItem().toString().equals("未上传")?"0":"1";
                demand_list=weakconfirmDBhelp.query(db,"select * from bu_weak_coverage_demand where isUpload='"+isup+"' and weakAddress like '%"+weakAddressParam+"%' and stAddress like '%"+weakpreAddressParam+"%';",null);
                refreshList();
            }
        });




        /**
         * 数据库帮助组件声明
         */
        weakconfirmDBhelp=new informDBHelperForWeakConfirm(context);
        db=weakconfirmDBhelp.getReadableDatabase();

        demand_list=weakconfirmDBhelp.query(db,"select * from bu_weak_coverage_demand where isUpload='0';",null);
        list=new ArrayList<>();
        for(weakCoverageDemand x:demand_list){
            HashMap<String,String> map=new HashMap<String,String>();
            map.put("row_confirm_address",x.getWeakAddress());
            map.put("row_confirm_preName",x.getPreStName());
            map.put("row_confirm_netmodel",x.getNetModel());
            map.put("row_confirm_stpro",x.getStPrope());
            map.put("row_confirm_buildType",x.getBuildType());
            map.put("row_confirm_celNum",x.getReqCellNum());
            list.add(map);
        }
        listAdapter = new SimpleAdapter(activity, list, R.layout.list_view_row_confirm_history,
                new String[]{
                        "row_confirm_address",
                        "row_confirm_preName",
                        "row_confirm_netmodel",
                        "row_confirm_stpro",
                        "row_confirm_buildType",
                        "row_confirm_celNum"
                },
                new int[]{
                        R.id.row_confirm_address,
                        R.id.row_confirm_preName,
                        R.id.row_confirm_netmodel,
                        R.id.row_confirm_stpro,
                        R.id.row_confirm_buildType,
                        R.id.row_confirm_celNum
                });
        confirm_history_list = (ListView) view.findViewById(R.id.confirm_history_list);
        confirm_history_list.setAdapter(listAdapter);
        confirm_history_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showWindown(demand_list.get(position));

            }
        });

        return view;
    }
    private weakCoverageDemand currentWD=null;
    private  Map<String,ArrayList<String>> mapResult;
    private int succNum=0;

    public void showWindown(weakCoverageDemand wd){
        currentWD=wd;
        contentView = LayoutInflater.from(activity).inflate(R.layout.window_history_demand_layout, null);
        mPopWindow = new PopupWindow(contentView,
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);

        win_demand_stname=(TextView)contentView.findViewById(R.id.win_demand_stname);
        win_demand_staddress=(TextView)contentView.findViewById(R.id.win_demand_staddress);
        win_demand_netmodel=(TextView)contentView.findViewById(R.id.win_demand_netmodel);
        win_demand_stprop=(TextView)contentView.findViewById(R.id.win_demand_stprop);
        win_demand_buildtype=(TextView)contentView.findViewById(R.id.win_demand_buildtype);
        win_demand_cellnum=(TextView)contentView.findViewById(R.id.win_demand_cellnum);
        win_demand_charge=(TextView)contentView.findViewById(R.id.win_demand_charge);
        win_demand_phoneTel=(TextView)contentView.findViewById(R.id.win_demand_phoneTel);
        win_demand_weak_address=(TextView)contentView.findViewById(R.id.win_demand_weak_address);
        win_demand_weak_eci=(TextView)contentView.findViewById(R.id.win_demand_weak_eci);
        win_demand_weak_tac=(TextView)contentView.findViewById(R.id.win_demand_weak_tac);
        win_demand_weak_bsss=(TextView)contentView.findViewById(R.id.win_demand_weak_bsss);
        win_demand_weak_networktype=(TextView)contentView.findViewById(R.id.win_demand_weak_networktype);
        win_demand_weak_lon=(TextView)contentView.findViewById(R.id.win_demand_weak_lon);
        win_demand_weak_lat=(TextView)contentView.findViewById(R.id.win_demand_weak_lat);
        win_demand_remark=(TextView)contentView.findViewById(R.id.win_demand_remark);
        win_demand_return=(Button) contentView.findViewById(R.id.win_demand_return);
        win_demand_upload=(Button) contentView.findViewById(R.id.win_demand_upload);
        win_demand_delete=(Button)contentView.findViewById(R.id.win_demand_delete);




        win_demand_stname.setText(wd.getPreStName());
        win_demand_staddress.setText(wd.getStAddress());
        win_demand_netmodel.setText(wd.getNetModel());
        win_demand_stprop.setText(wd.getStPrope());
        win_demand_buildtype.setText(wd.getBuildType());
        win_demand_cellnum.setText(wd.getReqCellNum());
        win_demand_charge.setText(wd.getPersonCharge());
        win_demand_phoneTel.setText(wd.getPersonTel());
        win_demand_weak_address.setText(wd.getWeakAddress());
        win_demand_weak_eci.setText(wd.getConfirm_eci());
        win_demand_weak_tac.setText(wd.getConfirm_tac());
        win_demand_weak_bsss.setText(wd.getConfirm_bsss());
        win_demand_weak_networktype.setText(wd.getConfirm_networktype());
        win_demand_weak_lon.setText(wd.getConfirm_lon());
        win_demand_weak_lat.setText(wd.getConfirm_lat());
        win_demand_remark.setText(wd.getRemark());


        /**
         * 删除一条需求记录
         */
        win_demand_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag=weakconfirmDBhelp.delete(db,currentWD.getWeakCollID(),context);
                if(flag){
                    Toast.makeText(activity, "删除成功", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(activity, "删除失败", Toast.LENGTH_LONG).show();
                }
                /**
                 * 更新列表
                 */
                weakAddressParam=confirm_his_weakAddress.getText().toString();
                weakpreAddressParam=confirm_his_preAddress.getText().toString();
                isup=confirm_his_isUpload.getSelectedItem().toString().equals("未上传")?"0":"1";
                demand_list=weakconfirmDBhelp.query(db,"select * from bu_weak_coverage_demand where isUpload='"+isup+"' and weakAddress like '%"+weakAddressParam+"%' and stAddress like '%"+weakpreAddressParam+"%';",null);
                refreshList();
                mPopWindow.dismiss();

            }
        });


        /**
         * 上传一条需求记录
         */
        win_demand_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    new Thread(){
                        String res="";
                        List<weakCoverageDemand> list4=new ArrayList<>();
                        @Override
                        public void run() {
                            list4.add(currentWD);
                            try{
                                res = connNetReq.post(getString(R.string.getAllDemand), connNetReq.beanToJsonDeamdn(list4));
                                res= AesAndToken.decrypt(res,AesAndToken.KEY);
                                Log.e("上传结果",res);
                                mapResult=new HashMap<String, ArrayList<String>>();
                                mapResult=connNetReq.jsonToMap(res);
                                if(mapResult!=null){
                                    succNum=0;
                                    for(String x:mapResult.get("succ")){
                                        Boolean flag=weakconfirmDBhelp.updateWeakStatus(db,x,context);
                                        if(flag){
                                            succNum++;
                                        }
                                    }
                                }
                            }catch(Exception e){
                                Log.e("",e.toString());
                            }finally {
                                //上传后的UI操作放在UI线程中
                                Looper.prepare();
                                new Handler(context.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(mapResult!=null)
                                            Toast.makeText(context, "上传成功"+mapResult.get("succ").size()+"条\n上传失败"+mapResult.get("fail").size()+"条\n"+"冲突记录"+mapResult.get("has").size()+"条", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(context, "上传异常", Toast.LENGTH_SHORT).show();
                                        if(succNum!=mapResult.get("succ").size()){
                                            Toast.makeText(context, "状态变更失败", Toast.LENGTH_SHORT).show();
                                        }
                                        /**
                                         * 更新列表
                                         */
                                        weakAddressParam=confirm_his_weakAddress.getText().toString();
                                        weakpreAddressParam=confirm_his_preAddress.getText().toString();
                                        isup=confirm_his_isUpload.getSelectedItem().toString().equals("未上传")?"0":"1";
                                        demand_list=weakconfirmDBhelp.query(db,"select * from bu_weak_coverage_demand where isUpload='"+isup+"' and weakAddress like '%"+weakAddressParam+"%' and stAddress like '%"+weakpreAddressParam+"%';",null);
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
        win_demand_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopWindow.dismiss();
            }
        });
        View rootview = LayoutInflater.from(activity).inflate(R.layout.window_history_demand_layout, null);
        mPopWindow.showAtLocation(rootview, Gravity.TOP, 0, 20);
    }


    public void refreshList(){
        list.clear();
        for(weakCoverageDemand x:demand_list){
            HashMap<String,String> map=new HashMap<String,String>();
            map.put("row_confirm_address",x.getWeakAddress());
            map.put("row_confirm_preName",x.getPreStName());
            map.put("row_confirm_netmodel",x.getNetModel());
            map.put("row_confirm_stpro",x.getStPrope());
            map.put("row_confirm_buildType",x.getBuildType());
            map.put("row_confirm_celNum",x.getReqCellNum());
            list.add(map);
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


}
