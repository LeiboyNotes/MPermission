package com.zl.permission.core;

public interface IPermission {

    void ganted();//已经授权

    void cancel();//权限取消

    void denied();//拒绝权限

}
