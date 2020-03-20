package com.zl.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.zl.permission.core.IPermission;
import com.zl.permission.utils.PermissionUtils;

//专门权限处理
public class MyPermissionActivity extends Activity {


    //定义权限处理的标记   --接收用户传递进来的
    private final static String PARAM_PERMISSION = "param_permission";
    private final static String PARAM_PERMISSION_CODE = "param_permission_code";
    public final static int PARAM_PERMISSION_CODE_DEFAULT = -1;

    private String[] permissions;
    private int requestCode;

    //方便回调的监听
    private static IPermission iPermission;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_permission);

        permissions = getIntent().getStringArrayExtra(PARAM_PERMISSION);
        requestCode = getIntent().getIntExtra(PARAM_PERMISSION_CODE, PARAM_PERMISSION_CODE_DEFAULT);

        if (permissions == null && requestCode < 0 && iPermission == null) {
            this.finish();
            return;
        }
        //开始检查  是否已授权
        boolean permissionRequest = PermissionUtils.hasPermissionRequest(this, permissions);
        if (permissionRequest) {
            //通知已授权
            iPermission.ganted();
            this.finish();
            return;
        }

        //需要申请权限
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    //申请的结果


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //返回的结果需要验证
        if (PermissionUtils.requestPermissionSuccess(grantResults)) {
            iPermission.ganted();//授权成功
            this.finish();
            return;
        }
        //没有成功  用户点击了拒绝并且勾选了不再提醒
        if (!PermissionUtils.shouldShowRequestPermissionRationale(this, permissions)) {
            //用户拒绝，不再提醒
            iPermission.denied();
            this.finish();
            return;
        }
        iPermission.cancel();
        this.finish();
        return;
    }

    //不让activity不要有任何动画
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    //此权限申请专用的Activity，对外暴露  static
    public static void requestPermissionAction(Context context, String[] permissions,
                                               int requestCode, IPermission iPermission) {
        MyPermissionActivity.iPermission = iPermission;
        Intent intent = new Intent(context, MyPermissionActivity.class);
        //效果
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_PERMISSION_CODE, requestCode);
        bundle.putStringArray(PARAM_PERMISSION, permissions);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
