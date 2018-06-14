package com.example.dengqian.netcolltool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dengqian.netcolltool.bean.AesAndToken;
import com.example.dengqian.netcolltool.bean.connNetReq;
import com.example.dengqian.netcolltool.bean.versionInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


//实现所有的Fragment的监听接口
public class MainActivity extends AppCompatActivity implements historyFragment.OnFragmentInteractionListener,netCollFragment.OnFragmentInteractionListener,personFragment.OnFragmentInteractionListener,dealStatusFragment.OnFragmentInteractionListener   {
    ProgressDialog progressDialog=null;
    Context context=null;

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
        context=MainActivity.this;




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
        /**
        * 检查版本更新
        */
        getCheckVersion();

    }

    private void getCheckVersion(){
        new Thread() {
            String warmText="版本已是最新";
            versionInfo version=new versionInfo();
            boolean isRequire=false;
            public void run() {
                String res = "";
                try {
                    String phoneNumber = "13508522561";
                    res = connNetReq.post(getString(R.string.getCheckVersion), "{\"phoneNumber\":\"" + phoneNumber + "\"}");
                    res= AesAndToken.decrypt(res,AesAndToken.KEY);
                    version=connNetReq.jsonToVersionInfo(res);
                    if(version.getAppName().equals("")){
                        warmText="版本更新检查错误";
                    }else{
                        double currentVersion=Double.valueOf(getString(R.string.currentVersion));
                        double serverVersion=Double.valueOf(version.getServerVersion());
                        if(serverVersion>currentVersion){
                            isRequire=true;
                        }else{
                            isRequire=false;
                        }
                    }
                }catch(Exception e){
                    Log.e("errMain",e.toString());
                }

                try{
                        new Handler(MainActivity.this.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if(isRequire){
                                    //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    //    设置Title的图标
                                    //builder.setIcon(R.drawable.ic_launcher);
                                    //    设置Title的内容
                                    builder.setTitle("版本更新检查");
                                    //    设置Content来显示一个信息
                                    builder.setMessage(version.getUpgradeinfo());
                                    //    设置一个PositiveButton
                                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            apk_path=version.getUpdateUrl();
                                            showDownloadDialog();
                                            //Toast.makeText(MainActivity.this, "positive: " + which, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    //    设置一个NegativeButton
                                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });

                                    //    显示出该对话框
                                    builder.show();
                                }else{
                                    Toast.makeText(MainActivity.this, warmText, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }catch(Exception e){
                        Log.e("errHander",e.toString());
                   }
            }
        }.start();
    }


    /**
     * 显示下载进度对话框
     */
    public void showDownloadDialog() {

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("正在下载...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        new downloadAsyncTask().execute();
    }

    // 外存sdcard存放路径
    private static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/" + "AutoUpdate" + "/";
    // 下载应用存放全路径
    private static final String FILE_NAME = FILE_PATH + "wyghk.apk";
    // 准备安装新版本应用标记
    private static final int INSTALL_TOKEN = 1;
    //Log日志打印标签
    private static final String TAG = "Update_log";
    private String apk_path = "http://117.187.6.14:5528/wyghk.apk";

    /**
     * 下载新版本应用
     */
    private class downloadAsyncTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            Log.e(TAG, "执行至--onPreExecute");
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {

            Log.e(TAG, "执行至--doInBackground");

            URL url;
            HttpURLConnection connection = null;
            InputStream in = null;
            FileOutputStream out = null;
            try {
                url = new URL(apk_path);
                connection = (HttpURLConnection) url.openConnection();

                in = connection.getInputStream();
                long fileLength = connection.getContentLength();
                File file_path = new File(FILE_PATH);
                if (!file_path.exists()) {
                    file_path.mkdir();
                }

                out = new FileOutputStream(new File(FILE_NAME));//为指定的文件路径创建文件输出流
                byte[] buffer = new byte[1024 * 1024];
                int len = 0;
                long readLength = 0;

                while ((len = in.read(buffer)) != -1) {

                    out.write(buffer, 0, len);//从buffer的第0位开始读取len长度的字节到输出流
                    readLength += len;

                    int curProgress = (int) (((float) readLength / fileLength) * 100);

                    publishProgress(curProgress);

                    if (readLength >= fileLength) {

                        break;
                    }
                }

                out.flush();
                return INSTALL_TOKEN;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {

            progressDialog.dismiss();//关闭进度条
            //安装应用
            installApp();
        }
    }

    /**
     * 安装新版本应用
     */
    private void installApp() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }




        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //if(Build.VERSION.SDK_INT>=24)

        if(Build.VERSION.SDK_INT>=24){ //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, "com.example.dengqian.netcolltool.fileprovider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }else{
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
        }
        context.startActivity(intent);

    }
}