package gc.dtu.weeg.stuvi.myview;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import android.widget.EditText;
import android.widget.LinearLayout;

import gc.dtu.weeg.stuvi.R;

public class LocalSetaddr201ExtrainfoView extends LinearLayout {
    String m198Modul="";
    Context mParent=null;
    String mcurcontent;
    String str220set;
    SettingInterface settingInterface=null;
    private  View view201show;

    EditText EditviewApn;
    EditText EditviewUsers;
    EditText EditviewPSWD;

    String mAPN;
    String mUSERS;
    String mPWSD;

    boolean mDatacorrect=false;

    public LocalSetaddr201ExtrainfoView(Context context,String setcontent,String str220,String addr198Modul) {
        super(context);
        mParent = context;
        m198Modul = addr198Modul;
        mcurcontent=setcontent;
        str220set=str220;

        view201show = View.inflate(mParent, R.layout.localsetting_addr201_layout,null);
        mAPN="";
        mUSERS="";
        mPWSD="";
        EditviewApn=view201show.findViewById(R.id.addr202_APN);
        EditviewUsers=view201show.findViewById(R.id.addr202_USERS);
        EditviewPSWD =view201show.findViewById(R.id.addr202_PASSWORDS);
        initshow( mcurcontent);
        EditviewApn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               // Log.d("zl",charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Log.d("zl",editable.toString());
                LocalSetaddr201ExtrainfoView.this.mAPN= editable.toString();
                updatecontent();
            }
        });

        EditviewUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                LocalSetaddr201ExtrainfoView.this.mUSERS= editable.toString();
                updatecontent();
            }
        });

        EditviewPSWD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                LocalSetaddr201ExtrainfoView.this.mPWSD= editable.toString();
                updatecontent();
            }
        });

        addView(view201show);
        LinearLayout lineatView;
        if(m198Modul.equals("NB-IOT")||m198Modul.equals("M72"))
        {

            lineatView = view201show.findViewById(R.id.addr201_USER_Container);
            lineatView.setVisibility(View.GONE);

            lineatView = view201show.findViewById(R.id.addr201_PSWD_Container);
            lineatView.setVisibility(View.GONE);
        }
        else if(m198Modul.equals("MC323"))
        {
            lineatView = view201show.findViewById(R.id.addr201_APN_Container);
            lineatView.setVisibility(View.GONE);
        }
        else if(m198Modul.equals("EC20 4G"))
        {

        }
        else
        {
            view201show.setVisibility(View.GONE);
        }
    }

    private void initshow(String strcontent) {
        int index=0;
        String show;
        show=new String (strcontent);
        if(m198Modul.equals("NB-IOT")||m198Modul.equals("M72"))
        {
            if(str220set.equals("")==false|| show.indexOf(",")>=0)
            {
                //settingInterface.OncurSetting(str220set+","+strcontent);
                mDatacorrect=false;
                return;
            }
            else
            {
                mAPN=show;
            }
        }
        else if(m198Modul.equals("MC323"))
        {
            if(str220set.equals("")==false||show.indexOf(",")<0)
            {
//                settingInterface.OncurSetting(str220set+","+strcontent);
                mDatacorrect=false;
                return;
            }
            else if(show.indexOf(",")>=0)
            {
                index=show.indexOf(",");
                int indextemp=index+1;
                indextemp=show.indexOf(",",indextemp);
                if(indextemp>=0)
                {
//                    settingInterface.OncurSetting(str220set+","+strcontent);
                    mDatacorrect=false;
                    return;
                }
//                mAPN="";
                mUSERS=show.substring(0,index);
                mPWSD = show.substring(index+1,show.length());
            }
        }
        else if(m198Modul.equals("EC20 4G"))
        {
            index=show.indexOf(",");
            if(str220set.equals(""))
            {

                if(index<0)
                {
//                    settingInterface.OncurSetting(str220set+","+strcontent);
                    mDatacorrect=false;
                    return;
                }
                mAPN=show.substring(0,index);

            }
            else
            {
                mAPN=str220set;
            }

            show=show.substring(index+1,show.length());
            index=show.indexOf(",");
            if(index<0)
            {
//                settingInterface.OncurSetting(str220set+","+strcontent);
                mDatacorrect=false;
                return;
            }
            else
            {
                mUSERS=show.substring(0,index);
                mPWSD=show.substring(index+1,show.length());
            }
        }
        mDatacorrect=true;
//        if(strcontent.length()==0)
//        {
//            return;
//        }
//        index=strcontent.indexOf(",");
//        if(index==0)
//        {
//            if(str220set.length()==0)
//            {
//                EditviewApn.setText("");
//                mAPN="";
//            }
//            else
//            {
//                EditviewApn.setText(str220set);
//                mAPN=str220set;
//            }
//        }
//        else
//        {
//            show=strcontent.substring(0,index);
//            EditviewApn.setText(show);
//            mAPN=show;
//        }
//        show=strcontent.substring(index+1,strcontent.length());
//        index=show.indexOf(",");
//        EditviewUsers.setText(show.substring(0,index));
//        mUSERS=show.substring(0,index);
//        show=show.substring(index+1,show.length());
//        EditviewPSWD.setText(show);
//        mPWSD=show;
        EditviewApn.setText(mAPN);
        EditviewUsers.setText(mUSERS);
        EditviewPSWD.setText(mPWSD);
        setContent();
    }

    public void setContent()
    {
        if(settingInterface==null)
        {
            return;
        }
        if(mDatacorrect==false)
        {
            settingInterface.OncurSetting(str220set+mcurcontent);
            return;
        }
        updatecontent();
    }

    private void updatecontent() {
        if(settingInterface==null)
        {
            return;
        }
        switch (m198Modul) {
            case "NB-IOT":
            case "M72":
                settingInterface.OncurSetting(mAPN);
                break;
            case "MC323":
                settingInterface.OncurSetting(mUSERS + "," + mPWSD);
                break;
            case "EC20 4G":
                settingInterface.OncurSetting(mAPN + "," + mUSERS + "," + mPWSD);
                break;
        }
    }

    public interface SettingInterface
    {
         void OncurSetting(String set);
    }

    public void setOncursettingChanged(SettingInterface si)
    {
        settingInterface=si;
    }

    public void updatecurrentshow()
    {
        setContent();
    }
}
