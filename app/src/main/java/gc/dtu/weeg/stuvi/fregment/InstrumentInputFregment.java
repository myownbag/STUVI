package gc.dtu.weeg.stuvi.fregment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gc.dtu.weeg.stuvi.MainActivity;
import gc.dtu.weeg.stuvi.R;
import gc.dtu.weeg.stuvi.utils.CodeFormat;
import gc.dtu.weeg.stuvi.utils.Constants;
import gc.dtu.weeg.stuvi.utils.DigitalTrans;
import gc.dtu.weeg.stuvi.utils.InstrumemtItemseetingActivity;
import gc.dtu.weeg.stuvi.utils.ToastUtils;

/**
 * Created by Administrator on 2018-03-22.
 */

public class InstrumentInputFregment extends BaseFragment {
    View mView;
    public MainActivity mainActivity=MainActivity.getInstance();
    private ListView mReg2000;
    private listadpater list2000adpater;
    private RelativeLayout mReg1998clickrecv;
    private RelativeLayout mReg1999clickrecv;
    private RelativeLayout mReg2000clickrecv;
    private TextView mBuardTx;
    private TextView mParityTx;
    private TextView mDataTx;
    private TextView mStopTx;
    private TextView mRecodeTmTx;
    private Intent intent;
    private Button mbutread;
    private Spinner mCannelSpiner;
    private int mcurSelect=0;
    private int mpreselect=0;
    private byte [][] bufofreadcmd=new byte[3][18];
    String[] listitem;//={"仪表状态:","仪表类型:","仪表地址:","供电时长(步长:10ms):","Elster press 地址:"};

    String[] mcannel={"2000","2001"};
    ArrayList<Map<String,String>> reg2000list;
    private int sendcmeindex=0;
//基础信息
public static String baseinfo[][]; ///123


    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mIsatart=false;
        if(mView!=null)
        {
            return mView;
        }
        mView=inflater.inflate(R.layout.instrumentfraglayout,null,false);
        mReg2000=mView.findViewById(R.id.tv_ins_2000_list);
        mReg1998clickrecv=mView.findViewById(R.id.but_layout_1998);
        mReg1999clickrecv=mView.findViewById(R.id.but_layout_1999);
        mReg2000clickrecv=mView.findViewById(R.id.but_layout_2000);
        mBuardTx=mView.findViewById(R.id.tv_ins_baud_value);
        mParityTx=mView.findViewById(R.id.tv_ins_paritybit_value);
        mDataTx=mView.findViewById(R.id.tv_ins_databit_value);
        mStopTx=mView.findViewById(R.id.tv_ins_stopbit_value);
        mRecodeTmTx=mView.findViewById(R.id.tv_ins_recodegap_value);
        mbutread=mView.findViewById(R.id.tv_ins_btn_read);
        mCannelSpiner=mView.findViewById(R.id.ins_select_spiner);

        InitResourceInfo();
        initview();
        initdata();
        return mView;
    }

    private void initview() {
        mReg1998clickrecv.setOnClickListener(new OnMyclicklisternerImp());
        mReg1999clickrecv.setOnClickListener(new OnMyclicklisternerImp());
       // mReg2000clickrecv.setOnClickListener(new OnMyclicklisternerImp());
        mbutread.setOnClickListener(new Onbutclicklisterner());

        listitem = new String[]{ getString(R.string.EVC_INSTRUMENT_TYPE),
                                 getString(R.string.EVC_INSTRUMENT_ADDRESS),
                /*"仪表状态:","仪表类型:","仪表地址:","供电时长(步长:10ms):","Elster press 地址:"*/};
    }

    private void initdata() {
        reg2000list=new ArrayList<>();

        for (String aListitem : listitem) {
            Map<String, String> tmp = new HashMap<>();
            tmp.put("lable", aListitem);
            tmp.put("value", "");
            reg2000list.add(tmp);
        }
        list2000adpater = new listadpater();
        mReg2000.setAdapter(list2000adpater);
        mReg2000.setOnItemClickListener(new OnmyOnItemClickListenerlistenerImp());
        initsendbuf();
        setSpinneradpater(mCannelSpiner,mcannel);
        mCannelSpiner.setOnItemSelectedListener(new OnItemSelectListernerompl());
    }

    private void initsendbuf() {
        byte[] temp={(byte)0xFD,0x00 ,0x00 ,0x0D ,0x00 ,0x19 ,0x00 ,0x00 ,0x00 ,0x00
                ,0x00 ,0x00 ,0x00 ,0x00 ,(byte)0xCE ,0x07 ,0x42 ,(byte)0x92};
        Log.d("zl","initsendbuf:"+(1998+2+mcurSelect));
        for(int i=0;i<3;i++)
        {
            ByteBuffer buf1;
            buf1=ByteBuffer.allocateDirect(18);
            buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
            buf1.put(temp);
            buf1.rewind();
            buf1.get(bufofreadcmd[i]);
            if(i==2)
            {
                ByteBuffer buf;
                buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
                buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
                buf.putInt(1998+i+mcurSelect);
                buf.rewind();
                buf.get(bufofreadcmd[i],14,2);
            }
            else
            {
                ByteBuffer buf;
                buf=ByteBuffer.allocateDirect(4); //无额外内存的直接缓存
                buf=buf.order(ByteOrder.LITTLE_ENDIAN);//默认大端，小端用这行
                buf.putInt(1998+i);
                buf.rewind();
                buf.get(bufofreadcmd[i],14,2);
            }
            CodeFormat.crcencode(bufofreadcmd[i]);
        }
    }

    private void putdata2000(Intent srcint,int addr)
    {
        String [] listdata=null;
        if(reg2000list!=null)
        {
            listdata=new String[reg2000list.size()];
            for(int i=0;i<reg2000list.size();i++)
            {
                listdata[i]=reg2000list.get(i).get("value");
            }
        }
        srcint.putExtra("listdata",listdata);
        srcint.putExtra("regaddr",2000+mcurSelect);
    }
    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
        Log.d("zl","OndataCometoParse:"+CodeFormat.byteToHex(readOutBuf1,readOutBuf1.length));
        if(mIsatart==false)
        {
            return;
        }
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
           String info[][]=baseinfo;
            switch(sendcmeindex)
            {
                case 0:
                    int buad=0x000000ff&readOutBuf1[19];
                    int parity=0x000000ff&(readOutBuf1[20]&0x03);
                    int databit=0x000000ff&(readOutBuf1[20]&0x0C);
                    int stopbit=0x000000ff&(readOutBuf1[20]&0x30);
                    for(int i=0;i<info.length;i++)
                    {
                        if(info[i][0].equals("1998")&&info[i][1].equals("1"))
                        {
                            if(Integer.valueOf(info[i][2]).intValue()== buad)
                            {
                                mBuardTx.setText(info[i][3]);
                            }
                        }
                        if(info[i][0].equals("1998")&&info[i][1].equals("2"))
                        {
                            if(Integer.valueOf(info[i][2]).intValue()== parity)
                            {
                                mParityTx.setText(info[i][3]);
                            }
                        }
                        if(info[i][0].equals("1998")&&info[i][1].equals("3"))
                        {
                            if(Integer.valueOf(info[i][2]).intValue()== databit)
                            {
                                mDataTx.setText(info[i][3]);
                            }
                        }
                        if(info[i][0].equals("1998")&&info[i][1].equals("4"))
                        {
                            if(Integer.valueOf(info[i][2]).intValue()== stopbit)
                            {
                                mStopTx.setText(info[i][3]);
                            }
                        }
                    }
                    break;
                case 1:
                    byte[] tempbyte={0,0,0,0};
                    tempbyte[0]=readOutBuf1[16];
                    tempbyte[1]=readOutBuf1[17];
                    ByteBuffer buf1;
                    buf1=ByteBuffer.allocateDirect(4);
                    buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
                    buf1.put(tempbyte);
                    buf1.rewind();
                    int gap=buf1.getInt();
                    mRecodeTmTx.setText(""+gap);
                    break;
                case 2:
//                    int devicestatus=0x000000ff&readOutBuf1[16];
                    int devicetype;
                    //ByteBuffer buf1;
                    tempbyte=new byte[4];
                    tempbyte[0]=readOutBuf1[17];
                    tempbyte[1]=readOutBuf1[18];
                    buf1=ByteBuffer.allocateDirect(4);
                    buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
                    buf1.put(tempbyte);
                    buf1.rewind();
                    devicetype=buf1.getInt();
                    Log.d("zl","devicetype:"+devicetype);
                    for(int i=0;i<info.length;i++)
                    {
//                        if(info[i][0].equals("2000")&&info[i][1].equals("1"))
//                        {
//                            if(Integer.valueOf(info[i][3]).intValue()== devicestatus)
//                            {
//
//                                reg2000list.get(0).put("value",info[i][2]);
//                            }
//                        }
                        if(info[i][0].equals("2000")&&info[i][1].equals("2"))
                        {

                            if(Integer.valueOf(info[i][3]).intValue()== devicetype)
                            {
                                reg2000list.get(0).put("value",info[i][2]);
                            }
                        }
                    }
                    //仪表地址
                    int addr=0x000000ff&(readOutBuf1[19]);
                    reg2000list.get(1).put("value",""+addr);
//                    //供电时长
//                    buf1=ByteBuffer.allocateDirect(4);
//                    buf1=buf1.order(ByteOrder.BIG_ENDIAN);
//                    buf1.put(readOutBuf1,24,4);
//                    buf1.rewind();
//                    int timegap=buf1.getInt();
//                    reg2000list.get(3).put("value",""+timegap);

//"Elster press 地址:"
//                    buf1=ByteBuffer.allocateDirect(8);
//                    buf1=buf1.order(ByteOrder.LITTLE_ENDIAN);
//                    buf1.put(readOutBuf1,28,8);
//                    buf1.rewind();
//                    byte[] by1=new byte[8];
//                    buf1.get(by1);
//                    reg2000list.get(4).put("value",DigitalTrans.byte2hex(by1));

                    list2000adpater.notifyDataSetChanged();
                    break;
            }
            sendcmeindex++;
            if(sendcmeindex==bufofreadcmd.length)
            {
                MainActivity.getInstance().mDialog.dismiss();
            }
            if(sendcmeindex<3)
            {
                String readOutMsg = DigitalTrans.byte2hex(bufofreadcmd[sendcmeindex]);
                verycutstatus(readOutMsg);
            }
    }
    private class listadpater extends BaseAdapter
    {
        @Override
        public int getCount() {
            return reg2000list.size();
        }

        @Override
        public Object getItem(int position) {
            return reg2000list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null)
            {
                convertView=View.inflate(mainActivity,R.layout.ins2000itemlayout,null);
            }
//            Log.d("zl",""+convertView);
            TextView viewlable=convertView.findViewById(R.id.ins_item_lable);
            TextView viewvlaue=convertView.findViewById(R.id.ins_item_value);
            viewlable.setText(reg2000list.get(position).get("lable"));
            viewvlaue.setText(reg2000list.get(position).get("value"));
            return convertView;
        }
    }
    public class OnMyclicklisternerImp implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            intent=new Intent(mainActivity, InstrumemtItemseetingActivity.class);
            int id=v.getId();
            switch (id)
            {
                case R.id.but_layout_1998:
                    intent.putExtra("title",getString(R.string.EVC_COM_SETTING));
                    intent.putExtra("buad",mBuardTx.getText().toString());
                    intent.putExtra("parity",mParityTx.getText().toString());
                    intent.putExtra("databit",mDataTx.getText().toString());
                    intent.putExtra("stopbit",mStopTx.getText().toString());
                    intent.putExtra("regaddr",1998);
                    break;
                case R.id.but_layout_1999:
                    intent.putExtra("title","Reg 1999");
                    intent.putExtra("recordgap",mRecodeTmTx.getText().toString());
                    intent.putExtra("regaddr",1999);
                    break;
                case R.id.but_layout_2000:
//                    intent.putExtra("title","Reg 2000");
//                    putdata2000(intent);
                    break;
                    default:
                        break;
            }
            startActivityForResult(intent, Constants.InstrumemtsetingFlag);
        }
    }
    public class OnmyOnItemClickListenerlistenerImp implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            intent=new Intent(mainActivity, InstrumemtItemseetingActivity.class);
            intent.putExtra("title","Reg "+(2000+mcurSelect));
            putdata2000(intent,(2000+mcurSelect));
            startActivityForResult(intent, Constants.InstrumemtsetingFlag);
        }
    }
    private class Onbutclicklisterner implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            mIsatart=true;
            sendcmeindex=0;
            mcurSelect=mCannelSpiner.getSelectedItemPosition();
            mpreselect=mcurSelect;
            initsendbuf();
            String readOutMsg = DigitalTrans.byte2hex(bufofreadcmd[sendcmeindex]);
            verycutstatus(readOutMsg);
        }
    }

//    private void verycutstatus(String readOutMsg) {
//        MainActivity parentActivity1 = (MainActivity) getActivity();
//        String strState1 = parentActivity1.GetStateConnect();
//        if(!strState1.equalsIgnoreCase("无连接"))
//        {
//            parentActivity1.mDialog.show();
//            parentActivity1.mDialog.setDlgMsg("读取中...");
//            //String input1 = Constants.Cmd_Read_Alarm_Pressure;
//            parentActivity1.sendData(readOutMsg, "FFFF");
//        }
//        else
//        {
//            ToastUtils.showToast(getActivity(), "请先建立蓝牙连接!");
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1)
        {
            int reg=data.getIntExtra("regaddr",-1);
            String[] Set=data.getStringArrayExtra("returnsettings");
            switch(reg)
            {
                case 1998:
                    if(Set!=null)
                    {
                        mBuardTx.setText(Set[0]);
                        mParityTx.setText(Set[1]);
                        mDataTx.setText(Set[2]);
                        mStopTx.setText(Set[3]);
                    }
                    break;
                case 1999:
                    if(Set!=null)
                        mRecodeTmTx.setText(Set[0]);
                    break;
                case 2000:
                    if(Set!=null)
                    {
                        for(int i=0;i<reg2000list.size();i++)
                        {
                            reg2000list.get(i).put("value",Set[i]);
                        }
                    }
                    list2000adpater.notifyDataSetChanged();
                case 2001:
                    if(Set!=null)
                    {
                        for(int i=0;i<reg2000list.size();i++)
                        {
                            reg2000list.get(i).put("value",Set[i]);
                        }
                    }
                    list2000adpater.notifyDataSetChanged();
                    break;
            }
        }
    }
    private void setSpinneradpater(Spinner spinner, String[] list )
    {
        int i=0;
        ArrayList<String> arrayList;
        arrayList=new ArrayList<>();
        for(i=0;i<list.length;i++)
        {
            arrayList.add(list[i]);
        }
        //适配器
        ArrayAdapter<String> arr_adapter;
        Activity activity=getActivity();
        if(activity!=null)
        {
            arr_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrayList);
            //设置样式
            arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //加载适配器
            spinner.setAdapter(arr_adapter);
        }
    }

    private class OnItemSelectListernerompl implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d("zl","OnItemSelectListernerompl:"+position);
            mcurSelect=position;
            if(mcurSelect!=mpreselect)
            {
                initsendbuf();
                sendcmeindex=2;
                String readOutMsg = DigitalTrans.byte2hex(bufofreadcmd[sendcmeindex]);
                Log.d("zl","OnItemSelectListernerompl:"+CodeFormat.byteToHex(bufofreadcmd[sendcmeindex],bufofreadcmd[sendcmeindex].length));
                verycutstatus(readOutMsg);
            }
            mpreselect=mcurSelect;
            mIsatart=true;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
    private void InitResourceInfo() {
        baseinfo =new String[][]{
                {"1998","1","1","300"}, // reg,item,seletc,value
                {"1998","1","2","600"},
                {"1998","1","4","1200"},
                {"1998","1","8","2400"},
                {"1998","1","16","4800"},
                {"1998","1","32","9600"},
                {"1998","1","64","19200"},

                {"1998","2","0",getString(R.string.EVC_CHECK_BIT_NONE)},
                {"1998","2","1",getString(R.string.EVC_CHECK_BIT_EVEN)},
                {"1998","2","2",getString(R.string.EVC_CHECK_BIT_ODD)},

                {"1998","3","0","5"},
                {"1998","3","4","6"},
                {"1998","3","8","7"},
                {"1998","3","12","8"},

                {"1998","4","16","0.5"},
                {"1998","4","0","1"},
                {"1998","4","48","1.5"},
                {"1998","4","32","2"},

                {"2000","1",getString(R.string.SSV_STATUS_OFF),"0"},
                {"2000","1",getString(R.string.SSV_STATUS_ON),"1"},


                {"2000","2","None","0"},
                {"2000","2","MFGD_Modbus","1000"},


                {"2000","2","Corus","1010"},
                {"2000","2","Corus 2003","1011"},
                {"2000","2","Corus-Modbus","1012"},
                {"2000","2","SEVC-D 3.0","1013"},

                {"2000","2","AS V1","1020"},
                {"2000","2","AS V2","1018"},

                {"2000","2","CNM","1004"},
                {"2000","2","CNM Modbus","1005"},
                {"2000","2","CNM EVC300","1017"},

                {"2000","2","Elster","1014"},
                {"2000","2","Elster-Modbus V1","1015"},

                {"2000","2","SMARC-Modbus","1023"},

                {"2000","2","Trancy V1.2","1001"},
                {"2000","2","Trancy V1.3","1002"},
                {"2000","2","Tancy Modbus A1","1003"},
                {"2000","2","Trancy Modbus_A4","1022"},
                {"2000","2","Trancy cpuCard","1021"},
//                {"2000","2","PTZ_BOX without Kp","1007"},
                {"2000","2","PTZ-BOX-V1","1006"},
                {"2000","2","PTZ-BOX-V3-1","1008"},
                {"2000","2","PTZ-BOX-V3-2","1009"},
                {"2000","2","PTZ-BOX-CV","1019"},

                {"2000","2","SICK V1","1024"},
//                {"2000","2","MFFD_Modbus","1016"},
//                {"2000","2","FLOWSIC500_V2","1025"},
//                {"2000","2","Tancy_Modbus_TFC","1026"},
//                {"2000","2","Control Valve","10500"},
//                {"2000","2","电压读取","10501"},
        };
    }
}
