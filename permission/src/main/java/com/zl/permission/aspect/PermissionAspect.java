package com.zl.permission.aspect;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.zl.permission.MyPermissionActivity;
import com.zl.permission.annotation.Permission;
import com.zl.permission.annotation.PermissionCancel;
import com.zl.permission.annotation.PermissionDenied;
import com.zl.permission.core.IPermission;
import com.zl.permission.utils.PermissionUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class PermissionAspect {

    //切入点  监听的注解
    @Pointcut("execution(@com.zl.permission.annotation.Permission * *(..)) && @annotation(aa)")
    public void pointActionMethod(Permission aa) {
    }

    //真正增加代码
    //环绕
    @Around("pointActionMethod(aa)")
    public void aProcessdingJoinPoint(final ProceedingJoinPoint point, Permission aa) throws Throwable {
        //先定义一个上下文环境
        Context context = null;
        final Object thisObject = point.getThis(); //thisObject == null 环境有问题
        //context 初始化
        if (thisObject instanceof Context) {
            context = (Context) thisObject;
        } else if (thisObject instanceof Fragment) {
            context = ((Fragment) thisObject).getActivity();
        }

        //判断是否为null
        if (null == context || aa == null) {
            throw new IllegalAccessException("null == context||aa == null is null");
        }

        final Context finalContext = context;
        MyPermissionActivity.requestPermissionAction(context, aa.value(), aa.requestCode(),
                new IPermission() {
                    @Override
                    public void ganted() {
                        //申请成功
                        try {
                            point.proceed();///被Permission注解的函数，正常执行下去，不拦截
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }

                    @Override
                    public void cancel() {
                        //被拒绝
                        PermissionUtils.invokeAnnotation(thisObject, PermissionCancel.class);
                    }

                    @Override
                    public void denied() {
                        //被拒绝 不再被提醒
                        PermissionUtils.invokeAnnotation(thisObject, PermissionDenied.class);
                        //跳转到手机设置界面
                        PermissionUtils.startAndroidSettings(finalContext);
                    }
                });

    }

}
