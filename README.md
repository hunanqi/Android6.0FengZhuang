# android 6.0权限封装+例子


## Usage
此项目根据csdn博客郭霖公开课（Android6.0运行时权限）思路封装。针对Android原装系统，国产机由于定制的原因不能完全兼容，也能正常运行，关于是否拒绝权限等，只能通过其他办法了，或者不做处理。
下载本项目，复制baseactivity相关代码到自己的基类activity中.包括onRequestPermissionsResult
方法下所有代码，以及自己定义的方法requesPermission和一个接口MPermissionListener.
项目思路：在基类activity中编写好方法，子类继承基类activity。
需要在其他非activity中调用的，需要自己写一个ativity管理类，找出当前的最顶层的activity，然后当做参数传递到基类requesPermission，代替其中this就能在其他其他java文件中调用此方法了。最后把requesPermission方法改成static
``` java
本项目使用例子
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
                }).setTitle("没有"+s+"权限无法开启app功能请在设置中开启权限").show();
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
```
### 基类Activity

``` java
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

```




