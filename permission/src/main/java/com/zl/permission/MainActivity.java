package com.zl.permission;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zl.permission.annotation.Permission;
import com.zl.permission.annotation.PermissionCancel;
import com.zl.permission.annotation.PermissionDenied;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //点击事件
    public void permissionRequestTest(View view) {
        testRequest();
    }

    //申请权限
    @Permission(value = Manifest.permission.READ_EXTERNAL_STORAGE, requestCode = 200)
    private void testRequest() {
        Toast.makeText(this, "权限申请成功", Toast.LENGTH_LONG).show();
        return;
    }

    //权限取消
    @PermissionCancel()
    public void testCancel() {
        Toast.makeText(this, "权限被拒绝", Toast.LENGTH_LONG).show();
    }

    //多次拒绝，还勾选了不再提示
    @PermissionDenied()
    public void testDenied() {
        Toast.makeText(this, "权限被拒绝，不再被提示", Toast.LENGTH_LONG).show();
    }
}
