package com.m.cenarius.resourceproxy.network;

import com.m.cenarius.Cenarius;
import com.m.cenarius.resourceproxy.cache.CacheHelper;
import com.m.cenarius.resourceproxy.cache.InternalCache;
import com.m.cenarius.route.Route;
import com.m.cenarius.route.RouteManager;
import com.m.cenarius.utils.LogUtils;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class HtmlHelper {

    public static final String TAG = HtmlHelper.class.getSimpleName();
    public static final List<String> mDownloadingProcess = new ArrayList<>();

    public static void downloadFilesWithinRoutes(List<Route> routes, final boolean shouldDownloadAll, final RouteManager.RouteRefreshCallback callback) {
        if (routes == null || routes.isEmpty()) {
            callback.onSuccess(null);
            return;
        }
        downloadFilesWithinRoutes(routes, shouldDownloadAll, callback, 0);
    }

    private static void downloadFilesWithinRoutes(final List<Route> routes, final boolean shouldDownloadAll, final RouteManager.RouteRefreshCallback callback, final int index) {
        if (index >= routes.size()) {
            callback.onSuccess(null);
            return;
        }

        final Route route = routes.get(index);

        // 如果文件在本地文件存在（要么在缓存，要么在资源文件夹），什么都不需要做
        String htmlFileURL = CacheHelper.getInstance().localHtmlURLForURI(route.uri);
        if (htmlFileURL != null) {
            downloadFilesWithinRoutes(routes, shouldDownloadAll, callback, index + 1);
            return;
        }

        // 文件不存在，下载下来
        RequestParams requestParams = new RequestParams(route.getHtmlFile());
        x.http().get(requestParams, new org.xutils.common.Callback.CommonCallback<byte[]>() {
            @Override
            public void onSuccess(byte[] result) {
                // 1. 存储到本地
                InternalCache.getInstance().saveCache(route, result);
                downloadFilesWithinRoutes(routes, shouldDownloadAll, callback, index + 1);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtils.i(TAG, "download html failed");
                if (shouldDownloadAll) {
                    callback.onFail();
                } else {
                    // 下载失败，仅删除旧文件
                    InternalCache.getInstance().removeCache(route);
                    downloadFilesWithinRoutes(routes, shouldDownloadAll, callback, index + 1);
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

}
