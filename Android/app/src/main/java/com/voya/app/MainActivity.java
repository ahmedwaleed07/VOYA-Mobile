package com.voya.app;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/** Native VOYA preview container. Backend and production functions belong to the canonical web project. */
public class MainActivity extends Activity {
    private static final String SITE = "https://voya-app.funpony.chatgpt.site/";
    private static final String TRUSTED_HOST = "voya-app.funpony.chatgpt.site";
    private static final String OFFLINE_ORIGIN = "https://offline.voya.invalid/";
    private static final int MEDIA_PERMISSION_REQUEST = 1001;
    private static final int GEO_PERMISSION_REQUEST = 1002;
    private static final int FILE_REQUEST = 1003;
    private WebView browser;
    private ProgressBar progress;
    private PermissionRequest pendingMedia;
    private GeolocationPermissions.Callback pendingGeolocation;
    private String pendingGeolocationOrigin;
    private android.webkit.ValueCallback<Uri[]> fileCallback;
    private boolean showingOffline = false;
    private boolean initialError = false;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(245,243,238));
        getWindow().setNavigationBarColor(Color.rgb(245,243,238));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(245,243,238));
        browser = new WebView(this);
        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100);
        progress.setProgressTintList(android.content.res.ColorStateList.valueOf(Color.rgb(200,230,74)));
        root.addView(browser, new FrameLayout.LayoutParams(-1,-1));
        FrameLayout.LayoutParams bar = new FrameLayout.LayoutParams(-1, 5);
        bar.topMargin = 0;
        root.addView(progress,bar);
        setContentView(root);

        if(Build.VERSION.SDK_INT >= 35) {
            root.setOnApplyWindowInsetsListener((view, insets) -> {
                android.graphics.Insets i = insets.getInsets(WindowInsets.Type.systemBars());
                view.setPadding(i.left,i.top,i.right,i.bottom);
                return insets;
            });
        }

        WebSettings settings = browser.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setGeolocationEnabled(true);
        if(Build.VERSION.SDK_INT >= 26) settings.setSafeBrowsingEnabled(true);
        WebView.setWebContentsDebuggingEnabled(false);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(browser, false);

        browser.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri u=request.getUrl();
                if("voya".equalsIgnoreCase(u.getScheme()) && "retry".equalsIgnoreCase(u.getHost())) { openVOYA(); return true; }
                if("https".equalsIgnoreCase(u.getScheme()) && (TRUSTED_HOST.equalsIgnoreCase(u.getHost()) || "offline.voya.invalid".equalsIgnoreCase(u.getHost()))) return false;
                if("about".equalsIgnoreCase(u.getScheme())) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, u)); }
                catch(Exception e) { Toast.makeText(MainActivity.this,"Unable to open link",Toast.LENGTH_SHORT).show(); }
                return true;
            }
            @Override public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if(request.isForMainFrame() && !showingOffline) { initialError=true; showOffline(); }
            }
            @Override public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse response) {
                if(request.isForMainFrame() && response.getStatusCode() >= 500 && !showingOffline) { initialError=true; showOffline(); }
            }
            @Override public void onPageFinished(WebView view, String url) {
                progress.setVisibility(View.GONE);
            }
        });

        browser.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView v,int p) {
                progress.setProgress(p);
                progress.setVisibility(p<100?View.VISIBLE:View.GONE);
            }
            @Override public void onPermissionRequest(PermissionRequest request) {
                runOnUiThread(() -> requestWebMedia(request));
            }
            @Override public void onPermissionRequestCanceled(PermissionRequest request) {
                if(pendingMedia==request) pendingMedia=null;
            }
            @Override public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                if(!trustedOrigin(origin)) { callback.invoke(origin,false,false); return; }
                pendingGeolocation = callback;
                pendingGeolocationOrigin = origin;
                if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                    callback.invoke(origin,true,false); pendingGeolocation=null;
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, GEO_PERMISSION_REQUEST);
                }
            }
            @Override public boolean onShowFileChooser(WebView view, android.webkit.ValueCallback<Uri[]> callback, FileChooserParams params) {
                if(fileCallback!=null) fileCallback.onReceiveValue(null);
                fileCallback=callback;
                try { startActivityForResult(params.createIntent(), FILE_REQUEST); return true; }
                catch(ActivityNotFoundException e) { fileCallback=null; callback.onReceiveValue(null); return false; }
            }
        });

        if(state!=null && browser.restoreState(state)!=null) return;
        openVOYA();
    }

    private boolean trustedOrigin(String origin) {
        try {
            Uri url=Uri.parse(origin);
            return "https".equalsIgnoreCase(url.getScheme()) && TRUSTED_HOST.equalsIgnoreCase(url.getHost());
        } catch(Exception e) { return false; }
    }

    private void requestWebMedia(PermissionRequest request) {
        if(!trustedOrigin(request.getOrigin().toString())) { request.deny(); return; }
        List<String> needed=new ArrayList<>();
        Set<String> res=new HashSet<>(Arrays.asList(request.getResources()));
        for(String resource:res) {
            if(PermissionRequest.RESOURCE_VIDEO_CAPTURE.equals(resource)) needed.add(Manifest.permission.CAMERA);
            else if(PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(resource)) needed.add(Manifest.permission.RECORD_AUDIO);
            else { request.deny(); return; }
        }
        if(needed.isEmpty()) { request.deny(); return; }
        ArrayList<String> ask=new ArrayList<>();
        for(String p:needed) if(checkSelfPermission(p)!=PackageManager.PERMISSION_GRANTED) ask.add(p);
        if(ask.isEmpty()) request.grant(request.getResources());
        else {
            if(pendingMedia!=null) pendingMedia.deny();
            pendingMedia=request;
            requestPermissions(ask.toArray(new String[0]), MEDIA_PERMISSION_REQUEST);
        }
    }

    @Override public void onRequestPermissionsResult(int code, String[] permissions, int[] grants) {
        super.onRequestPermissionsResult(code,permissions,grants);
        if(code==MEDIA_PERMISSION_REQUEST && pendingMedia!=null) {
            boolean granted=true;
            for(int g:grants) if(g!=PackageManager.PERMISSION_GRANTED) granted=false;
            if(grants.length==0) granted=false;
            PermissionRequest req=pendingMedia; pendingMedia=null;
            if(granted && trustedOrigin(req.getOrigin().toString())) req.grant(req.getResources()); else req.deny();
        } else if(code==GEO_PERMISSION_REQUEST && pendingGeolocation!=null) {
            boolean granted=false;
            for(int g:grants) if(g==PackageManager.PERMISSION_GRANTED) granted=true;
            pendingGeolocation.invoke(pendingGeolocationOrigin,granted,false);
            pendingGeolocation=null;pendingGeolocationOrigin=null;
        }
    }

    @Override protected void onActivityResult(int code,int resultCode, Intent data) {
        super.onActivityResult(code,resultCode,data);
        if(code==FILE_REQUEST && fileCallback!=null) {
            Uri[] chosen=WebChromeClient.FileChooserParams.parseResult(resultCode,data);
            fileCallback.onReceiveValue(chosen);
            fileCallback=null;
        }
    }

    private void openVOYA() {
        initialError=false;showingOffline=false;
        progress.setVisibility(View.VISIBLE);
        browser.loadUrl(SITE);
    }
    private void showOffline() {
        if(showingOffline)return;
        showingOffline=true;
        try(BufferedReader r=new BufferedReader(new InputStreamReader(getAssets().open("offline.html")))) {
            StringBuilder h=new StringBuilder();String line;
            while((line=r.readLine())!=null) h.append(line).append('\n');
            browser.loadDataWithBaseURL(OFFLINE_ORIGIN,h.toString(),"text/html","UTF-8",null);
        } catch(Exception e) {
            browser.loadDataWithBaseURL(OFFLINE_ORIGIN,"<h1>VOYA</h1><p>Check your connection.</p><a href='voya://retry'>Try again</a>","text/html","UTF-8",null);
        }
    }

    @Override public void onBackPressed() {
        if(showingOffline) { openVOYA(); return; }
        if(browser.canGoBack()) browser.goBack(); else super.onBackPressed();
    }
    @Override protected void onPause(){super.onPause();browser.onPause();}
    @Override protected void onResume(){super.onResume();browser.onResume();}
    @Override protected void onSaveInstanceState(Bundle state){browser.saveState(state);super.onSaveInstanceState(state);}
    @Override protected void onDestroy(){
        if(fileCallback!=null){fileCallback.onReceiveValue(null);fileCallback=null;}
        if(pendingMedia!=null){pendingMedia.deny();pendingMedia=null;}
        browser.destroy();super.onDestroy();
    }
}
