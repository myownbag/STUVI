package gc.dtu.weeg.stuvi.fregment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import gc.dtu.weeg.stuvi.MainActivity;
import gc.dtu.weeg.stuvi.R;
import gc.dtu.weeg.stuvi.utils.ToastUtils;

public abstract class BaseFragment extends Fragment {
    public Boolean mIsatart=false;
    public boolean m_dlgcancled=false;
    Bundle bundle;
    public int position=0;
    String str;
    abstract public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        if (bundle != null) {
            position=bundle.getInt("position");
            str=bundle.getString("extra");
//            Log.d("zl","position:"+position);
            Log.d("zl","title:"+str);
        }
        else
        {
            Log.d("zl","position:"+"ERROR");
        }
    }

    public void Oncurrentpageselect(int index)
    {
        if(position!=index)
        {
            mIsatart=false;
        }
        if(str.equals("固件升级"))
        {
            MainActivity.getInstance().getcurblueservice().ChangetimeoutofPackage(50);
        }
        else
        {
            MainActivity.getInstance().getcurblueservice().ChangetimeoutofPackage(200);
        }
    }
    public void Ondlgcancled()
    {
        mIsatart=false;
    }

    public void verycutstatus(String readOutMsg) {
        MainActivity parentActivity1 = (MainActivity) getActivity();
        String strState1 = parentActivity1.GetStateConnect();
        if(!strState1.equalsIgnoreCase(getString(R.string.title_not_connected)))
        {
            parentActivity1.mDialog.show();
            parentActivity1.mDialog.setDlgMsg(getString(R.string.reading));
            //String input1 = Constants.Cmd_Read_Alarm_Pressure;
            parentActivity1.sendData(readOutMsg, "FFFF");
        }
        else
        {
            ToastUtils.showToast(getActivity(), getString(R.string.not_connected));
        }
    }
    public void verycutstatus(String readOutMsg,int timeout) {
        MainActivity parentActivity1 = (MainActivity) getActivity();
        String strState1 = parentActivity1.GetStateConnect();
        if(!strState1.equalsIgnoreCase(getString(R.string.title_not_connected)))
        {
            parentActivity1.mDialog.show();
            parentActivity1.mDialog.setDlgMsg(getString(R.string.reading));
            //String input1 = Constants.Cmd_Read_Alarm_Pressure;
            parentActivity1.sendData(readOutMsg, "FFFF",timeout);
        }
        else
        {
            ToastUtils.showToast(getActivity(),  getString(R.string.not_connected));
        }
    }
//        public void verycutstatus(byte [] buf,int timeout)
//    {
//        MainActivity parentActivity1 = MainActivity.getInstance();
//        String strState1 = parentActivity1.GetStateConnect();
//        if(!strState1.equalsIgnoreCase("无连接"))
//        {
////            parentActivity1.mDialog.show();
////            parentActivity1.mDialog.setDlgMsg("读取中...");
//            //String input1 = Constants.Cmd_Read_Alarm_Pressure;
//            parentActivity1.sendData(buf,0);
//        }
//        else
//        {
//            ToastUtils.showToast(parentActivity1, "请先建立蓝牙连接!");
//        }
//        if(timeout>0)
//        {
//
//            cv = new Thread(new timeoutSupervisor(timeout));
//            cv.start();
//        }
//    }
}
