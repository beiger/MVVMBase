package com.bing.mvvmbase.module.loadingpage;


import com.bing.mvvmbase.R;
import com.kingja.loadsir.callback.Callback;

public class NetErrorCallback extends Callback {
    @Override
    protected int onCreateView() {
        return R.layout.layout_network_useless;
    }
}
