package com.example.myapplication.activity;

import com.bracks.mylib.base.basemvp.BasePresenter;
import com.bracks.mylib.base.basemvp.BaseView;

/**
 * good programmer.
 *
 * @date : 2019-08-14 9:59
 * @author: futia
 * @email : futianyi1994@126.com
 * @description :
 */
public abstract class BaseUi<V extends BaseView, P extends BasePresenter<V>> extends com.bracks.mylib.base.basemvp.BaseUi<V, P> {
    @Override
    public void showLoading(String msg, boolean isCancelable) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showToast(String msg) {

    }
}
