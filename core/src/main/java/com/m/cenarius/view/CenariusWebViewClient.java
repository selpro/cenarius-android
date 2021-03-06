package com.m.cenarius.view;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.m.cenarius.resourceproxy.network.InterceptJavascriptInterface;
import com.m.cenarius.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CenariusWebViewClient extends WebViewClient {

    static final String TAG = CenariusWebViewClient.class.getSimpleName();

    private List<CenariusWidget> mWidgets = new ArrayList<>();

    public CenariusWebViewClient(WebView webView) {
        mWebView = webView;
        mJSIntercept = new InterceptJavascriptInterface(this);
        mWebView.addJavascriptInterface(mJSIntercept, "interception");
    }

    // ajax 拦截
    private WebView mWebView = null;
    private InterceptJavascriptInterface mJSIntercept = null;
    //    private OkHttpClient client = new OkHttpClient();
    private InterceptJavascriptInterface.AjaxRequestContents mNextAjaxRequestContents = null;

    public void nextMessageIsAjaxRequest(InterceptJavascriptInterface.AjaxRequestContents ajaxRequestContents) {
        mNextAjaxRequestContents = ajaxRequestContents;
    }


    /**
     * 自定义url拦截处理
     *
     * @param widget
     */
    public void addCenariusWidget(CenariusWidget widget) {
        if (null != widget) {
            mWidgets.add(widget);
        }
    }

    public List<CenariusWidget> getCenariusWidgets() {
        return mWidgets;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (CenariusHandleRequest.handleWidgets(view, url, mWidgets)) {
            return true;
        }

        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (Utils.hasLollipop()) {
            return handleResourceRequest(view, request.getUrl().toString());
        } else {
            return super.shouldInterceptRequest(view, request);
        }
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return handleResourceRequest(view, url);
    }

//    @Override
//    public void onPageStarted(WebView view, String url, Bitmap favicon) {
//        super.onPageStarted(view, url, favicon);
//        LogUtils.i(TAG, "onPageStarted");
//    }
//
//    @Override
//    public void onPageFinished(WebView view, String url) {
//        super.onPageFinished(view, url);
//        LogUtils.i(TAG, "onPageFinished");
//    }
//
//    @Override
//    public void onLoadResource(WebView view, String url) {
//        super.onLoadResource(view, url);
//        LogUtils.i(TAG, "onLoadResource : " + url);
//    }

    /**
     * 拦截资源请求，部分资源需要返回本地资源
     * <p>
     * <p>
     * html，js资源直接渲染进程返回,图片等其他资源先返回空的数据流再异步向流中写数据
     * <p>
     * <p>
     * <note>这个方法会在渲染线程执行，如果做了耗时操作会block渲染</note>
     */
    private WebResourceResponse handleResourceRequest(WebView webView, String requestUrl) {
        WebResourceResponse webResourceResponse;
        if (mNextAjaxRequestContents != null) {
            // ajax 请求
            webResourceResponse = CenariusHandleRequest.handleAjaxRequest(requestUrl, mNextAjaxRequestContents);
        } else {
            // h5 请求
            webResourceResponse = CenariusHandleRequest.handleResourceRequest(requestUrl);
        }
        if (webResourceResponse != null) {
            return webResourceResponse;
        }

        return super.shouldInterceptRequest(webView, requestUrl);
    }
}

