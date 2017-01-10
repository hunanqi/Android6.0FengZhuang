package com.example.com.android60fengzhuang;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<String> lists = new ArrayList<String>();
        lists.add(android.Manifest.permission.READ_PHONE_STATE);
        lists.add(android.Manifest.permission.WRITE_CONTACTS);
        reques(lists);
    }

    private void reques(final List<String> lists) {
        requesPermission(lists, new MPermissionListener() {
            @Override
            public void success() {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }

            @Override
            public void faild(final List<String> list) {
                String s = zhuanHuan(list);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        reques(list);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainActivity.this.finish();
                    }
                }).setTitle("我们确实需要"+s+"权限").show();
            }

            @Override
            public void noRemind(List<String> list) {
                String s = zhuanHuan(list);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainActivity.this.finish();
                    }
                }).setTitle("没有"+s+"权限无法开启app功能，请在设置中开启权限").show();
            }
        });
    }

    /**
     * 转换汉字
     * @param list
     * @return
     */
    private String zhuanHuan(List<String> list) {
        StringBuffer buffer=new StringBuffer();
               for(String str: list){
                   switch (str){
                       case Manifest.permission.WRITE_CONTACTS:
                           buffer.append("读取通讯录 ");
                           break;
                       case Manifest.permission.READ_PHONE_STATE:
                           buffer.append("读取电话状态改变 ");
                           break;
                           default:break;
                   }
               }
        return buffer.toString();
    }
}
