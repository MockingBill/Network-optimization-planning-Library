package com.example.dengqian.netcolltool;

import android.app.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class confirmHistoryFragment extends Fragment {
    //主体activity
    private Activity activity;
    //当前Fragment的view。使用它获取布局组件
    private View view;
    //当前上下文
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=(MainActivity) this.getActivity();
    }

    //fragment生命周期创建view
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_confirm_history, container, false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }



}
