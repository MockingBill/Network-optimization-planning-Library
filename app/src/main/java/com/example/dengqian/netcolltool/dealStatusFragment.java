package com.example.dengqian.netcolltool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dengqian.netcolltool.bean.AesAndToken;
import com.example.dengqian.netcolltool.bean.connNetReq;
import com.example.dengqian.netcolltool.bean.information;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class dealStatusFragment extends ListFragment {
    //主体activity
    private Activity activity;
    //当前Fragment的view。使用它获取布局组件
    private View view;
    //当前上下文
    private Context context;
    //当前fragment监听
    private OnFragmentInteractionListener mListener;
    //listView适配器
    private SimpleAdapter listAdapter;
    //listView显示的数据结构
    private ArrayList<HashMap<String,String>> list;
    //小型持久化数据组件
    private SharedPreferences shp;
    //弹出框view，用于获取弹出框组件。
    private View contentView;
    //当前采集信息对象
    private  information inf;
    //当前弹出框对象
    private PopupWindow mPopWindow;
    //请求信息返回值
    private String resu;
    //由请求信息返回值所转换的采集信息对象线性表
    private List<information> listInfo;
    //数据加载提示
    private  String toastText="未知错误";
    //构造方法
    public dealStatusFragment() {

    }


    public static dealStatusFragment newInstance(String param1, String param2) {
        dealStatusFragment fragment = new dealStatusFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //listView列表项点击事件
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        showPopupWindow(position);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //fragment生命周期创建view
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =inflater.inflate(R.layout.fragment_deal_status, container, false);
         shp=context.getSharedPreferences ("userInformation",Context.MODE_PRIVATE);
        final String  phoneNumber=shp.getString("user_phone","");
        new Thread() {
            public void run() {


                try {
                    //发送一个请求包含手机号信息在远程服务器返回该手机号下的状态变更信息
                    resu = connNetReq.post(getString(R.string.getStatusData), "{\"phoneNumber\":\""+phoneNumber+"\"}");
                    resu=AesAndToken.decrypt(resu,AesAndToken.KEY);
                } catch (Exception e) {
                    Log.e("err",e.toString());
                    toastText="远程服务器连接失败";
                }
                new Handler(context.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        list = new ArrayList<HashMap<String, String>>();
                        listInfo=connNetReq.statusJsonToList(resu);

                        HashMap<String, String> map1 = new HashMap<String, String>();
                        if(listInfo!=null&&!listInfo.isEmpty()) {
                            for (information info : listInfo) {
                                map1 = new HashMap<String, String>();
                                String time = info.getCollTime();

                                if (!time.equals("")&&time.split(" ").length==2) {
                                    time = time.split(" ")[1];
                                    time = time.split(":")[0] + ":" + time.split(":")[1];
                                }
                                map1.put("upAddress",info.getAddress());
                                map1.put("upBSSS", String.valueOf(info.getBSSS()));
                                map1.put("upTime", time);
                                map1.put("upStatus", info.getSolveStatus());
                                list.add(map1);
                            }
                            toastText="数据加载成功";
                            listAdapter = new SimpleAdapter(activity, list, R.layout.list_view_row_deal,
                                    new String[]{"upAddress", "upBSSS",  "upTime","upStatus"},
                                    new int[]{R.id.upAddress, R.id.upBSSS,R.id.upTime, R.id.upStatus});
                            setListAdapter(listAdapter);
                            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
                        }else{
                            if(!toastText.equals("远程服务器连接失败"))
                                toastText="该手机号下没有状态已被变更的记录";
                            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();

                        }



                    }
                });
            }

        }.start();



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
        activity=(MainActivity) this.getActivity();
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
     * 显示弹出窗口
     * @param position
     */
    private void showPopupWindow(int position) {
        //设置contentView
        contentView = LayoutInflater.from(activity).inflate(R.layout.window_deal_status_layout, null);
        mPopWindow = new PopupWindow(contentView,
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, true);
        inf = listInfo.get(position);
        //获取弹窗框组件
        TextView t_address = (TextView) contentView.findViewById(R.id.ValueAddress);
        TextView t_bsss = (TextView) contentView.findViewById(R.id.ValueBSSS);
        TextView t_collTime = (TextView) contentView.findViewById(R.id.ValueCollTime);
        TextView t_district = (TextView) contentView.findViewById(R.id.ValueDistrict);
        TextView t_ECI = (TextView) contentView.findViewById(R.id.ValueECI);
        TextView t_GPS = (TextView) contentView.findViewById(R.id.ValueGPS);
        TextView t_NetworkOperatorName = (TextView) contentView.findViewById(R.id.ValueNetworkOperatorName);
        TextView t_overlayScene = (TextView) contentView.findViewById(R.id.ValueOverlayScene);
        TextView t_phoneNumber = (TextView) contentView.findViewById(R.id.ValuePhoneNumber);
        TextView t_Tac = (TextView) contentView.findViewById(R.id.ValueTAC);
        TextView t_solveStatus=(TextView) contentView.findViewById(R.id.ValueSolveStatus);
        TextView t_solveTime=(TextView) contentView.findViewById(R.id.ValueSolveTime);
        //设置弹出框组件值
        t_address.setText(inf.getAddress());
        t_bsss.setText(String.valueOf(inf.getBSSS()));
        t_collTime.setText(inf.getCollTime());
        t_district.setText(inf.getDistrict());
        t_ECI.setText(inf.getECI());
        t_GPS.setText(inf.getGPS());
        t_NetworkOperatorName.setText(inf.getNetworkOperatorName());
        t_overlayScene.setText(inf.getOverlayScene());
        t_phoneNumber.setText(inf.getPhoneNumber());
        t_Tac.setText(inf.getTAC());
        t_solveStatus.setText(inf.getSolveStatus());
        t_solveTime.setText(inf.getSolveTime());
        //显示PopupWindow
        View rootview = LayoutInflater.from(activity).inflate(R.layout.fragment_history, null);
        mPopWindow.showAtLocation(rootview, Gravity.TOP, 0, 30);

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
}
