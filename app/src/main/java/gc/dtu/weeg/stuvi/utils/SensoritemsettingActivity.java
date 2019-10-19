package gc.dtu.weeg.stuvi.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import gc.dtu.weeg.stuvi.MainActivity;
import gc.dtu.weeg.stuvi.R;
import gc.dtu.weeg.stuvi.fregment.GasSensorSetFragment;
import gc.dtu.weeg.stuvi.fregment.SensorInputFregment;
import gc.dtu.weeg.stuvi.myview.CustomDialog;


public class SensoritemsettingActivity extends Activity {
    Intent intent;
    public MainActivity mainActivity;
    RelativeLayout  selectlayout;
    RelativeLayout  anologinputlayout;
    RelativeLayout  mLimitContainer;
    TextView  mtitle;
    TextView  text1;
    TextView  text2;
    Spinner msettings;
    EditText m_range;
    EditText editText1;
    EditText editText2;
    Boolean misRead;
    Boolean misWrite;

    TextView mAnologLableView;

    ImageView Imageback;
    Button   butcommit;
    ArrayList<String> listcontent;
    ArrayList<String> listvalue;
    int m_currentselect=0;
    int m_curposition=-1;
    TextView mLimitinfo;
    public CustomDialog mDialog;
    byte[] sendbufread={(byte) 0xFD, 0x00 ,0x00 ,0x0D ,        0x00 ,0x19 ,0x00 ,        0x00 ,0x00 ,0x00
            ,0x00 ,0x00 ,0x00 ,0x00 , (byte) 0xD9 ,0x00 ,0x0C , (byte) 0xA0};
    ArrayList<Map<String,String>> mSetitemdata=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensoritemsettinglayout);
        intent=getIntent();
        selectlayout=findViewById(R.id.sensor_select_layout);
        anologinputlayout=findViewById(R.id.sensor_anolog_layout);
        text1=findViewById(R.id.sensor_set_item_hight_lable);
        text2=findViewById(R.id.sensor_set_item_low_lable);
        mtitle=findViewById(R.id.Sensor_item_txt_titles);
        msettings= findViewById(R.id.sensor_set_item);
        Imageback=findViewById(R.id.Sensor_imgBackItemset);
        butcommit=findViewById(R.id.buttsensorcommite);
        mainActivity=MainActivity.getInstance();
        m_range=findViewById(R.id.sensor_set_item_manual);
        editText1=findViewById(R.id.sensor_set_item_hight_input);
        editText2=findViewById(R.id.sensor_set_item_low_input);
        mAnologLableView=findViewById(R.id.sensor_set_item_manual_label);
        mLimitinfo = findViewById(R.id.sensor_limit_info);
        mLimitContainer = findViewById(R.id.sensor_limit_info_container);
        initview();

    }

    private void initview() {
            initdialog();
            misRead = false;
            int position=intent.getIntExtra("position",-1);
            m_curposition=position;
            if(position==5)
            {
                mAnologLableView.setText("请输入量程:");
            }
            else
            {
                mAnologLableView.setText("请输入量程:(单位:KPa)");
            }
            String temptitle=intent.getStringExtra("name");
            boolean isfind=false;
            mtitle.setText(temptitle);
            listcontent= new ArrayList<>();
            listvalue= new ArrayList<>();
            String tempcontent=intent.getStringExtra("item1");
            if(position==1||position==2)
            {
                for(int i = 0; i< SensorInputFregment.sensorinfo.length; i++)
                {
                    if(SensorInputFregment.sensorinfo[i][0]=="1")
                    {
                        listcontent.add(SensorInputFregment.sensorinfo[i][1]);
                        listvalue.add(SensorInputFregment.sensorinfo[i][2]);

                        if(tempcontent.equals(SensorInputFregment.sensorinfo[i][1]))
                        {
                            m_currentselect=listvalue.size()-1;
                            isfind=true;
                        }
                    }
                }
                if(isfind==false)
                {
                    if(tempcontent.length()!=0)
                    {
                        m_currentselect=listvalue.size()-1;
                    }
                }

            }
            else if(position==3)
            {
                for(int i=0;i<SensorInputFregment.sensorinfo.length;i++)
                {
                    if(SensorInputFregment.sensorinfo[i][0]=="2")
                    {
                        listcontent.add(SensorInputFregment.sensorinfo[i][1]);
                        listvalue.add(SensorInputFregment.sensorinfo[i][2]);
                        if(tempcontent.equals(SensorInputFregment.sensorinfo[i][1]))
                        {
                            m_currentselect=listvalue.size()-1;
                        }
                    }
                }
            }
            else if(position==5) //燃气报警器参数设置
            {
                for(int i = 0; i< GasSensorSetFragment.gassensorinfo.length; i++)
                {
                    if(GasSensorSetFragment.gassensorinfo[i][0]=="3")
                    {
                        listcontent.add(GasSensorSetFragment.gassensorinfo[i][1]);
                        listvalue.add(GasSensorSetFragment.gassensorinfo[i][2]);

                        if(tempcontent.equals(GasSensorSetFragment.gassensorinfo[i][1]))
                        {
                            m_currentselect=listvalue.size()-1;
                            isfind=true;
                        }
                    }
                }
                if(isfind==false)
                {
                    if(tempcontent.length()!=0)
                    {
                        m_currentselect=listvalue.size()-1;
                    }
                }
            }
        switch (position)
            {
                case 1:
                case 2:
                case 3:
                    selectlayout.setVisibility(View.VISIBLE);
//                    anologinputlayout.setVisibility(View.VISIBLE);
                    text1.setText(R.string.ANALOG_UPPER_LIMITE);
                    text2.setText(R.string.ANALOG_LOWER_LIMITE);
                    m_range.setText(tempcontent);
                    editText1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
                    editText2.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
                    mLimitinfo.setText(getString(R.string.ANALOG_INPUT_LIMITE));
                    mLimitContainer.setVisibility(View.VISIBLE);
                    break;
                case 4:
                case 6:
                    selectlayout.setVisibility(View.GONE);
                    anologinputlayout.setVisibility(View.GONE);
                    text1.setText(R.string.ANALOG_SCAN_FREQUENCE);
                    text2.setText(R.string.ANALOG_Acquisition_FREQUENCE);
                    editText1.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText2.setInputType(InputType.TYPE_CLASS_NUMBER);
                    mLimitContainer.setVisibility(View.GONE);
                    break;
                case 5:
                    selectlayout.setVisibility(View.VISIBLE);
//                    anologinputlayout.setVisibility(View.VISIBLE);
                    text1.setText(R.string.GAS_DETECT_SETTING_LOW_LIMITE);
                    text2.setText(R.string.GAS_DETECT_SETTING_HIGH_LIMITE);
                    m_range.setText(tempcontent);
                    editText1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
                    editText2.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
                    mLimitinfo.setText(getString(R.string.GAS_SENSOR_ALARM_SETTING));
                    mLimitContainer.setVisibility(View.VISIBLE);
                    break;
            }
            editText1.setText(intent.getStringExtra("item2"));
            editText2.setText(intent.getStringExtra("item3"));
        //适配器
        ArrayAdapter<String> arr_adapter;
        arr_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listcontent);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        msettings.setAdapter(arr_adapter);
        //        serverIntent.putExtra("name","第二路压力");
//        serverIntent.putExtra("position",2);
//        serverIntent.putExtra("item1",mpressmode2.getText().toString());
//        serverIntent.putExtra("item2",mPress2H.getText().toString());
//        serverIntent.putExtra("item3",mPress2L.getText().toString());
        msettings.setSelection(m_currentselect,true);
        msettings.setOnItemSelectedListener(new SpinerOnitemselectimp());
        int sizemax=listvalue.size()-1;
        if(position==1||position==2||position==5)
        {
            if(m_currentselect==sizemax)
            {
//                anologinputlayout.setVisibility(View.VISIBLE);
            }
            else
                anologinputlayout.setVisibility(View.GONE);
        }
        else if(position==3)
        {
            anologinputlayout.setVisibility(View.GONE);
        }
        else if(position==4||position==6)
        {
            selectlayout.setVisibility(View.GONE);
        }
        Imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SensoritemsettingActivity.this.finish();
            }
        });
        butcommit.setOnClickListener(new ButtonOnclicklistenerimp());
    }

    private void initdialog() {

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
//                Log.d("zl","dialog has been cancelde");
//                if(mCurrentpage!=null)
//                {
//                    mCurrentpage.Ondlgcancled();
//                }
            }
        });
        MainActivity.getInstance().setOndataparse(new datacometoparse());
    }

    public void test()
    {

    }
    private class SpinerOnitemselectimp implements AdapterView.OnItemSelectedListener
    {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(listvalue.get(position).equals("65535"))
                {
//                    anologinputlayout.setVisibility(View.VISIBLE);
//                    anologinputlayout.setFocusable(true);
//                    anologinputlayout.setFocusableInTouchMode(true);
                    anologinputlayout.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(anologinputlayout,0);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    m_range.setText("");
                }
                else
                {
                    anologinputlayout.setVisibility(View.GONE);
                }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
    private class ButtonOnclicklistenerimp implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            ArrayList<Map<String,String>> itemdata=new ArrayList<Map<String,String>>();
            int m_currentselect=  msettings.getSelectedItemPosition();
            Map<String,String> temp;
            if(listvalue.size()!=0)
            {
                temp=new HashMap<String,String>();

                if(listvalue.get(m_currentselect).equals("65535")==false)
                {
                    temp.put("text",listcontent.get(m_currentselect));
                    temp.put("settings",listvalue.get(m_currentselect));
                    temp.put("unit","");
                    itemdata.add(temp);
                }
                else
                {
                    if(m_range.length()==0)
                    {
                        ToastUtils.showToast(SensoritemsettingActivity.this,getString(R.string.ANALOG_PARAMETER_ERROR));
                        SensoritemsettingActivity.this.setResult(-1,intent);
                        return;
                    }
                    else
                    {
                        temp.put("text",m_range.getText().toString());
                        temp.put("settings",m_range.getText().toString());
                        temp.put("unit","KPa");
                        itemdata.add(temp);
                    }
                }
            }

             if(m_currentselect!=0&&m_currentselect!=-1)
             {
                 int position=intent.getIntExtra("position",-1);
                 if(editText1.length()==0||editText2.length()==0)
                 {
                     ToastUtils.showToast(SensoritemsettingActivity.this,getString(R.string.ANALOG_PARAMETER_ERROR));
                     SensoritemsettingActivity.this.setResult(-1,intent);
                     return;
                 }
                 if(Float.valueOf(editText1.getText().toString())<Float.valueOf(editText2.getText().toString()))
                 {
                     if(position<=3)
                     {
                         ToastUtils.showToast(SensoritemsettingActivity.this,getString(R.string.UPPER_MUST_BIGGER));
                     }
                     else if(position == 5)
                     {
                         ToastUtils.showToast(SensoritemsettingActivity.this,getString(R.string.GAS_HIGH_MUST_BIGGER));
                     }
                     SensoritemsettingActivity.this.setResult(-1,intent);
                     return;
                 }
             }
            temp=new HashMap<String,String>();
            if(editText1.getText().length()==0)
            {
                temp.put("text","0");
                temp.put("settings","0");
            }
            else
            {
                temp.put("text",editText1.getText().toString());
                temp.put("settings",editText1.getText().toString());
            }
            itemdata.add(temp);

            temp=new HashMap<String,String>();
            if(editText2.getText().toString().length()==0)
            {
                temp.put("text","0");
                temp.put("settings","0");
            }
            else
            {
                temp.put("text",editText2.getText().toString());
                temp.put("settings",editText2.getText().toString());
            }
            itemdata.add(temp);
            switch (m_curposition)
            {
                case 1:
                case 2:
                     if(Float.valueOf(editText1.getText().toString())<Float.valueOf(editText2.getText().toString()))
                     {
                         ToastUtils.showToast(SensoritemsettingActivity.this,getString(R.string.UPPER_MUST_BIGGER));
                         return;
                     }

                case 3:
                case 4:
                    sendbufread[14]= (byte) 0xD9;
                    mainActivity.fregment4.updateallsettingitems(itemdata);
                    break;
                case 5:
                    if(Float.valueOf(editText1.getText().toString())>Float.valueOf(editText2.getText().toString()))
                    {
                        ToastUtils.showToast(SensoritemsettingActivity.this,getString(R.string.GAS_HIGH_MUST_BIGGER));
                        return;
                    }
                case 6:
                    sendbufread[14]= (byte) 0xDA;
                    mainActivity.fregment5.updateallsettingitems(itemdata);
                    break;
            }
//            SensoritemsettingActivity.this.setResult(1,intent);
//            SensoritemsettingActivity.this.finish();
            mSetitemdata = itemdata;
            CodeFormat.crcencode(sendbufread);
            String readOutMsg = DigitalTrans.byte2hex(sendbufread);
            Log.d("zl","\nOnclick:"+readOutMsg);
            misRead = true;
            verycutstatus(readOutMsg);

        }
    }

    public void verycutstatus(String readOutMsg) {
        MainActivity parentActivity1 = MainActivity.getInstance();
        String strState1 = parentActivity1.GetStateConnect();
        if(!strState1.equalsIgnoreCase(getString(R.string.title_not_connected)))
        {
            mDialog.show();
            mDialog.setDlgMsg(getString(R.string.DIALOG_ITEM_WRITE));
            //String input1 = Constants.Cmd_Read_Alarm_Pressure;
            parentActivity1.sendData(readOutMsg, "FFFF");
        }
        else
        {
            ToastUtils.showToast(SensoritemsettingActivity.this,getString(R.string.not_connected));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.getInstance().setOndataparse(null);
    }

    private class datacometoparse  implements MainActivity.Ondataparse
    {

        @Override
        public void datacometoparse(String readOutMsg1, byte[] readOutBuf1) {
            int i=0;
//            ItemSetingActivity.this.mDialog.dismiss();
//            Log.d("zl","\ndatacometoparse:"+CodeFormat.byteToHex(readOutBuf1,readOutBuf1.length).toUpperCase());
            ByteBuffer buf;
            if(misRead==true)
            {
               // return;
                misRead = false;
                if(mSetitemdata==null)
                {
                    return;
                }
                readOutBuf1[5]=0x15;
                switch (m_curposition)
                {
                    case 1:
                    case 2:
                        String tem;
                        tem=mSetitemdata.get(0).get("settings");
                        buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
                        buf.putInt(Integer.valueOf(tem));
                        buf.rewind();
                        buf.get(readOutBuf1,16+10*(m_curposition-1),2);

                        tem=mSetitemdata.get(1).get("settings");
                        buf=ByteBuffer.allocateDirect(4);
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                        buf.putFloat(Float.valueOf(tem).floatValue());
                        buf.rewind();
                        buf.get(readOutBuf1,18+10*(m_curposition-1),4);

                        tem=mSetitemdata.get(2).get("settings");
                        buf=ByteBuffer.allocateDirect(4);
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                        buf.putFloat(Float.valueOf(tem).floatValue());
                        buf.rewind();
                        buf.get(readOutBuf1,22+10*(m_curposition-1),4);
                        //温度选项EMERSON不使用，全部填充为0
//                        int i=0;
                            for(i=0;i<9;i++)
                            {
                                readOutBuf1[36+i]=0x00;
                            }
                        break;
                    case 3:
//                        int i=0;
                        for(i=0;i<9;i++)
                        {
                            readOutBuf1[36+i]=0x00;
                        }
                        break;
                    case 4:
                        tem=mSetitemdata.get(0).get("settings");
                        buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
                        buf.putInt(Integer.valueOf(tem).intValue());
                        buf.rewind();
                        buf.get(readOutBuf1,45,2);

                        tem=mSetitemdata.get(1).get("settings");
                        buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
                        buf.putInt(Integer.valueOf(tem).intValue());
                        buf.rewind();
                        buf.get(readOutBuf1,47,2);
                        break;
                    case 5:
                        tem=mSetitemdata.get(0).get("settings");
                        buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
                        buf.putInt(Integer.valueOf(tem));
                        buf.rewind();
                        buf.get(readOutBuf1,16,2);

                        tem=mSetitemdata.get(2).get("settings");   //低报警
                        buf=ByteBuffer.allocateDirect(4);
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                        buf.putFloat(Float.valueOf(tem).floatValue());
                        buf.rewind();
                        buf.get(readOutBuf1,18,4);

                        tem=mSetitemdata.get(1).get("settings");  //高报警
                        buf=ByteBuffer.allocateDirect(4);
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);
                        buf.putFloat(Float.valueOf(tem).floatValue());
                        buf.rewind();
                        buf.get(readOutBuf1,22,4);
                        break;
                    case 6:
                        tem=mSetitemdata.get(0).get("settings");
                        buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
                        buf.putInt(Integer.valueOf(tem).intValue());
                        buf.rewind();
                        buf.get(readOutBuf1,26,2);

                        tem=mSetitemdata.get(1).get("settings");
                        buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
                        buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
                        buf.putInt(Integer.valueOf(tem).intValue());
                        buf.rewind();
                        buf.get(readOutBuf1,28,2);
                        break;
                }
                CodeFormat.crcencode(readOutBuf1);
                String readOutMsg = DigitalTrans.byte2hex(readOutBuf1);
                Log.d("zl","\ndatacometoparse and misRead is true:"+readOutMsg);
                misWrite = true;
                verycutstatus(readOutMsg);
                return;
            }
            if(misWrite == true)
            {
                misWrite = false;
                byte[] bufcrc = new byte[readOutBuf1.length+2];
                buf=ByteBuffer.allocateDirect(readOutBuf1.length);
                buf.order(ByteOrder.LITTLE_ENDIAN);
                buf.put(readOutBuf1);
                buf.rewind();
                buf.get(bufcrc,0,readOutBuf1.length);
               if(CodeFormat.crcencode(bufcrc)==0)
               {
                   ToastUtils.showToast(SensoritemsettingActivity.this,getString(R.string.STU_SETTING_RESULT_OK));
                   SensoritemsettingActivity.this.setResult(1,intent);
                   SensoritemsettingActivity.this.finish();
               }
               else
               {
                   ToastUtils.showToast(SensoritemsettingActivity.this,getString(R.string.DEVICE_SEETING_ERROR));
               }
                Log.d("zl","\ndatacometoparse and misRead is misWrite:"+readOutMsg1);
            }
//            if(mCurCMD<sendbufs.size())
//            {
//                byte[] recvs=new byte[sendbufs.get(mCurCMD).length+2];
//                ByteBuffer buf = ByteBuffer.allocateDirect(sendbufs.get(mCurCMD).length);
//                buf.order(ByteOrder.LITTLE_ENDIAN);
//                buf.put(sendbufs.get(mCurCMD));
//                buf.rewind();
//                buf.get(recvs,0,sendbufs.get(mCurCMD).length);
//                if(CodeFormat.crcencode(recvs)==0)
//                {
//                    ToastUtils.showToast(MainActivity.getInstance(),getString(R.string.correct));
//                }
//                else
//                {
//                    ToastUtils.showToast(MainActivity.getInstance(),getString(R.string.wrong));
//                    mDialog.dismiss();
//                    StuViDeviceItemSettingActivity.this.setResult(0,getIntent());
//                    StuViDeviceItemSettingActivity.this.finish();
//                    return;
//                }
//            }
//            mCurCMD++;
//            if(mCurCMD<sendbufs.size())
//            {
//                String readOutMsg = DigitalTrans.byte2hex(sendbufs.get(mCurCMD));
//
//                Log.d("zl",CodeFormat.byteToHex(sendbufs.get(mCurCMD),sendbufs.get(mCurCMD).length));
//                verycutstatus(readOutMsg);
//            }
//            else
//            {
//                mDialog.dismiss();
////                StuViDeviceItemSettingActivity.this.setResult(1,getIntent());
////                StuViDeviceItemSettingActivity.this.finish();
//            }
        }
    }
}
