package com.m.cenarius.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import com.m.cenarius.Cenarius;
import com.m.cenarius.R;
import com.m.cenarius.resourceproxy.cache.AssetCache;
import com.m.cenarius.resourceproxy.cache.CacheHelper;
import com.m.cenarius.view.CenariusWidget;
import com.m.cenarius.widget.AlertDialogWidget;
import com.m.cenarius.widget.CordovaWidget;
import com.m.cenarius.widget.NativeWidget;
import com.m.cenarius.widget.TitleWidget;
import com.m.cenarius.widget.ToastWidget;
import com.m.cenarius.widget.WebWidget;
import com.m.cenarius.widget.menu.MenuItem;
import com.m.cenarius.widget.menu.MenuWidget;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * `CNRSViewActivity` 是一个 Cenarius Container。
 * 它提供了一个使用 web 技术 html, css, javascript 开发 UI 界面的容器。
 * 其他Activity都应该继承它
 */
public class CNRSViewActivity extends AppCompatActivity {

    public static final String TAG = CNRSViewActivity.class.getSimpleName();

    /**
     * 打开本地web应用
     *
     * @param uri        相对路径
     * @param parameters 参数
     */
    public void openWebPage(String uri, HashMap parameters) {
        Intent intent = new Intent(this, CNRSWebViewActivity.class);
        intent.putExtra("uri", uri);
        intent.putExtra("parameters", parameters);
        startActivity(intent);
    }

    /**
     * 打开轻应用
     *
     * @param htmlFileURL 网址
     * @param parameters  参数
     */
    public void openLightApp(String htmlFileURL, HashMap parameters) {
        Intent intent = new Intent(this, CNRSWebViewActivity.class);
        intent.putExtra("htmlFileURL", htmlFileURL);
        intent.putExtra("parameters", parameters);
        startActivity(intent);
    }

    /**
     * 打开原生页面
     *
     * @param className  类名
     * @param parameters 参数
     */
    public void openNativePage(String className, HashMap parameters) {
        String c = getPackageName();
//        try {
//            Object test = Class.forName(className).getInterfaces();
//            if (test instanceof CNRSViewActivity) {
//            }
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//
//        try {
//            CNRSViewActivity cnrsViewActivity = (CNRSViewActivity) Class.forName(className).newInstance();
//            if (cnrsViewActivity != null) {
//                Intent intent = new Intent(this, CNRSViewActivity.class);
//                intent.putExtra("parameters", parameters);
//                startActivity(intent);
//            }
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 打开Cordova页面
     *
     * @param uri        相对路径
     * @param parameters 参数
     */
    public void openCordovaPage(String uri, HashMap parameters) {
        Intent intent = new Intent(this, CNRSCordovaActivity.class);
        intent.putExtra("uri", uri);
        intent.putExtra("parameters", parameters);
        startActivity(intent);
    }

//    /**
//     * 登录
//     *
//     * @param username 用户名
//     * @param password 密码
//     * @param callback 登录后将执行这个 callback
//     */
//    public static void login(Context context, String username, String password, LoginWidget.LoginCallback callback) {
//        LoginWidget.login(context, username, password, callback);
//    }


    /**
     * 对应的 uri。
     */
    public String uri;

    /**
     * 对应的 url。
     */
    public String htmlFileURL;

    /**
     * 传参
     */
    public HashMap cnrsDictionary;

    public ArrayList<CenariusWidget> widgets = new ArrayList<>();

    private List<MenuItem> mMenuItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cnrs_view_activity);

        uri = getIntent().getStringExtra("uri");
        htmlFileURL = getIntent().getStringExtra("htmlFileURL");

        // Widgets
        TitleWidget titleWidget = new TitleWidget();
        AlertDialogWidget alertDialogWidget = new AlertDialogWidget();
        ToastWidget toastWidget = new ToastWidget();
//        PullToRefreshWidget pullToRefreshWidget = new PullToRefreshWidget();
        MenuWidget menuWidget = new MenuWidget();
        NativeWidget nativeWidget = new NativeWidget();
        WebWidget webWidget = new WebWidget();
        CordovaWidget cordovaWidget = new CordovaWidget();

        widgets.add(titleWidget);
        widgets.add(alertDialogWidget);
        widgets.add(toastWidget);
//        widgets.add(pullToRefreshWidget);
        widgets.add(menuWidget);
        widgets.add(nativeWidget);
        widgets.add(webWidget);
        widgets.add(cordovaWidget);
    }

    public String htmlURL() {
        return cnrs_htmlURL(uri, htmlFileURL);
    }

    /**
     * 添加自定义的 widget
     */
    public void addCenariusWidget(CenariusWidget widget) {
        if (null != widget) {
            widgets.add(widget);
        }
    }

    private String cnrs_htmlURL(String uri, String htmlFileURL) {
        if (htmlFileURL == null) {
            //读取sd目录
            if (Cenarius.DevelopModeEnable)
            {
                return getSDFile(uri);
            }
            else {
                htmlFileURL = CacheHelper.getInstance().localHtmlURLForURI(uri);
                if (htmlFileURL == null) {
                    htmlFileURL = CacheHelper.getInstance().remoteHtmlURLForURI(uri);
                }
            }
        }
        return htmlFileURL;
    }

    // 获取sdcard目录
    private String getSDFile(String uri) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCardDir = Environment.getExternalStorageDirectory();//获取SDCard目录
            String applicationName = getApplicationName();
            File fileDir = new File(sdCardDir, applicationName + "/" + AssetCache.getInstance().mFilePath + "/" + uri);
            String url = "file://" + fileDir.getPath();
            return url;
        }

        return null;
    }

    private String getApplicationName() {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo;
        try {
            packageManager = getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName =
                (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

}