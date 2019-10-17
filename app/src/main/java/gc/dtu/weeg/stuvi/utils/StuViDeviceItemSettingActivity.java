package gc.dtu.weeg.stuvi.utils;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gc.dtu.weeg.stuvi.MainActivity;
import gc.dtu.weeg.stuvi.R;
import gc.dtu.weeg.stuvi.fregment.STUVISettingFragment;
import gc.dtu.weeg.stuvi.myview.CustomDialog;
import gc.dtu.weeg.stuvi.myview.LocalSetaddr219ExtraInfoView;
import gc.dtu.weeg.stuvi.myview.slidingbutton.BaseSlidingToggleButton;
import gc.dtu.weeg.stuvi.myview.slidingbutton.SlidingToggleButton;

import static gc.dtu.weeg.stuvi.fregment.STUVISettingFragment.baseinfo;
import static gc.dtu.weeg.stuvi.fregment.STUVISettingFragment.registerinfosel;


public class StuViDeviceItemSettingActivity extends Activity {
//ImageView mbluetooth;
    RelativeLayout mSpinerconter;
    RelativeLayout mConters[] =new RelativeLayout[5];
    LinearLayout uploadvalueconter;
    TimePickerView pvTime;

    int mClickViewid;
    SlidingToggleButton mTimeset1;
    SlidingToggleButton mTimeset2;
    SlidingToggleButton mTimeset3;
    SlidingToggleButton mTimeset4;

    ArrayList<SlidingToggleButton> mslidebuts=new ArrayList<>();

    TextView mstart1;
    TextView mend1;
    TextView mstart2;
    TextView mend2;
    TextView mstart3;
    TextView mend3;
    TextView mstart4;
    TextView mend4;

    TextView timeviews[] = new TextView[4];

    Spinner mSpiner;
    TextView mTextlabes[] =new TextView[5];
    TextView Tiltetx ;

    EditText mTextSetValues[] =new EditText[5];


    ArrayList<String> m_setings;

    String[] mConnectparameters;
//    String[] mUploadparameters;
    int mRegs[];

    Button but;
    ArrayList<byte[]> sendbufs = new ArrayList<>();

    int spinercurselectvalue;
    public CustomDialog mDialog;

    boolean misStart;
    int mCurCMD;

MainActivity mainActivity = MainActivity.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stu_i_device_item_setting);
        initview();
    }

    private void initview() {
        initdata();
        initctrls();
        initshow();
        misStart = false;
//        mbluetooth = findViewById(R.id.blustoothbut);
//        mbluetooth.setOnClickListener(new OnclickListererImp());
       // ToastUtils(StuViDeviceItemSettingActivity.this ,mainActivity.GetStateConnect());
    }

    private void initdata() {
          Intent intent =  getIntent();
          if(intent!=null)
          {
              mRegs = intent.getIntArrayExtra("Regadrr");
              m_setings = intent.getStringArrayListExtra("RegSettingValue");
          }
        mConnectparameters=new String[5];
        mConnectparameters[0]=getString(R.string.device_apn);
        mConnectparameters[1]=getString(R.string.device_apn_username);
        mConnectparameters[2]=getString(R.string.device_apn_password);
        mConnectparameters[3]=getString(R.string.device_station_ip);
        mConnectparameters[4]=getString(R.string.device_station_port);

//        mUploadparameters = new
    }

    private void initctrls() {
        int i=0;

        mstart1=findViewById(R.id.local_219_starttime1);
        mstart1.setOnClickListener(new OnTextViewClicklinenterimpl());
        mend1 = findViewById(R.id.local_219_endtime1);
        mend1.setOnClickListener(new OnTextViewClicklinenterimpl());

        mstart2=findViewById(R.id.local_219_starttime2);
        mstart2.setOnClickListener(new OnTextViewClicklinenterimpl());
        mend2 = findViewById(R.id.local_219_endtime2);
        mend2.setOnClickListener(new OnTextViewClicklinenterimpl());

        mstart3=findViewById(R.id.local_219_starttime3);
        mstart3.setOnClickListener(new OnTextViewClicklinenterimpl());
        mend3 = findViewById(R.id.local_219_endtime3);
        mend3.setOnClickListener(new OnTextViewClicklinenterimpl());

        mstart4=findViewById(R.id.local_219_starttime4);
        mstart4.setOnClickListener(new OnTextViewClicklinenterimpl());
        mend4 = findViewById(R.id.local_219_endtime4);
        mend4.setOnClickListener(new OnTextViewClicklinenterimpl());

        timeviews[0]=mstart1;
        timeviews[1]=mstart2;
        timeviews[2]=mstart3;
        timeviews[3]=mstart4;


        mTimeset1 = findViewById(R.id.local_219_checkbut_1);
        mTimeset2 = findViewById(R.id.local_219_checkbut_2);
        mTimeset3 = findViewById(R.id.local_219_checkbut_3);
        mTimeset4 = findViewById(R.id.local_219_checkbut_4);

        mslidebuts.add(mTimeset1);
        mslidebuts.add(mTimeset2);
        mslidebuts.add(mTimeset3);
        mslidebuts.add(mTimeset4);

        mSpinerconter = findViewById(R.id.stu_i_selectitemspiner);
        mConters[0] = findViewById(R.id.stu_i_setting_item1);
        mConters[1] = findViewById(R.id.stu_i_setting_item2);
        mConters[2] = findViewById(R.id.stu_i_setting_item3);
        mConters[3] = findViewById(R.id.stu_i_setting_item4);
        mConters[4] = findViewById(R.id.stu_i_setting_item5);

        mSpiner = findViewById(R.id.stu_i_item_spiner);

        mTextlabes[0] = findViewById(R.id.stu_i_setting_item_label1);
        mTextlabes[1] = findViewById(R.id.stu_i_setting_item_label2);
        mTextlabes[2] = findViewById(R.id.stu_i_setting_item_label3);
        mTextlabes[3] = findViewById(R.id.stu_i_setting_item_label4);
        mTextlabes[4] = findViewById(R.id.stu_i_setting_item_label5);

        Tiltetx  = findViewById(R.id.txt_titles);

        mTextSetValues[0] = findViewById(R.id.stu_i_setting_item_value1);
        mTextSetValues[1] = findViewById(R.id.stu_i_setting_item_value2);
        mTextSetValues[2] = findViewById(R.id.stu_i_setting_item_value3);
        mTextSetValues[3] = findViewById(R.id.stu_i_setting_item_value4);
        mTextSetValues[4] = findViewById(R.id.stu_i_setting_item_value5);
        uploadvalueconter = findViewById(R.id.local_219_detail_view);
        mTimeset1.setOnCheckedChanageListener(new OnSlidebuttonCkeckedchangedlistenerImpl());
        mTimeset2.setOnCheckedChanageListener(new OnSlidebuttonCkeckedchangedlistenerImpl());
        mTimeset3.setOnCheckedChanageListener(new OnSlidebuttonCkeckedchangedlistenerImpl());
        mTimeset4.setOnCheckedChanageListener(new OnSlidebuttonCkeckedchangedlistenerImpl());



        but = findViewById(R.id.tv_itemsettings_btn_write);
        but.setOnClickListener(new Onclickedsenfbuf());
        Calendar date1 = Calendar.getInstance();
        date1.set(Calendar.HOUR_OF_DAY,1);
        date1.set(Calendar.MINUTE,55);
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                //  Toast.makeText(mActivity, getTime(date), Toast.LENGTH_SHORT).show();
                String temp=""+date.getHours()+":"+date.getMinutes();
                TextView selectview;
                selectview=findViewById(mClickViewid);
                selectview.setText(temp);
                switch(mClickViewid)
                {
                    case R.id.local_219_starttime1:
//                        settings[2]=(byte)(date.getHours()%0x100);
//                        settings[3]=(byte)(date.getMinutes()%0x100);
                        break;
                    case R.id.local_219_endtime1:
//                        settings[4]=(byte)(date.getHours()%0x100);
//                        settings[5]=(byte)(date.getMinutes()%0x100);
                        break;
                    case R.id.local_219_starttime2:
//                        settings[6]=(byte)(date.getHours()%0x100);
//                        settings[7]=(byte)(date.getMinutes()%0x100);
                        break;
                    case R.id.local_219_endtime2:
//                        settings[8]=(byte)(date.getHours()%0x100);
//                        settings[9]=(byte)(date.getMinutes()%0x100);
                        break;
                    case R.id.local_219_starttime3:
//                        settings[10]=(byte)(date.getHours()%0x100);
//                        settings[11]=(byte)(date.getMinutes()%0x100);
                        break;
                    case R.id.local_219_endtime3:
//                        settings[12]=(byte)(date.getHours()%0x100);
//                        settings[13]=(byte)(date.getMinutes()%0x100);
                        break;
                    case R.id.local_219_starttime4:
//                        settings[10]=(byte)(date.getHours()%0x100);
//                        settings[11]=(byte)(date.getMinutes()%0x100);
                        break;
                    case R.id.local_219_endtime4:
//                        settings[12]=(byte)(date.getHours()%0x100);
//                        settings[13]=(byte)(date.getMinutes()%0x100);
                        break;
                    default:
                        break;
                }
//                Log.d("zl","call update in onTimeSelect"
//                        + CodeFormat.byteToHex(settings,settings.length));
              //  updatesetting();
            }
        })
                .setDate(date1)
                .setType(new boolean[]{false,false,false,true,true,false})
                .setLabel(null,null,null,"时","分",null)
                .build();
        Log.d("zl",""+mRegs[0]);
        if(mRegs[0] == 208)
        {
            Tiltetx.setText(R.string.device_report_type);
            mSpinerconter.setVisibility(View.VISIBLE);
            for(i=0;i<4;i++)
            {
                mTextlabes[1+i].setVisibility(View.GONE);
            }
            spinercurselectvalue = initspiner();
            if(spinercurselectvalue ==1)
            {
//                spinercurselectvalue = 1;
                uploadvalueconter.setVisibility(View.VISIBLE);
                for(i=0;i<mConters.length;i++)
                    mConters[i].setVisibility(View.GONE);
                String temp = m_setings.get(1);
                int index = temp.indexOf(';');
                mTimeset1.setChecked(false);
                mTimeset1.setChecked(false);
                mTimeset1.setChecked(false);
                mTimeset1.setChecked(false);
                if(index!=-1)
                {
                    mstart1.setText(temp.substring(0,index));
                    temp = temp.substring(index+1);
                    mTimeset1.setChecked(true);

                    index = temp.indexOf(';');
                    if(index!=-1)
                    {
                        mstart2.setText(temp.substring(0,index));
                        temp = temp.substring(index+1);
                        mTimeset2.setChecked(true);
                        index = temp.indexOf(';');
                        if(index!=-1)
                        {
                            mstart3.setText(temp.substring(0,index));
                            temp = temp.substring(index+1);
                            mTimeset3.setChecked(true);

                            index = temp.indexOf(';');
                            if(index!=-1)
                            {
                                mstart4.setText(temp.substring(0,index));
                                mTimeset4.setChecked(true);
                            }
                        }
                    }
                }
            }
            else
            {
                uploadvalueconter.setVisibility(View.GONE);
                mConters[0].setVisibility(View.VISIBLE);
                mTextlabes[0].setText("");
//                spinercurselectvalue =0;
            }
        }
        else
        {
            mSpinerconter.setVisibility(View.GONE);
            uploadvalueconter.setVisibility(View.GONE);
        }

        if(mRegs[0]==103)
        {
            Tiltetx.setText(R.string.device_usersID);
            for(TextView tx:mTextlabes)
            {
                tx.setVisibility(View.GONE);
            }
            for(EditText ex:mTextSetValues)
            {
                ex.setVisibility(View.GONE);
            }
            for(i=0;i<5;i++)
            {
                mConters[i].setVisibility(View.GONE);
            }
            mConters[0].setVisibility(View.VISIBLE);
            mTextSetValues[0].setVisibility(View.VISIBLE);
            mTextSetValues[0].setText(m_setings.get(0));
        }
        else if(mRegs[0]==201)
        {
            Tiltetx.setText(R.string.device_connect_parameter);
            for(i=0;i<5;i++)
            {
                mConters[i].setVisibility(View.VISIBLE);
            }
            for(i=0;i<mTextlabes.length;i++)
            {
                mTextlabes[i].setText(mConnectparameters[i]);
                mTextlabes[i].setVisibility(View.VISIBLE);
                mTextSetValues[i].setVisibility(View.VISIBLE);
                mTextSetValues[i].setText(m_setings.get(i));
            }
        }
        else
        {

        }
      //  mainActivity.setOndataparse(new StuViDeviceItemSettingActivity().this.datacometoparse());
        MainActivity.getInstance().setOndataparse(new datacometoparse());
    }

    private int  initspiner() {
        int resulevalue=-1;
        int i;
        ArrayList data_list;
        data_list= new ArrayList<>();
        String spineritems[][] = registerinfosel;
        for(i=0;i<spineritems.length ; i++)
        {
            data_list.add(spineritems[i][1]);
        }
        ArrayAdapter<String> arr_adapter;
        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        mSpiner.setAdapter(arr_adapter);
//            spinner.setSelection(-1,true);
        for(i=0;i<spineritems.length;i++)
        {
            if(m_setings.get(0).equals(spineritems[i][1]))
            {
                mSpiner.setSelection(i);
                resulevalue = i;
                break;
            }
        }
        mSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1)
                {
                    uploadvalueconter.setVisibility(View.VISIBLE);
                    mConters[0].setVisibility(View.GONE);
                }
                else
                {
                    uploadvalueconter.setVisibility(View.GONE);
                    mConters[0].setVisibility(View.VISIBLE);
                }
                spinercurselectvalue = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return resulevalue;
    }

    private void initshow() {
        mDialog = CustomDialog.createProgressDialog(this, Constants.TimeOutSecond, new CustomDialog.OnTimeOutListener() {
            @Override
            public void onTimeOut(CustomDialog dialog) {
                dialog.dismiss();
                ToastUtils.showToast(getBaseContext(), getString(R.string.timeout));
            }
        });

    }
//    public class OnclickListererImp implements View.OnClickListener
//    {
//
//        @Override
//        public void onClick(View v) {
//            switch(v.getId())
//            {
//                case R.id.blustoothbut:// 蓝牙扫描
//                    if(!mainActivity.mIsconnect)
//                    {
//                        Intent serverIntent = new Intent(StuViDeviceItemSettingActivity.this, DeviceListActivity.class);
//                        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
//                    }
//                    else
//                    {
//                        // 关闭连接socket
//                        try {
//                            // 关闭蓝牙
//                          //  mTxtStatus.setText(R.string.title_not_connected);
//                            mainActivity.mBTService.stop();
//                        } catch (Exception e) {
//                        }
//                    }
//                    Toast.makeText(StuViDeviceItemSettingActivity.this
//                            ,mainActivity.GetStateConnect()
//                            ,Toast.LENGTH_SHORT
//                    ).show();
//                    break;
//                default:
//                    break;
//            }
//
//        }
//    }

    class OnTextViewClicklinenterimpl implements View.OnClickListener
    {

        @Override
        public void onClick(View view) {
            mClickViewid = view.getId();
            pvTime.show();
        }
    }

    class OnSlidebuttonCkeckedchangedlistenerImpl implements SlidingToggleButton.OnCheckedChanageListener
    {

        @Override
        public void onCheckedChanage(BaseSlidingToggleButton slidingToggleButton, boolean isChecked) {
            int id = slidingToggleButton.getId();
            int i=0;
//            if(mFlaginitend==false)
//            {
//                return;
//            }
            switch (id)
            {
                case R.id.local_219_checkbut_1:

                    mstart1.setEnabled(isChecked);
                    mend1.setEnabled(isChecked);
//                    setslidebuttonpremeter(isChecked,mstart1,mend1,2);
                    break;
                case R.id.local_219_checkbut_2:

                    mstart2.setEnabled(isChecked);
                    mend2.setEnabled(isChecked);
//                    setslidebuttonpremeter(isChecked,mstart2,mend2,6);
                    break;
                case R.id.local_219_checkbut_3:
                    mstart3.setEnabled(isChecked);
                    mend3.setEnabled(isChecked);
//                    setslidebuttonpremeter(isChecked,mstart3,mend3,10);
                    break;
                case R.id.local_219_checkbut_4:
                    mstart4.setEnabled(isChecked);
//                    mend3.setEnabled(isChecked);
                    break;
            }
//            Log.d("zl","call updat in onCheckedChanage \n"
//                    + CodeFormat.byteToHex(settings,settings.length));
//            updatesetting();
        }
    }

    private class Onclickedsenfbuf implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            byte[] crusetbyte;
            sendbufs.clear();
            int j=0;
            byte[] sendbuf;
            int[] datalen=new int[mRegs.length];
            int i=-1;
            for(j=0;j<mRegs.length;j++)
            {
                for(i=0;i<baseinfo.length;i++)
                {
                    if(mRegs[j] == Integer.valueOf(baseinfo[i][0]))
                    {
                        datalen[j]=Integer.valueOf(baseinfo[i][2]);
                        break;
                    }
                }
            }

            switch (mRegs[0])
            {
                case 103:
                    sendbuf=new byte[datalen[0] +18];
                    sendbuf[0]= (byte) 0xFD;
                    sendbuf[3]= (byte) ((datalen[0]+13)%0x100);
                    sendbuf[5]=0x15;
                    sendbuf[14]= (byte) ((mRegs[0])%0x100);
                    crusetbyte = mTextSetValues[0].getText().toString().getBytes();
                    if(crusetbyte.length>datalen[0])
                    {
                        String temp = getString(R.string.iuput_lenth_error);
                        Toast.makeText(StuViDeviceItemSettingActivity.this,temp,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for( i=0;i<datalen[0];i++)
                    {
                        if(i<crusetbyte.length)
                        {
                            sendbuf[16+i]=crusetbyte[i];
                        }
                        else
                            sendbuf[16+i]=(byte)0x00;
                    }
                    CodeFormat.crcencode(sendbuf);
                    sendbufs.add(sendbuf);
                    break;
                case 201:
                    for(i=0;i<mRegs.length;i++)
                    {
                        if(mRegs[i]==201)
                        {
                            sendbuf=new byte[datalen[i] +18];
                            sendbuf[0]= (byte) 0xFD;
                            sendbuf[3]= (byte) ((datalen[0]+13)%0x100);
                            sendbuf[5]=0x15;
                            sendbuf[14]= (byte) ((mRegs[i])%0x100);
                            String APN="";
                            APN=mTextSetValues[0].getText()+","+mTextSetValues[1].getText()+","+mTextSetValues[2].getText();
                            crusetbyte = APN.getBytes();
//                            Log.d("zl",APN);
                            if(crusetbyte.length>datalen[0])
                            {
                                String temp = getString(R.string.iuput_lenth_error);
                                Toast.makeText(StuViDeviceItemSettingActivity.this,temp,Toast.LENGTH_SHORT).show();
                                return;
                            }
                            int lenth= datalen[i];
                         //   int lenth2 =crusetbyte.length;
                            for(j=0;j<lenth;j++)
                            {
                                if(j<crusetbyte.length)
                                {
                                    sendbuf[16+j]=crusetbyte[j];
                                }
                            }
                            CodeFormat.crcencode(sendbuf);
                            sendbufs.add(sendbuf);
                        }
                        else if(mRegs[i]==202)
                        {
                           if(isboolIP(mTextSetValues[3].getText().toString())==false)
                           {
                               String temp = getString(R.string.device_station_ip)+" "+getString(R.string.wrong);
                               Toast.makeText(StuViDeviceItemSettingActivity.this,temp,Toast.LENGTH_SHORT).show();
                               return;
                           }
                            sendbuf=new byte[datalen[i] +18];
                            sendbuf[0]= (byte) 0xFD;
                            sendbuf[3]= (byte) ((datalen[i]+13)%0x100);
                            sendbuf[5]=0x15;
                            sendbuf[14]= (byte) ((mRegs[i])%0x100);

                            String ipmessage=mTextSetValues[3].getText().toString();
                            int index =ipmessage.indexOf('.');
                            sendbuf[16] = (byte) (Integer.valueOf(ipmessage.substring(0,index)).shortValue()%0x100);
                            ipmessage = ipmessage.substring(index+1);

                            index = ipmessage.indexOf('.');
                            sendbuf[17] = (byte) (Integer.valueOf(ipmessage.substring(0,index)).shortValue()%0x100);
                            ipmessage = ipmessage.substring(index+1);

                            index = ipmessage.indexOf('.');
                            sendbuf[18] = (byte) (Integer.valueOf(ipmessage.substring(0,index)).shortValue()%0x100);
                            ipmessage = ipmessage.substring(index+1);

                            sendbuf[19] = (byte) (Integer.valueOf(ipmessage).shortValue()%0x100);
                            String temp;
                            if(isNumeric(mTextSetValues[4].getText().toString())==false)
                            {
                                temp = getString(R.string.device_station_port)+" "+getString(R.string.wrong);
                                Toast.makeText(StuViDeviceItemSettingActivity.this,temp,Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //short portvalue = mTextSetValues[4].getText().toString();
                            temp = mTextSetValues[4].getText().toString();

                            short port = Integer.valueOf(temp).shortValue();

                            ByteBuffer buf = ByteBuffer.allocateDirect(2);
                            buf.order(ByteOrder.LITTLE_ENDIAN);
                            buf.putShort(port);
                            buf.rewind();
                            buf.get(sendbuf,20,2);
                            CodeFormat.crcencode(sendbuf);
                            sendbufs.add(sendbuf);
                        }
                    }
                    break;
                case 208:
                    String temoinfo;
                    String temoinfo1;
                    sendbuf=new byte[datalen[0] +18];
                    sendbuf[0]= (byte) 0xFD;
                    sendbuf[3]= (byte) ((datalen[0]+13)%0x100);
                    sendbuf[5]=0x15;
                    sendbuf[14]= (byte) ((mRegs[0])%0x100);

                    ByteBuffer buf = ByteBuffer.allocateDirect(4);
                    buf.order(ByteOrder.LITTLE_ENDIAN);

                    buf.putInt(Integer.valueOf(registerinfosel[spinercurselectvalue][2]).intValue());
                    buf.rewind();
                    buf.get(sendbuf,16,1);
                    CodeFormat.crcencode(sendbuf);
                    sendbufs.add(sendbuf);
                    if(spinercurselectvalue == 0)
                    {
                        if(isNumeric(mTextSetValues[0].getText().toString())==false)
                        {
                            String temp = getString(R.string.device_report_freq)+" "+getString(R.string.wrong);
                            Toast.makeText(StuViDeviceItemSettingActivity.this,temp,Toast.LENGTH_SHORT).show();
                            return;
                        }
                        sendbuf=new byte[datalen[1] +18];
                        sendbuf[0]= (byte) 0xFD;
                        sendbuf[3]= (byte) ((datalen[1]+13)%0x100);
                        sendbuf[5]=0x15;
                        sendbuf[14]= (byte) ((mRegs[1])%0x100);

                        String ipmessage=mTextSetValues[0].getText().toString();
                        short freq = Integer.valueOf(ipmessage).shortValue();

                        buf = ByteBuffer.allocateDirect(2);
                        buf.order(ByteOrder.LITTLE_ENDIAN);
                        buf.putShort(freq);
                        buf.rewind();
                        buf.get(sendbuf,16,2);
                        CodeFormat.crcencode(sendbuf);
                        sendbufs.add(sendbuf);
                    }
                    else if(spinercurselectvalue == 1)
                    {
                        sendbuf=new byte[datalen[2] +18];
                        sendbuf[0]= (byte) 0xFD;
                        sendbuf[3]= (byte) ((datalen[2]+13)%0x100);
                        sendbuf[5]=0x15;
                        sendbuf[14]= (byte) ((mRegs[2])%0x100);
                   //     byte[] timebyte =new byte[12];
                        for(i=0;i<12;i++)
                        {
                            sendbuf[i+16]= (byte) 0xff;
                        }
                        temoinfo="";
                        ArrayList<String> timeinfo=new ArrayList<>();
                        for(i=0;i<mslidebuts.size();i++)
                        {
                            int index=-1;
                            if(mslidebuts.get(i).isChecked())
                            {
                                temoinfo1 = timeviews[i].getText().toString();
                                timeinfo.add(temoinfo1);
                            }
                        }
                        for(i=0;i<timeinfo.size();i++)
                        {
                            String time1;
                            time1=timeinfo.get(i);
                            j=time1.indexOf(':');
                            sendbuf[i*3+1+16]= Byte.valueOf(time1.substring(0,j));
                            sendbuf[i*3+2+16]= Byte.valueOf(time1.substring(j+1));
                        }
                        CodeFormat.crcencode(sendbuf);
                        sendbufs.add(sendbuf);
                    }
                    break;
                    default:
                        break;
            }

            String readOutMsg = DigitalTrans.byte2hex(sendbufs.get(0));

            Log.d("zl",CodeFormat.byteToHex(sendbufs.get(0),sendbufs.get(0).length));
            misStart = true;
            mCurCMD = 0;
            verycutstatus(readOutMsg);

        }
    }
    private void verycutstatus(String readOutMsg) {
        MainActivity parentActivity1 = StuViDeviceItemSettingActivity .this.mainActivity;
        String strState1 = parentActivity1.GetStateConnect();
        if(!strState1.equalsIgnoreCase(getString(R.string.title_not_connected)))
        {
            StuViDeviceItemSettingActivity.this.mDialog.show();
            StuViDeviceItemSettingActivity.this.mDialog.setDlgMsg(getString(R.string.reading));
            parentActivity1.sendData(readOutMsg, "FFFF");
        }
        else
        {
            ToastUtils.showToast(StuViDeviceItemSettingActivity.this, getString(R.string.not_connected));
        }
    }
    private class datacometoparse  implements MainActivity.Ondataparse
    {

        @Override
        public void datacometoparse(String readOutMsg1, byte[] readOutBuf1) {
//            ItemSetingActivity.this.mDialog.dismiss();
                if(misStart==false)
                {
                    return;
                }
                if(mCurCMD<sendbufs.size())
                {
                    byte[] recvs=new byte[sendbufs.get(mCurCMD).length+2];
                    ByteBuffer buf = ByteBuffer.allocateDirect(sendbufs.get(mCurCMD).length);
                    buf.order(ByteOrder.LITTLE_ENDIAN);
                    buf.put(sendbufs.get(mCurCMD));
                    buf.rewind();
                    buf.get(recvs,0,sendbufs.get(mCurCMD).length);
                    if(CodeFormat.crcencode(recvs)==0)
                    {
                        ToastUtils.showToast(MainActivity.getInstance(),getString(R.string.correct));
                    }
                    else
                    {
                        ToastUtils.showToast(MainActivity.getInstance(),getString(R.string.wrong));
                        return;
                    }
                }
                mCurCMD++;
            if(mCurCMD<sendbufs.size())
            {
                String readOutMsg = DigitalTrans.byte2hex(sendbufs.get(mCurCMD));

                Log.d("zl",CodeFormat.byteToHex(sendbufs.get(mCurCMD),sendbufs.get(mCurCMD).length));
                verycutstatus(readOutMsg);
            }
            else
            {
                mDialog.dismiss();
                StuViDeviceItemSettingActivity.this.setResult(1,getIntent());
                StuViDeviceItemSettingActivity.this.finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.getInstance().setOndataparse(null);
    }
    public static boolean isboolIP(String ipAddress){
        String ip="(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }
    public static boolean isNumeric(String string){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(string).matches();
    }
}
