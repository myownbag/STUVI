package gc.dtu.weeg.stuvi.fregment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import gc.dtu.weeg.stuvi.MainActivity;
import gc.dtu.weeg.stuvi.R;
import gc.dtu.weeg.stuvi.utils.CodeFormat;
import gc.dtu.weeg.stuvi.utils.Constants;
import gc.dtu.weeg.stuvi.utils.DigitalTrans;
import gc.dtu.weeg.stuvi.utils.ItemSetingActivity;
import gc.dtu.weeg.stuvi.utils.StuViDeviceItemSettingActivity;
import gc.dtu.weeg.stuvi.utils.ToastUtils;

public class STUVISettingFragment extends BaseFragment {
    View mView;
    LayoutInflater thisinflater;
    ViewGroup thiscontainer;
    Button mybut;
    int mIndexcmd=0;

    //需要操作的各个控件
    TextView mDeviceinfoTX;
    TextView mDeviceSoftVerTX;
    TextView mDeviceTimeTX;
    TextView mDeviceserialnumberTX;
    TextView mAPNTX;
    TextView mApnUsernameTX;
    TextView mApnPasswordTX;
    TextView mMainStationIPTX;
    TextView mMainStationPortTX;
    TextView mUpLoadTypeTX;
    TextView mUploadValueTX;

    //点击事件接收
    RelativeLayout mClickLocaltime;
    RelativeLayout mClickUserID;
    LinearLayout mClickConnectParameter;
    LinearLayout mClickupload;

    Boolean mIsWiteCmdRespone ;
    int  Reg208type=-1;

    public static String[][] baseinfo;
    public static String[][] registerinfosel;
    byte[][] senddatabuf;


    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
      //  mytimer.cancel();
//        Log.d("zl",CodeFormat.byteToHex(readOutBuf1,readOutBuf1.length));
        if(mIsWiteCmdRespone == true)
        {
            mIsWiteCmdRespone = false;
            byte[] buf =new byte[readOutBuf1.length+2];
            ByteBuffer bufcrc = ByteBuffer.allocateDirect(readOutBuf1.length);
            bufcrc.order(ByteOrder.LITTLE_ENDIAN);
            bufcrc.put(readOutBuf1);
            bufcrc.rewind();
            bufcrc.get(buf,0,readOutBuf1.length);
            MainActivity.getInstance().mDialog.dismiss();
//            Log.d("zl",CodeFormat.byteToHex(buf,buf.length));
            if(CodeFormat.crcencode(buf)==0)
            {
                ToastUtils.showToast(MainActivity.getInstance(),getString(R.string.Time_set_result)+" success");
            }
            else
            {
                ToastUtils.showToast(MainActivity.getInstance(),getString(R.string.Time_set_result)+" error");
            }

            return;
        }
        if(mIsatart==false)
        {
            return;
        }
        String temp;
        int tempint;
        int  i=0;
        if(readOutBuf1.length<5)
        {
            ToastUtils.showToast(getActivity(), getString(R.string.receive_data_too_short));
            return;
        }
        else
        {
            if(readOutBuf1[3]!=(readOutBuf1.length-5))
            {
                ToastUtils.showToast(getActivity(), getString(R.string.receive_data_lenth_error));
                return;
            }
        }
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(2);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(readOutBuf1,14,2);
        byteBuffer.rewind();
        short addr = byteBuffer.getShort();
//        byte addr= readOutBuf1[14];//(byte) (Integer.valueOf(baseinfo[mIndexcmd][0])%0x100);
//        Log.d("zl","STUVISettingFragment->OndataCometoParse addr = "+addr);
        if(mIndexcmd>=baseinfo.length)
        {
            Log.d("zl","接收任务完成" );
            return;
        }
        if(Integer.valueOf(baseinfo[mIndexcmd][2])==1)
        {
            for(i=0;i<registerinfosel.length;i++)
            {
                byte bytetemp= (byte) (Integer.valueOf(registerinfosel[i][0])%0x100);
                if(addr==(short)(bytetemp&0xff))
                {
                    tempint=(0x000000ff&readOutBuf1[15])*0x100+(0x000000ff&readOutBuf1[16]);
                    if(tempint==Integer.valueOf(registerinfosel[i][2]))
                    {
                        mUpLoadTypeTX.setText(registerinfosel[i][1]);
                        if(Integer.valueOf(baseinfo[mIndexcmd][0])==208)
                        {
                            Reg208type=tempint;
                        }
                        break;
                    }
                }
            }
        }
        else if(addr == 105)
        {
            StringBuilder temp1=new StringBuilder();;
            temp1.append(String.format("%x-%x-%x %x %x:%x:%x ", readOutBuf1[16],readOutBuf1[17],readOutBuf1[18],readOutBuf1[19]
                    ,readOutBuf1[20],readOutBuf1[21],readOutBuf1[22]));
            mDeviceTimeTX.setText(temp1.toString());
        }
        else if(addr == 201)
        {
            byte buf[]=new byte[Integer.valueOf(baseinfo[mIndexcmd][2])-2];
            byteBuffer = ByteBuffer.allocateDirect(Integer.valueOf(baseinfo[mIndexcmd][2])-2);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.put(readOutBuf1,16,Integer.valueOf(baseinfo[mIndexcmd][2])-2);
            byteBuffer.rewind();
            byteBuffer.get(buf);
            String APN;
            Log.d("zl","STUVISettingFragment->OndataCometoParse buf = "
                    +CodeFormat.bytesToHexString(buf));
            APN="";
            for(byte bytemem:buf)
            {
                if(bytemem!=0x00)
                    APN+=(char)bytemem;
            }
            int index=-1;
            index = APN.indexOf(',');
            if(index!=-1)
            {
                mAPNTX.setText(APN.substring(0,index));
                APN = APN.substring(index+1);
                index = APN.indexOf(',');
                if(index!=-1)
                {
                    mApnUsernameTX.setText(APN.substring(0,index));
                    mApnPasswordTX.setText(APN.substring(index+1));
                }
            }
        }
        else if(addr == 202)
        {
            temp=String.format("%d.%d.%d.%d",0x000000ff&readOutBuf1[16],0x000000ff&readOutBuf1[17]
                    ,0x000000ff&readOutBuf1[18],0x000000ff&readOutBuf1[19]);
            tempint=(0x000000ff&readOutBuf1[20])+(0x000000ff&readOutBuf1[21])*0x100;
           // temp=temp+tempint;
           // settingscontent[mIndexcmd]=temp;
            mMainStationIPTX.setText(temp);
            mMainStationPortTX.setText(""+tempint);
        }
        else if(addr == 209)
        {
            if(Reg208type == 0)
            {
                tempint=(0x000000ff&readOutBuf1[16])+(0x000000ff&readOutBuf1[17])*0x100;
                temp=""+tempint;
                mUploadValueTX.setText(temp);
            }
        }
        else if(addr == 210)
        {
            if(Reg208type == 2)
            {
                String daytime="";
                String lockdaytime1=ArrayFormatCString(0x000000ff&readOutBuf1[16],0x000000ff&readOutBuf1[17]
                        ,0x000000ff&readOutBuf1[18]);
                if(lockdaytime1.equals("")==false)
                {
                    daytime+=lockdaytime1+";";
                }
                String lockdaytime2=ArrayFormatCString(0x000000ff&readOutBuf1[19],0x000000ff&readOutBuf1[20],
                        0x000000ff&readOutBuf1[21]);
                if (lockdaytime2.equals("")==false)
                {
                    daytime+=lockdaytime2+";";
                }
                String lockdaytime3=ArrayFormatCString(0x000000ff&readOutBuf1[22],0x000000ff&readOutBuf1[23],
                        0x000000ff&readOutBuf1[24]);
                if (lockdaytime3.equals("")==false)
                {
                    daytime+=lockdaytime3+";";
                }
                String lockdaytime4=ArrayFormatCString(0x000000ff&readOutBuf1[25],0x000000ff&readOutBuf1[26],
                        0x000000ff&readOutBuf1[27]);
                if (lockdaytime4.equals("")==false)
                {
                    daytime+=lockdaytime4+";";
                }
                mUploadValueTX.setText(daytime);
            }
        }
        else
        {
            temp="";
            for(i=0;i<readOutBuf1.length-18;i++)
            {
                if(readOutBuf1[i+16]==0)
                {
                    break;
                }
                temp+=(char)readOutBuf1[16+i];
            }
            if(addr == 49) {
                mDeviceinfoTX.setText(""+temp);
            } else if(addr == 103){
                mDeviceserialnumberTX.setText(""+temp);
            }else if(addr == 3){
                mDeviceSoftVerTX.setText(""+temp);
            }
        }

        updatecmdlist();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mIsatart=false;
        if (mView != null) {
            // 防止多次new出片段对象，造成图片错乱问题
            return mView;
        }
        thisinflater =inflater;
        thiscontainer=container;
        mView = inflater.inflate(R.layout.stu_vi_stusetting_fragment, container, false);
        initdata();
        initView();
//        initdata();
        return  mView;
    }

    private void initdata() {
        mIsWiteCmdRespone = false;
        baseinfo = new String[][]
                {

                        {"49",getString(R.string.device_describe),"40","R"},
                        {"3",getString(R.string.device_firmware),"4","R"},
                        {"103",getString(R.string.device_usersID),"8","T"},
                        {"105",getString(R.string.device_local_time),"7","R"},
//                    {"198",getString(R.string.device_wireless_module),"1","L"},
                        {"201",getString(R.string.device_connect_parameter),"40","E"},
                        {"202",getString(R.string.device_connect_station),"6","T"},
                        {"208",getString(R.string.device_report_type),"1","L"},
                        {"209",getString(R.string.device_report_type),"2","T"},
                        {"210",getString(R.string.device_report_type),"12","T"},

                };
        registerinfosel= new String[][]
                {
                        {"208",getString(R.string.device_report_freq),"0"},
                        {"208",getString(R.string.device_report_firm_time),"2"},
                };
        senddatabuf=new byte[baseinfo.length][18];
    }
    private void initView() {
        mDeviceinfoTX = mView.findViewById(R.id.stu_setting_product_module_value);
        mDeviceSoftVerTX = mView.findViewById(R.id.stu_setting_device_firmware_value);
        mDeviceTimeTX = mView.findViewById(R.id.stu_setting_local_time_value);
        mDeviceserialnumberTX= mView.findViewById(R.id.stu_setting_user_id_value);
        mAPNTX = mView.findViewById(R.id.stu_setting_apn_value);
        mApnUsernameTX = mView.findViewById(R.id.stu_setting_apn_user_value);
        mApnPasswordTX = mView.findViewById(R.id.stu_setting_apn_password_value);
        mMainStationIPTX = mView.findViewById(R.id.stu_setting_station_ip_value);
        mMainStationPortTX= mView.findViewById(R.id.stu_setting_station_port_value);
        mUpLoadTypeTX = mView.findViewById(R.id.stu_setting_report_type_value);
        mUploadValueTX = mView.findViewById(R.id.stu_setting_report_value);
        mybut =  mView.findViewById(R.id.stu_setting_action_read);

        mClickLocaltime = mView.findViewById(R.id.stu_setting_local_time_item);
        mClickUserID = mView.findViewById(R.id.stu_setting_user_id_item);
        mClickConnectParameter = mView.findViewById(R.id.stu_connect_parameter_id_item);
        mClickupload = mView.findViewById(R.id.stu_setting_report_type_item);

        mybut.setOnClickListener(new butonclicklistener());

        mClickLocaltime.setOnClickListener(new Itemclickedimplement());
        mClickUserID.setOnClickListener(new Itemclickedimplement());
        mClickConnectParameter.setOnClickListener(new Itemclickedimplement());
        mClickupload.setOnClickListener(new Itemclickedimplement());

    }

    private class butonclicklistener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            Readdeviceinfo();
        }
    }

    private void verycutstatus(String readOutMsg) {
        MainActivity parentActivity1 = (MainActivity) getActivity();
        String strState1 = parentActivity1.GetStateConnect();
        if(!strState1.equalsIgnoreCase("无连接"))
        {
            parentActivity1.mDialog.show();
            parentActivity1.mDialog.setDlgMsg("读取中...");
            //String input1 = Constants.Cmd_Read_Alarm_Pressure;
            parentActivity1.sendData(readOutMsg, "FFFF");
        }
        else
        {
            ToastUtils.showToast(getActivity(), "请先建立蓝牙连接!");
        }
    }
    private void verycutstatus(String readOutMsg,int timeout) {
        MainActivity parentActivity1 = (MainActivity) getActivity();
        String strState1 = parentActivity1.GetStateConnect();
        if(!strState1.equalsIgnoreCase("无连接"))
        {
            parentActivity1.mDialog.show();
            parentActivity1.mDialog.setDlgMsg("读取中...");
            //String input1 = Constants.Cmd_Read_Alarm_Pressure;
            parentActivity1.sendData(readOutMsg, "FFFF",timeout);
        }
        else
        {
            ToastUtils.showToast(getActivity(), "请先建立蓝牙连接!");
        }
    }

    private String ArrayFormatCString(int week, int hour, int minute) {

        String temp="";
        if(hour==0xff||minute==0xff)
            return "";
        String strhourminute;
        strhourminute=String.format("%02d:%02d",hour,minute);
        return strhourminute;
    }
    private void updatecmdlist() {
        mIndexcmd++;
        if (mIndexcmd == senddatabuf.length) {
//            myadpater.notifyDataSetChanged();
            MainActivity.getInstance().mDialog.dismiss();
        }
//        else
//        {
//            mytimer.start();
//        }
        if (mIndexcmd < senddatabuf.length) {
            String readOutMsg = DigitalTrans.byte2hex(senddatabuf[mIndexcmd]);
            verycutstatus(readOutMsg, 0);
        }
    }
    private class Itemclickedimplement implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent serverIntent;
            int i=0;
            int id=v.getId();
            if(id == R.id.stu_setting_local_time_item)
            {
                Calendar date1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
                byte sendbuf[]=new byte[18+7];
                sendbuf[0]= (byte) 0xFD;
                sendbuf[3]= (byte) ((7+13)%0x100);
                sendbuf[5]=0x15;
                sendbuf[14]= (byte) (Integer.valueOf(baseinfo[3][0])%0x100);
                int tempint =  date1.get(Calendar.YEAR);
                ByteBuffer buf2 = ByteBuffer.allocateDirect(4);
                buf2.order(ByteOrder.LITTLE_ENDIAN);
                buf2.putInt(tempint-2000);
                buf2.rewind();
                buf2.get(sendbuf,16,1);

                tempint =  date1.get(Calendar.MONTH);
                buf2 = ByteBuffer.allocateDirect(4);
                buf2.order(ByteOrder.LITTLE_ENDIAN);
                buf2.putInt(tempint+1);
                buf2.rewind();
                buf2.get(sendbuf,17,1);

                tempint =  date1.get(Calendar.DATE);
                buf2 = ByteBuffer.allocateDirect(4);
                buf2.order(ByteOrder.LITTLE_ENDIAN);
                buf2.putInt(tempint);
                buf2.rewind();
                buf2.get(sendbuf,18,1);

                tempint =  date1.get(Calendar.DAY_OF_WEEK);
                buf2 = ByteBuffer.allocateDirect(4);
                buf2.order(ByteOrder.LITTLE_ENDIAN);
                buf2.putInt(tempint);
                buf2.rewind();
                buf2.get(sendbuf,19,1);

                tempint =  date1.get(Calendar.HOUR_OF_DAY);
                buf2 = ByteBuffer.allocateDirect(4);
                buf2.order(ByteOrder.LITTLE_ENDIAN);
                buf2.putInt(tempint);
                buf2.rewind();
                buf2.get(sendbuf,20,1);

                tempint =  date1.get(Calendar.MINUTE);
                buf2 = ByteBuffer.allocateDirect(4);
                buf2.order(ByteOrder.LITTLE_ENDIAN);
                buf2.putInt(tempint);
                buf2.rewind();
                buf2.get(sendbuf,21,1);

                tempint =  date1.get(Calendar.SECOND);
                buf2 = ByteBuffer.allocateDirect(4);
                buf2.order(ByteOrder.LITTLE_ENDIAN);
                buf2.putInt(tempint);
                buf2.rewind();
                buf2.get(sendbuf,22,1);

                for(i=0;i<7;i++)
                {
                    sendbuf[16+i] = CodeFormat.HEX2BCD(sendbuf[16+i]);
                }
                CodeFormat.crcencode(sendbuf);
                String readOutMsg = DigitalTrans.byte2hex(sendbuf);
                verycutstatus(readOutMsg);
//                Log.d("zl",CodeFormat.byteToHex(sendbuf,sendbuf.length).toUpperCase());
                mIsWiteCmdRespone = true;
            }
            else
            {
                serverIntent = new Intent(MainActivity.getInstance(), StuViDeviceItemSettingActivity.class);
                int[] addrs;
                ArrayList setvalue;
                switch(id)
                {
                    case R.id.stu_setting_user_id_item:
                         addrs = new int[1];
                        addrs[0] = Integer.valueOf(baseinfo[2][0]);
                       // String setvalue[]=new String[1];
                        setvalue =new ArrayList();
                        setvalue.add( mDeviceserialnumberTX.getText().toString());
                        serverIntent.putExtra("Regadrr",addrs);
                        serverIntent.putCharSequenceArrayListExtra("RegSettingValue",setvalue);
                        break;
                    case R.id.stu_connect_parameter_id_item:
                         addrs = new int[2];
                        addrs[0]= Integer.valueOf(baseinfo[4][0]);
                        addrs[1]= Integer.valueOf(baseinfo[5][0]);

                        setvalue =new ArrayList();
                        setvalue.add( mAPNTX.getText().toString());
                        setvalue.add( mApnUsernameTX.getText().toString());
                        setvalue.add( mApnPasswordTX.getText().toString());
                        setvalue.add( mMainStationIPTX.getText().toString());
                        setvalue.add( mMainStationPortTX.getText().toString());

                        serverIntent.putExtra("Regadrr",addrs);
                        serverIntent.putCharSequenceArrayListExtra("RegSettingValue",setvalue);
                        break;
                    case R.id.stu_setting_report_type_item:
                        addrs = new int[3];
                        addrs[0]= Integer.valueOf(baseinfo[6][0]);
                        addrs[1]= Integer.valueOf(baseinfo[7][0]);
                        addrs[2]= Integer.valueOf(baseinfo[8][0]);

                        setvalue =new ArrayList();
                        setvalue.add( mUpLoadTypeTX.getText().toString());
                        setvalue.add( mUploadValueTX.getText().toString());
//                        setvalue.add( mApnPasswordTX.getText().toString());

                        serverIntent.putCharSequenceArrayListExtra("RegSettingValue",setvalue);
                        serverIntent.putExtra("Regadrr",addrs);
                        break;
                    default:
                        break;
                }
                startActivityForResult(serverIntent, Constants.LocalsetingFlag);
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1)
        {
            ArrayList<String> valuestr = data.getStringArrayListExtra("returnregvalue");
           switch (data.getIntExtra("ReturnReg",-1))
           {
               case 103:
                   mDeviceserialnumberTX.setText(valuestr.get(0));
                   break;
               case 201:
                   mAPNTX .setText(valuestr.get(0));
                   mApnUsernameTX .setText(valuestr.get(1));
                   mApnPasswordTX .setText(valuestr.get(2));
                   mMainStationIPTX .setText(valuestr.get(3));
                   mMainStationPortTX .setText(valuestr.get(4));
                   break;
               case 208:
                   mUpLoadTypeTX.setText(valuestr.get(0));
                   mUploadValueTX.setText(valuestr.get(1));
                   break;
                   default:
                       break;
           }
        }
    }
    private void Readdeviceinfo()
    {
        int index=0;
        int i;
        int tempint;
        byte[] adsinf0;//={1,3,105, (byte) 0xC7};
        mIsatart=true;
        adsinf0=new byte[baseinfo.length];
        for(i=0;i<adsinf0.length;i++)
        {
            tempint=Integer.valueOf(baseinfo[i][0]);
            adsinf0[i]= (byte) (tempint%0x100);
        }
        mIndexcmd=0;
        for(int j=0;j<adsinf0.length;j++)
        {
            senddatabuf[j][index++]= (byte) 0xfd;
            senddatabuf[j][index++]= (byte) 0x00;
            senddatabuf[j][index++]= (byte) 0x00;
            senddatabuf[j][index++]= 13;
            senddatabuf[j][index++]= (byte) 0x00;
            senddatabuf[j][index++]= (byte) 0x19;
            for(i=0;i<8;i++)
            {
                senddatabuf[j][index++]= (byte) 0x00;
            }
            senddatabuf[j][index++]= adsinf0[j];
            senddatabuf[j][index++]= (byte) 0x00;
            CodeFormat.crcencode(senddatabuf[j]);
            index=0;
        }
        if(mIndexcmd<adsinf0.length)
        {
            String readOutMsg = DigitalTrans.byte2hex(senddatabuf[mIndexcmd]);
            verycutstatus(readOutMsg);
        }
    }

}
