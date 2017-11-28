package com.vb.stockkeeper.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.webkit.JavascriptInterface;

/**
 * Created by vb on 11/27/17.
 */
public class DialogUtils {

    public static ProgressDialog showProgressDialog(Activity activity, String message) {
        ProgressDialog m_Dialog = new ProgressDialog(activity);
        m_Dialog.setMessage(message);
        m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_Dialog.setCancelable(false);
        m_Dialog.show();
        return m_Dialog;
    }
    public static ProgressDialog dismissProgressDialog(ProgressDialog mProgressDialog) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        return null;
    }

    public static DialogUtilsJSInterface JSInterface(Activity mActivity) {
        return new DialogUtilsJSInterface(mActivity);
    }
}

class DialogUtilsJSInterface {
    Activity mActivity;
    ProgressDialog loadDialog;

    public DialogUtilsJSInterface(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @JavascriptInterface
    public void showProgressDialog() {
        this.loadDialog = DialogUtils.showProgressDialog(mActivity, "");
    }

    @JavascriptInterface
    public void dismissProgressDialog() {
        this.loadDialog = DialogUtils.dismissProgressDialog(this.loadDialog);
    }
}