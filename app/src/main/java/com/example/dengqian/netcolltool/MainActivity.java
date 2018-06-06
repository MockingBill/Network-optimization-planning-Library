package com.example.dengqian.netcolltool;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;




//实现所有的Fragment的监听接口
public class MainActivity extends AppCompatActivity implements historyFragment.OnFragmentInteractionListener,netCollFragment.OnFragmentInteractionListener,personFragment.OnFragmentInteractionListener,dealStatusFragment.OnFragmentInteractionListener   {


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager FM = getSupportFragmentManager();

            FragmentTransaction MfragmentTransaction =FM.beginTransaction();

            /**
             * 创建三个子Fragment用于不同按钮之间切换
             */
            netCollFragment f1 = new netCollFragment();
            historyFragment f2=new historyFragment();
            personFragment f3=new personFragment();
            dealStatusFragment f4=new dealStatusFragment();


            switch (item.getItemId()) {
                case R.id.navigation_home:{
                    //向容器内加入Fragment，一般使用add或者replace方法实现，需要传入容器的id和Fragment的实例。
                    MfragmentTransaction.replace(R.id.container,f1);
                    //提交事务，调用commit方法提交。
                    MfragmentTransaction.commit();
                    return true;
                }

                case R.id.navigation_dashboard:{
                    MfragmentTransaction.replace(R.id.container,f2);
                    MfragmentTransaction.commit();
                    return true;
                }


                case R.id.navigation_notifications:
                {
                    MfragmentTransaction.replace(R.id.container,f3);
                    MfragmentTransaction.commit();
                    return true;
                }
                case R.id.deal_status:
                {
                    MfragmentTransaction.replace(R.id.container,f4);
                    MfragmentTransaction.commit();
                    return true;
                }
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * 默认显示第一个网络测试模块
         */
        netCollFragment f1 = new netCollFragment();
        FragmentManager FM = getSupportFragmentManager();
        FragmentTransaction MfragmentTransaction =FM.beginTransaction();
        MfragmentTransaction.replace(R.id.container,f1);
        MfragmentTransaction.commit();

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
