package com.m.cenarius.widget;

import android.app.Activity;
import android.content.DialogInterface;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.m.cenarius.view.CenariusWidget;

import org.crosswalk.engine.XWalkCordovaView;

import java.util.ArrayList;
import java.util.List;

public class AlertDialogWidget implements CenariusWidget {

    private static boolean sShowing = false;

    @Override
    public String getPath() {
        return "/widget/alert_dialog";
    }

    @Override
    public boolean handle(View view, String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        Uri uri = Uri.parse(url);
        String path = uri.getPath();
        if (TextUtils.equals(path, getPath())) {
            String data = uri.getQueryParameter("data");
            Data alertDialogData = null;
            if (!TextUtils.isEmpty(data)) {
                alertDialogData = JSON.parseObject(data, Data.class);
            }
            if (null == alertDialogData || !alertDialogData.valid()) {
                return false;
            }

            return renderDialog((Activity) view.getContext(), view, alertDialogData);
        }
        return false;
    }


    /**
     * 根据dialog数据显示dialog
     *
     * @param data
     * @return
     */
    private static boolean renderDialog(Activity activity, final View webView, Data data) {
        if (null == data) {
            return false;
        }
        // 正在显示dialog，则不再显示
        if (sShowing) {
            return false;
        }
        if (activity.isFinishing()) {
            return false;
        }
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity)
                .setTitle(data.title)
                .setMessage(data.message)
                // 不可消失
                .setCancelable(false)
                // dialog消失后，sShowing设置为false
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        sShowing = false;
                    }
                });
        switch (data.buttons.size()) {
            // 一个button
            case 1: {
                final Button positive = data.buttons.get(0);
                builder.setPositiveButton(positive.text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadUrl(webView,"javascript:" + positive.action);
                    }
                });
                break;
            }
            // 两个button
            case 2: {
                final Button positive = data.buttons.get(1);
                final Button negative = data.buttons.get(0);
                builder.setPositiveButton(positive.text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadUrl(webView,"javascript:" + positive.action);
                    }
                });
                builder.setNegativeButton(negative.text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadUrl(webView,"javascript:" + negative.action);
                    }
                });
                break;
            }
            // 3个button
            case 3: {
                final Button positive = data.buttons.get(2);
                final Button negative = data.buttons.get(0);
                final Button neutral = data.buttons.get(1);
                builder.setPositiveButton(positive.text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadUrl(webView,"javascript:" + positive.action);
                    }
                });
                builder.setNegativeButton(negative.text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadUrl(webView,"javascript:" + negative.action);
                    }
                });
                builder.setPositiveButton(positive.text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadUrl(webView,"javascript:" + positive.action);
                    }
                });
                builder.setNeutralButton(neutral.text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadUrl(webView,"javascript:" + neutral.action);
                    }
                });
                break;
            }
            default:{
                return false;
            }
        }
        builder.create().show();
        sShowing = true;
        return true;
    }

    static class Button {
        String text;
        String action;

        public Button() {}
    }

    static class Data {
        String title;
        String message;
        List<Button> buttons = new ArrayList<>();

        public Data(){}

        public boolean valid() {
            if (TextUtils.isEmpty(message)) {
                return false;
            }
            if (null == buttons || buttons.size() == 0) {
                return false;
            }
            return true;
        }
    }

    private static void loadUrl(View webView, String url){
        if (webView instanceof WebView)
        {
            ((WebView)webView).loadUrl(url);
        }
        else if (webView instanceof XWalkCordovaView){
            ((XWalkCordovaView)webView).load(url, null);
        }
    }

}
