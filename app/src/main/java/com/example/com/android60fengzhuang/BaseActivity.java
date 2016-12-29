package com.example.com.android60fengzhuang;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 胡楠奇 on 2016/12/28.
 */

public class BaseActivity extends AppCompatActivity {
    private MPermissionListener mPermission;

    interface MPermissionListener {
        /**
         * 有权限
         */
        void success();

        /**
         * 有权限没有获取成功
         *
         * @param list
         */
        void faild(List<String> list);

        /**
         * 有权限用户选择了不再提醒
         *
         * @param list
         */
        void noRemind(List<String> list);
    }

    /**
     * 请求权限
     *
     * @param list
     */
    public void requesPermission(List<String> list, MPermissionListener permission) {
        mPermission = permission;
        List<String> permissions = new ArrayList<>();
        if (list.size() <= 0)
            return;
        for (String str : list) {
            //把没有权限的放进permissions数组
            if (ContextCompat.checkSelfPermission(this, str) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(str);
            }
        }
        //permissions数组为空说明全部都有权限
        if (permissions.size() <= 0) {
            mPermission.success();
        } else {//开始申请权限
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        List<String> permissionList = new ArrayList<>();
        List<String> noTixing = new ArrayList<>();
        if (requestCode == 123) {
            if (grantResults.length > 0) {
                //把用户拒绝的权限加入permissionList数组
                int length = grantResults.length;
                for (int i = 0; i < length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        permissionList.add(permissions[i]);
                    }
                }
                if (permissionList.size() == 0) {//用户全部允许了权限
                    mPermission.success();
                } else {//用户没有全部允许权限
                    //检测哪些权限，用户选择了不再提醒按钮
                    for (int i = 0; i < permissionList.size(); i++) {
                        boolean b = ActivityCompat.shouldShowRequestPermissionRationale(this, permissionList.get(i));
                        if (!b)
                            noTixing.add(permissionList.get(i));
                    }
                    if (noTixing.size() == 0)//权限获取失败
                        mPermission.faild(permissionList);
                    else {//有选项用户选择了不再提醒
                        mPermission.noRemind(noTixing);
                    }

                }
            }
        }
    }
}
