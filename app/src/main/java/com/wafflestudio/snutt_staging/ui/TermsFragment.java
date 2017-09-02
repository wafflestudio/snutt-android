package com.wafflestudio.snutt_staging.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseFragment;
import com.wafflestudio.snutt_staging.manager.PrefManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by makesource on 2017. 1. 24..
 */

public class TermsFragment extends SNUTTBaseFragment {
    private WebView webView;
    private Map headers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_webview, container, false);
        headers = new HashMap();
        headers.put("x-access-apikey", getResources().getString(R.string.api_key));
        headers.put("x-access-token", PrefManager.getInstance().getPrefKeyXAccessToken());
        webView = (WebView) rootView.findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient()); // 이걸 안해주면 새창이 뜸
        webView.loadUrl(getString(R.string.api_server) + getString(R.string.terms), headers);
        return rootView;
    }
}
