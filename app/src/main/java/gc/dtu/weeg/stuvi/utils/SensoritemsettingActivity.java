package gc.dtu.weeg.stuvi.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import gc.dtu.weeg.stuvi.MainActivity;
import gc.dtu.weeg.stuvi.R;
import gc.dtu.weeg.stuvi.fregment.GasSensorSetFragment;
import gc.dtu.weeg.stuvi.fregment.SensorInputFregment;


public class SensoritemsettingActivity extends Activity {
    Intent intent;
    public MainActivity mainActivity;
    RelativeLayout  selectlayout;
    RelativeLayout  anologinputlayout;
    TextView  mtitle;
    TextView  text1;
    TextView  text2;
    Spinner msettings;
    EditText m_range;
    EditText editText1;
    EditText editText2;

    TextView mAnologLableView;

    ImageView Imageback;
    Button   butcommit;
    ArrayList<String> listcontent;
    ArrayList<String> listvalue;
    int m_currentselect=0;
    int m_curposition=-1;

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
        initview();

    }

    private void initview() {

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
            listcontent=new ArrayList<String>();
            listvalue=new ArrayList<String>();
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
                    anologinputlayout.setVisibility(View.VISIBLE);
                    text1.setText("高报警(KPa)");
                    text2.setText("低报警(KPa)");
                    m_range.setText(tempcontent);
                    editText1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
                    editText2.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
                    break;
                case 4:
                case 6:
                    selectlayout.setVisibility(View.GONE);
                    anologinputlayout.setVisibility(View.GONE);
                    text1.setText("扫描时间(秒)");
                    text2.setText("记录时间(分)");
                    editText1.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText2.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
                case 5:
                    selectlayout.setVisibility(View.VISIBLE);
                    anologinputlayout.setVisibility(View.VISIBLE);
                    text1.setText("高报警");
                    text2.setText("低报警");
                    m_range.setText(tempcontent);
                    editText1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
                    editText2.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
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
                anologinputlayout.setVisibility(View.VISIBLE);
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
    public void test()
    {

    }
    private class SpinerOnitemselectimp implements AdapterView.OnItemSelectedListener
    {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(listvalue.get(position).equals("65535"))
                {
                    anologinputlayout.setVisibility(View.VISIBLE);
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
                        Toast.makeText(SensoritemsettingActivity.this,"请完善信息",Toast.LENGTH_SHORT).show();
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
                 if(editText1.length()==0||editText2.length()==0)
                 {
                     Toast.makeText(SensoritemsettingActivity.this,"请完善信息",Toast.LENGTH_SHORT).show();
                     SensoritemsettingActivity.this.setResult(-1,intent);
                     return;
                 }
                 if(Float.valueOf(editText1.getText().toString())<Float.valueOf(editText2.getText().toString()))
                 {
                     Toast.makeText(SensoritemsettingActivity.this,"高报警必须大于低报警",Toast.LENGTH_SHORT).show();
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
                case 3:
                case 4:
                    mainActivity.fregment4.updateallsettingitems(itemdata);
                    break;
                case 5:
                case 6:
                    mainActivity.fregment5.updateallsettingitems(itemdata);
                    break;
            }
            SensoritemsettingActivity.this.setResult(1,intent);
            SensoritemsettingActivity.this.finish();
        }
    }
}
