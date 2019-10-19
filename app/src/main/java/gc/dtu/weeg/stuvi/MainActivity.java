package gc.dtu.weeg.stuvi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import gc.dtu.weeg.stuvi.bluetooth.BluetoothService;
import gc.dtu.weeg.stuvi.bluetooth.BluetoothState;
import gc.dtu.weeg.stuvi.bluetooth.DeviceListActivity;
import gc.dtu.weeg.stuvi.fregment.AppVersioninfoFregment;
import gc.dtu.weeg.stuvi.fregment.BaseFragment;
import gc.dtu.weeg.stuvi.fregment.BasicinfoFregment;
import gc.dtu.weeg.stuvi.fregment.CNKFixedPagerAdapter;
import gc.dtu.weeg.stuvi.fregment.FrozendataFregment;
import gc.dtu.weeg.stuvi.fregment.GasSensorSetFragment;
import gc.dtu.weeg.stuvi.fregment.GateStatusControlFragment;
import gc.dtu.weeg.stuvi.fregment.Hex2BinConvertFragment;
import gc.dtu.weeg.stuvi.fregment.InstrumentInputFregment;
import gc.dtu.weeg.stuvi.fregment.LocalsettngsFregment;
import gc.dtu.weeg.stuvi.fregment.NBRegisiterfragment;
import gc.dtu.weeg.stuvi.fregment.PressSensoraddSetframent;
import gc.dtu.weeg.stuvi.fregment.RealtimedataFregment;
import gc.dtu.weeg.stuvi.fregment.STUVISettingFragment;
import gc.dtu.weeg.stuvi.fregment.SensorInputFregment;
import gc.dtu.weeg.stuvi.fregment.StuViSSVSettingFragment;
import gc.dtu.weeg.stuvi.myview.CustomDialog;
import gc.dtu.weeg.stuvi.utils.Constants;
import gc.dtu.weeg.stuvi.utils.DigitalTrans;
import gc.dtu.weeg.stuvi.utils.ToastUtils;

import static gc.dtu.weeg.stuvi.bluetooth.BluetoothState.REQUEST_CONNECT_DEVICE;
import static gc.dtu.weeg.stuvi.bluetooth.BluetoothState.REQUEST_ENABLE_BT;

public class MainActivity extends FragmentActivity {


    //蓝牙扫描
    RelativeLayout rllBtScan;
    private TextView mTxtStatus;
    //蓝牙超时线程
    private BlueToothTimeOutMornitor mThreedTimeout;
    LayoutInflater mLayoutInflater;
    BaseFragment mCurrentpage;
    BaseFragment mPrepage;
    ViewPager info_viewpager;
    private CNKFixedPagerAdapter mPagerAdater;
    /**
     * 当前选择的分类
     */
    private int mCurClassIndex=0;
    public int mPreClassIndex=0;
    public int mCurClassIndex1=0;
    /**
     * 选择的分类字体颜色
     */
    private int mColorSelected;
    /**
     * 非选择的分类字体颜色
     */
    private int mColorUnSelected;
    /**

     /**
     * 水平滚动的Tab容器
     */
    private HorizontalScrollView mScrollBar;
    /**
     * 分类导航的容器
     */
    private ViewGroup mClassContainer;
    int mScrollX = 0;
    private List<BaseFragment> fragments;
    public String[] titles;
    //蓝牙状态保存
    public Boolean mIsconnect = false;
    // Name of the connected device
    public String mConnectedDeviceName = null;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the services
    public BluetoothService mBTService = null;

    String gOwner = "";
    static MainActivity instanceMainActivity = null;

    private long exitTime = 0;
    public CustomDialog mDialog;

    //各个子页面
//    public BasicinfoFregment fregment1;
    public STUVISettingFragment fregment1;
//    public RealtimedataFregment fregment2;
    public StuViSSVSettingFragment fregment2;
    public InstrumentInputFregment   fregment3;   //FrozendataFregment
    public SensorInputFregment fregment4;    //LocalsettngsFregment
    public GasSensorSetFragment  fregment5;
//    public InstrumentInputFregment fregment6;
//    public PressSensoraddSetframent fregment7;
//    public NBRegisiterfragment      fregment8;
//    public AppVersioninfoFregment   fregment9;
//    public Hex2BinConvertFragment   fragment10;
//    public GasSensorSetFragment     fragment11;
//    public GateStatusControlFragment fragment12;


    //接口
    Ondataparse mydataparse=null;
//    OnPageSelectedinviewpager myOnPageSelectedinviewpager=null;
    public static MainActivity getInstance() {
        return instanceMainActivity;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x.Ext.init(getApplication());
//        ToastUtils.showToast(this,getString(R.string.stu_i_title1));
       // x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
        instanceMainActivity = this;
        mydataparse=null;
        InitView();
        InitFrgment();
        InitBlueTooth();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled())
        {
            //打开蓝牙
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        else
        {
            if (mBTService == null) {
                // Initialize the BluetoothService to perform bluetooth
                // connections
                mBTService = new BluetoothService(this, mHandler);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity
        // returns.
        if (mBTService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't
            // started already
            if (mBTService.getState() == BluetoothState.STATE_NONE) {
                // Start the Bluetooth services
                mBTService.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth services
        if (mBTService != null)
            mBTService.stop();
    }

    // The Handler that gets information back from the BluetoothService
    @SuppressLint("HandlerLeak")
    public final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothState.MESSAGE_STATE_CHANGE:
                    // if (D)
                    //    Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothState.STATE_CONNECTED:
                            //setStatus(getString(R.string.title_connected_to,
                            //		mConnectedDeviceName));
                            mTxtStatus.setText(getString(R.string.title_connected_to) + mConnectedDeviceName);

                            // mConversationArrayAdapter.clear();
                            mIsconnect = true;
                            break;
                        case BluetoothState.STATE_CONNECTING:
                            //setStatus(R.string.title_connecting);
                            mTxtStatus.setText(R.string.title_connecting);
                            mIsconnect = false;
                            break;
                        case BluetoothState.STATE_LISTEN:
                        case BluetoothState.STATE_NONE:
                            //   Log.d("zl","BluetoothState_state:"+"STATE_NONE/STATE_LISTEN");
                            //setStatus(R.string.title_not_connected);
                            mTxtStatus.setText(R.string.title_not_connected);
                            mIsconnect = false;
                            break;
                    }
                    break;
                case BluetoothState.MESSAGE_WRITE:
                    // byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    // String writeMessage = new String(writeBuf);
                    // mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case BluetoothState.MESSAGE_READ:
                    if (mThreedTimeout != null)
                                mThreedTimeout.interrupt();
                    mThreedTimeout = null;
                    byte[] readBuf = (byte[]) msg.obj;

                    String readMessage = "";
                    for (int i = 0; i < msg.arg1; i++) {
                        //readMessage += readBuf[i];

                        String hex = Integer.toHexString(readBuf[i] & 0xFF);
                        if (hex.length() == 1) {
                            hex = '0' + hex;
                        }

                        readMessage += hex;
                    }

                    byte[] readOutBuf = DigitalTrans.hex2byte(readMessage);
                    String readOutMsg = DigitalTrans.byte2hex(readOutBuf);

                    //获取接收的返回数据
                    Log.v("ttt", "recv:" + readOutMsg);

                    if (mydataparse != null) {
                        mydataparse.datacometoparse(readOutMsg, readOutBuf);
                    } else {
                        mCurrentpage.OndataCometoParse(readOutMsg, readOutBuf);
                    }
                    //mDialog.dismiss();

                    break;
                case BluetoothState.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(BluetoothState.DEVICE_NAME);
                    ToastUtils.showToast(getApplicationContext(), getString(R.string.title_connected_to) + mConnectedDeviceName);

                    break;
                case BluetoothState.MESSAGE_TOAST:

                    ToastUtils.showToast(getApplicationContext(),
                            msg.getData().getString(BluetoothState.TOAST));
                    break;
                case BluetoothState.MESSAGE_STATE_TIMEOUT:
                    if (mIsconnect) {
                        // 关闭连接socket
                        try {
                            // 关闭蓝牙
                            mTxtStatus.setText(R.string.title_not_connected);
                            mBTService.stop();
                        } catch (Exception e) {
                        }
                    }
                    mThreedTimeout = null;
                    mDialog.dismiss();
                    // ToastUtils.showToast(getActivity(), "数据长度异常");
                    ToastUtils.showToast(MainActivity.this, getString(R.string.DEVICE_NO_RESPONE));
                    break;
                case BluetoothState.MESSAGE_BLOCK_TIMEOUT:
//                    if(mCurrentpage==fregment3) {
////                        Log.d("zl","BluetoothState.MESSAGE_BLOCK_TIMEOUT:"+msg.arg1);
//                        if(msg.arg1==Constants.NB_FRESONDATA_KEY_BLOCK_FINISHED)
//                        {
////                            Log.d("zl","Main OnBlockdataFinished");
//                            fregment3.OnBlockdataFinished();
//                        }
//                        else if(msg.arg1==Constants.NB_FRESONDATA_KEY_TASKFINISHED_FINISHED)
//                        {
////                            Log.d("zl","Main updatelistview");
//                            fregment3.updatelistview();
//                        }
//                    }
                    break;
                case BluetoothState.MESSAGE_CONVERT_INFO:
//                    if(mCurrentpage==fragment10)
//                    {
//                        fragment10.OnFileConvertResult(msg.arg1);
//                    }
                    break;
            }
        }
    };
    private void InitFrgment() {

        //添加Tab标签
        int index=0;
        addScrollView(titles);
        mScrollBar.post(new Runnable() {
            @Override
            public void run() {
                mScrollBar.scrollTo(mScrollX,0);
            }
        });

        fragments=new ArrayList<BaseFragment>();

//        fregment1 = new BasicinfoFregment();
        fregment1 = new STUVISettingFragment();
        Bundle bundle0 = new Bundle();
        bundle0.putInt("position",index);
        bundle0.putString("extra",titles[index++]);
        fregment1.setArguments(bundle0);
        fragments.add(fregment1);

        fregment2 =new StuViSSVSettingFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt("position",index);
        bundle1.putString("extra",titles[index++]);
        fregment2.setArguments(bundle1);
        fragments.add(fregment2);

        fregment3 = new InstrumentInputFregment();  //FrozendataFregment
        Bundle bundle2 = new Bundle();
        bundle2.putInt("position",index);
        bundle2.putString("extra",titles[index++]);
        fregment3.setArguments(bundle2);
        fragments.add(fregment3);

        fregment4 = new SensorInputFregment();
        Bundle bundle3 = new Bundle();
        bundle3.putInt("position",index);
        bundle3.putString("extra",titles[index++]);
        fregment4.setArguments(bundle3);
        fragments.add(fregment4);

        fregment5 = new GasSensorSetFragment();
        Bundle bundle4 = new Bundle();
        bundle4.putInt("position",index);
        bundle4.putString("extra",titles[index++]);
        fregment5.setArguments(bundle4);
        fragments.add(fregment5);

//        //添加气体传感器的设置
//        fragment11 = new GasSensorSetFragment();
//        Bundle bundle11 = new Bundle();
//        bundle11.putInt("position",index);
//        bundle11.putString("extra",titles[index++]);
//        fragment11.setArguments(bundle11);
//        fragments.add(fragment11);
//
//        fregment6 = new InstrumentInputFregment();
//        Bundle bundle5 = new Bundle();
//        bundle5.putInt("position",index);
//        bundle5.putString("extra",titles[index++]);
//        fregment6.setArguments(bundle5);
//        fragments.add(fregment6);
//
//        fregment7 =new PressSensoraddSetframent();
//        Bundle bundle6 = new Bundle();
//        bundle6.putInt("position",index);
//        bundle6.putString("extra",titles[index++]);
//        fregment7.setArguments(bundle6);
//        fragments.add(fregment7);
//
//        fregment8 =new NBRegisiterfragment();
//        Bundle bundle7 = new Bundle();
//        bundle7.putInt("position",index);
//        bundle7.putString("extra",titles[index++]);
//        fregment8.setArguments(bundle7);
//        fragments.add(fregment8);
//
////        fragment12 = new GateStatusControlFragment();
////        Bundle bundle12 = new Bundle();
////        bundle12.putInt("position",index);
////        bundle12.putString("extra",titles[index++]);
////        fragment12.setArguments(bundle12);
////        fragments.add(fragment12);
//
//        fregment9 =new AppVersioninfoFregment();
//        Bundle bundle8= new Bundle();
//        bundle8.putInt("position",index);
//        bundle8.putString("extra",titles[index++]);
//        fregment9.setArguments(bundle8);
//        fragments.add(fregment9);
//
////        sixthFragment = new Pressure2Fragment();
////        Bundle bundle6 = new Bundle();
////        bundle6.putString("extra",titles[index++]);
////        sixthFragment.setArguments(bundle6);
////        fragments.add(sixthFragment);
//        fragment10 = new Hex2BinConvertFragment();
//        Bundle bundle9= new Bundle();
//        bundle9.putInt("position",index);
//        bundle9.putString("extra",titles[index++]);
//        fragment10.setArguments(bundle9);
//        fragments.add(fragment10);

        mPagerAdater=new CNKFixedPagerAdapter(getSupportFragmentManager());
        mPagerAdater.setTitles(titles);
        mPagerAdater.setFragments(fragments);
        info_viewpager.setAdapter(mPagerAdater);
        info_viewpager.addOnPageChangeListener(new OnpagechangedListernerImp());

        mCurrentpage=fregment1;
        mPrepage=fregment1;
    }

    private void addScrollView(String[] titles) {
        final int count = titles.length;
        for (int i = 0; i < count; i++) {
            // Log.e("tchl","onclick: i:"+i);
            final String title = titles[i];
            final View view = mLayoutInflater.inflate(R.layout.horizontal_item_layout, null);
            final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.horizontal_linearlayout_type);
            final ImageView img_type = (ImageView) view.findViewById(R.id.horizontal_img_type);
            final TextView type_name = (TextView) view.findViewById(R.id.horizontal_tv_type);
            type_name.setText(title);
            if (i == mCurClassIndex) {
                //已经选中
                type_name.setTextColor(ContextCompat.getColor(this, R.color.color_selected));
                img_type.setImageResource(R.drawable.bottom_line_blue);
            } else {
                //未选中
                type_name.setTextColor(ContextCompat.getColor(this, R.color.color_unselected));
                img_type.setImageResource(R.drawable.bottom_line_gray);
            }
            final int index=i;
            //点击顶部Tab标签，动态设置下面的ViewPager页面
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //首先设置当前的Item为正常状态
                    // Log.e("tchl","onclick: first mCurClassIndex:"+mCurClassIndex);
                    View currentItem=mClassContainer.getChildAt(mCurClassIndex);
                    ((TextView)(currentItem.findViewById(R.id.horizontal_tv_type))).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_unselected));
                    ((ImageView)(currentItem.findViewById(R.id.horizontal_img_type))).setImageResource(R.drawable.bottom_line_gray);
                    mCurClassIndex=index;
                    mCurClassIndex1=index;
                    // Log.e("tchl","onclick: first index:"+index);
                    //设置点击状态
                    img_type.setImageResource(R.drawable.bottom_line_blue);
                    type_name.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.color_selected));
                    //跳转到指定的ViewPager
                    info_viewpager.setCurrentItem(mCurClassIndex);
                    mCurrentpage=fragments.get(mCurClassIndex);
                    mCurrentpage=fragments.get(mCurClassIndex);
                    mCurrentpage.Oncurrentpageselect(mCurClassIndex);
                    if(mPrepage!=null)
                    {
                        mPrepage.Oncurrentpageselect(mCurClassIndex);
                    }
                    mPrepage=mCurrentpage;
//                    if(mCurClassIndex1!=2)
//                    {
//                        if(mPreClassIndex==2)
//                        {
//                            Log.d("zl","addScrollView bluetoothblockdisable");
//                            bluetoothblockdisable();
//                        }
//                    }
                    mPreClassIndex=mCurClassIndex1;
                }
            });

            mClassContainer.addView(view);
        }

    }

    private void InitView() {
        titles=new String[]{
            getString(R.string.stu_i_title1),
            getString(R.string.stu_i_title2),
            getString(R.string.stu_i_title3),
            getString(R.string.stu_i_title4),
            getString(R.string.stu_i_title5),
//                "1","2","3","4","5",
//               "气体传感器接入", "仪表接入","传感器调试","NB业务注册","阀门控制","版本信息","固件升级"
        };
        mDialog = CustomDialog.createProgressDialog(this, Constants.TimeOutSecond, new CustomDialog.OnTimeOutListener() {
            @Override
            public void onTimeOut(CustomDialog dialog) {
                dialog.dismiss();
                ToastUtils.showToast(getBaseContext(), getString(R.string.timeout));
            }
        });
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.d("zl","dialog has been cancelde");
                if(mCurrentpage!=null)
                {
                    mCurrentpage.Ondlgcancled();
                }
            }
        });
        //蓝牙扫描
        rllBtScan = (RelativeLayout)findViewById(R.id.rll_bt_scan);
        rllBtScan.setOnClickListener(new OnclickListererImp());
        //蓝牙监听状态
        mTxtStatus = (TextView) findViewById(R.id.txt_status);

        mLayoutInflater = LayoutInflater.from(this);
        info_viewpager = (ViewPager)findViewById(R.id.info_viewpager);
        mScrollBar = (HorizontalScrollView)findViewById(R.id.horizontal_info);
        mClassContainer = (ViewGroup)findViewById(R.id.ll_container);
    }

    private void InitBlueTooth()
    {
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null)
        {
            ToastUtils.showToast(this, getString(R.string.PHONE_HAS_NO_BLUETOOTH));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BluetoothState.REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(
                            DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter
                            .getRemoteDevice(address);
                    // Attempt to connect to the device
                    mBTService.connect(device);
                }
                break;
            case BluetoothState.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled
                    // Initialize the BluetoothService to perform bluetooth
                    // connections

                    mBTService = new BluetoothService(this, mHandler);

                    Intent serverIntent = new Intent(this, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);


                } else {
                    // User did not enable Bluetooth or an error occured
                    // Log.d(TAG, "BT not enabled");
                    ToastUtils.showToast(this, getString(R.string.bt_not_enabled_leaving));

                    finish();
                }

                break;

        }
    }

    public void sendData(String data, String strOwner) {


        // Check that we're actually connected before trying anything
        if (mBTService.getState() != BluetoothState.STATE_CONNECTED) {

            ToastUtils.showToast(this,  R.string.not_connected);
            return;
        }

        // Check that there's actually something to send
        if (data.length() > 0) {
            gOwner = strOwner;
            Log.v("ttt", "Send: " + data);
            String hexString = data;
            byte[] buff = DigitalTrans.hex2byte(hexString);

            mBTService.write(buff);
            if(mThreedTimeout==null)
            {
                mThreedTimeout=new BlueToothTimeOutMornitor();
                mThreedTimeout.start();
            }
        }
    }

    //当timeout设置为0时 ，不会做超时计算
    public void sendData(String data, String strOwner,int timeout) {

//        Log.d("zl","MainActivity:"+data);
        // Check that we're actually connected before trying anything
        if (mBTService.getState() != BluetoothState.STATE_CONNECTED) {

            ToastUtils.showToast(this,  R.string.not_connected);
            return;
        }

        // Check that there's actually something to send
        if (data.length() > 0) {
            gOwner = strOwner;
            Log.v("ttt", "Send: " + data);
            String hexString = data;
            byte[] buff = DigitalTrans.hex2byte(hexString);

            mBTService.write(buff);
            if(timeout>0)
            {
                if(mThreedTimeout==null)
                {
                    mThreedTimeout=new BlueToothTimeOutMornitor(timeout);
                    mThreedTimeout.start();
                }
            }
        }
    }

    public void sendData(byte[] databuf,int timeout)
    {
        mBTService.write(databuf);
        if(timeout>0)
        {
            if(mThreedTimeout==null)
            {
                mThreedTimeout=new BlueToothTimeOutMornitor(timeout);
                mThreedTimeout.start();
            }
        }
    }
    // 双击退出-----------------------------------------------
    @SuppressLint("RestrictedApi")
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtils.showToast(this, getString(R.string.PRESS_AGAIN_EXIT));
                exitTime = System.currentTimeMillis();
            } else {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            return false;
        }

        return super.dispatchKeyEvent(event);

    }
    public class OnclickListererImp implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            switch(v.getId())
            {
                case R.id.rll_bt_scan:// 蓝牙扫描
                    if(!mIsconnect)
                    {
                        Intent serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                    }
                    else
                    {
                        // 关闭连接socket
                        try {
                            // 关闭蓝牙
                            mTxtStatus.setText(R.string.title_not_connected);
                            mBTService.stop();
                        } catch (Exception e) {
                        }
                    }
                    break;
                default:
                    break;
            }

        }
    }

    private class OnpagechangedListernerImp implements ViewPager.OnPageChangeListener
    {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Log.d("zl","in onPageSelected");
            mCurClassIndex1=position;
            mCurrentpage=fragments.get(position);
//            if(mCurClassIndex1!=2)
//            {
//                if(mPreClassIndex==2)
//                {
//                    bluetoothblockdisable();
//                    Log.d("zl","OnpagechangedListernerImp bluetoothblockdisable");
//                }
//            }
            mPreClassIndex=mCurClassIndex1;
            mCurrentpage.Oncurrentpageselect(position);
            if(mPrepage!=null)
            {
                mPrepage.Oncurrentpageselect(position);
            }
            mPrepage=mCurrentpage;
//            if(myOnPageSelectedinviewpager!=null)
//            {
//                myOnPageSelectedinviewpager.currentviewpager(position);
//            }
            View preView=mClassContainer.getChildAt(mCurClassIndex);
            ((TextView)(preView.findViewById(R.id.horizontal_tv_type))).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_unselected));
            ((ImageView)(preView.findViewById(R.id.horizontal_img_type))).setImageResource(R.drawable.bottom_line_gray);
            mCurClassIndex=position;
            //设置当前为选中状态
            View currentItem=mClassContainer.getChildAt(mCurClassIndex);
            ((ImageView)(currentItem.findViewById(R.id.horizontal_img_type))).setImageResource(R.drawable.bottom_line_blue);
            ((TextView)(currentItem.findViewById(R.id.horizontal_tv_type))).setTextColor(ContextCompat.getColor(MainActivity.this, R.color.color_selected));
            //这边移动的距离 是经过计算粗略得出来的
            mScrollX=currentItem.getLeft()-300;
            //Log.d("zttjiangqq", "mScrollX:" + mScrollX);
            mScrollBar.post(new Runnable() {
                @Override
                public void run() {
                    mScrollBar.scrollTo(mScrollX,0);
                }
            });

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }



    public String GetStateConnect()
    {
        return mTxtStatus.getText().toString();
    }
    public interface Ondataparse
    {
        void datacometoparse(String readOutMsg1,byte[] readOutBuf1);
    }

    public void setOndataparse(Ondataparse ondataparse)
    {
        mydataparse=ondataparse;
    }

    public class BlueToothTimeOutMornitor extends Thread
    {
       public int mtimeout;
        BlueToothTimeOutMornitor()
        {
            mtimeout=2000;
        }
        BlueToothTimeOutMornitor(int timeout)
        {
            mtimeout=timeout;
        }
        @Override
        public void run() {
            try {
                sleep(mtimeout);
                MainActivity.this.mHandler.obtainMessage(BluetoothState.MESSAGE_STATE_TIMEOUT)
                        .sendToTarget(); //       mHandler.obtainMessage(BluetoothState.MESSAGE_READ, bytes, -1, buffer)
                      //  .sendToTarget();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public String getmConnectedDeviceName()
    {
        String str="";
        if(mConnectedDeviceName==null)
        {
            return null;
        }
        if(mConnectedDeviceName.equals(getResources().getString(R.string.not_connected)))
        {
            return null;
        }
        int len=mConnectedDeviceName.length();
        for(int i=0;i<len;i++)
        {
            if(mConnectedDeviceName.charAt(i)>=48&&mConnectedDeviceName.charAt(i)<=57)
            {
                str+=mConnectedDeviceName.charAt(i);
            }

        }
        return str;
    }

    public BluetoothService getcurblueservice()
    {
       return   mBTService;
    }
    public void bluetoothblockdisable() {
//        mBTService.SetBlockmode(false);
//        mBTService.emptyalldata();
//        mBTService.getcurSemaphore().release();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
////        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        Log.d("zl","Main onRequestPermissionsResult");
//        if(mCurrentpage==fragment10)
//        {
//            fragment10.onRequestPermissionsResult(requestCode,permissions,grantResults);
//        }
//    }
}
