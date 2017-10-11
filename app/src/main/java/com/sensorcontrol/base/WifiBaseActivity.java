package com.sensorcontrol.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.sensorcontrol.R;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

/**
 * Created by lizhe on 2017/10/11 0011.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public abstract class WifiBaseActivity extends BaseActivity{

    /** 等待框 */
    public ProgressDialog progressDialog;

    /**
     * 设置ProgressDialog
     */
    public void setProgressDialog() {
        progressDialog = new ProgressDialog(this);
        String loadingText = getString(R.string.loadingtext);
        progressDialog.setMessage(loadingText);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 检查网络连通性（工具方法）
     *
     * @param context
     * @return
     */
    public boolean checkNetwork(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = conn.getActiveNetworkInfo();
        if (net != null && net.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AndPermission.onRequestPermissionsResult(requestCode,permissions,grantResults,mypermissionlistener);

    }


    PermissionListener mypermissionlistener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantPermissions) {
            WifiBaseActivity.this.onSucceed(requestCode,grantPermissions);
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            WifiBaseActivity.this.onFailed(requestCode,deniedPermissions);
        }
    };



    public void onFailed(int requestCode, List<String> deniedPermissions) {}

    public void onSucceed(int requestCode, List<String> grantPermissions) {}

}
