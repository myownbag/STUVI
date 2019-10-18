package gc.dtu.weeg.stuvi.fregment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import gc.dtu.weeg.stuvi.MainActivity;
import gc.dtu.weeg.stuvi.R;
import gc.dtu.weeg.stuvi.utils.CodeFormat;
import gc.dtu.weeg.stuvi.utils.DigitalTrans;
import gc.dtu.weeg.stuvi.utils.ToastUtils;

public class StuViSSVSettingFragment extends BaseFragment implements View.OnClickListener {
    View mView;
    LayoutInflater thisinflater;
    ViewGroup  thiscontainer;

    Button btn;
    TextView mSSVStatus1;
    TextView mSSVStatus2;

    @Override
    public void OndataCometoParse(String readOutMsg1, byte[] readOutBuf1) {
//        Log.d("zl","StuViSSVSettingFragment->OndataCometoParse: "
//                +CodeFormat.byteToHex(readOutBuf1,readOutBuf1.length).toUpperCase());
        if(MainActivity.getInstance().mDialog.isShowing())
            MainActivity.getInstance().mDialog.dismiss();
        if(mIsatart==true)
        {
            byte[] buf= new byte[readOutBuf1.length+2];
            ByteBuffer mybuf= ByteBuffer.allocateDirect(readOutBuf1.length);
            mybuf.order(ByteOrder.LITTLE_ENDIAN);
            mybuf.put(readOutBuf1);
            mybuf.rewind();
            mybuf.get(buf,0,readOutBuf1.length);
            if(CodeFormat.crcencode(buf)!=0)
            {
                ToastUtils.showToast(MainActivity.getInstance(),getString(R.string.wrong));
            }
            else
            {
                if(readOutBuf1[16]==0x00)
                {
                    mSSVStatus1.setText(getString(R.string.SSV_STATUS_ON));
                }
                else if(readOutBuf1[16]==0x01)
                {
                    mSSVStatus1.setText(getString(R.string.SSV_STATUS_OFF));
                }
                if(readOutBuf1[17]==0x00)
                {
                    mSSVStatus2.setText(getString(R.string.SSV_STATUS_ON));
                }
                else if(readOutBuf1[17]==0x01)
                {
                    mSSVStatus2.setText(getString(R.string.SSV_STATUS_OFF));
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        mIsatart=false;
        if (mView != null) {
            // 防止多次new出片段对象，造成图片错乱问题
            return mView;
        }
        thisinflater =inflater;
        thiscontainer=container;
        mView = inflater.inflate(R.layout.stu_i_ssv_setting_fragment_layout, container, false);
//        initdata();
        initView();
//        initdata();
        return  mView;
    }

    private void initView() {
        mSSVStatus1 = mView.findViewById(R.id.stu_ssv_line1_value);
        mSSVStatus2 =mView.findViewById(R.id.stu_ssv_line2_value);
        btn = mView.findViewById(R.id.stu_setting_action_read);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        byte[] senddatabuf =new byte[18];
        int index=0;
        int i=0;
        senddatabuf[index++]= (byte) 0xfd;
        senddatabuf[index++]= (byte) 0x00;
        senddatabuf[index++]= (byte) 0x00;
        senddatabuf[index++]= 13;
        senddatabuf[index++]= (byte) 0x00;
        senddatabuf[index++]= (byte) 0x19;
        for(i=0;i<8;i++)
        {
            senddatabuf[index++]= (byte) 0x00;
        }
        ByteBuffer buf = ByteBuffer.allocateDirect(2);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putShort((short) 1000);
        buf.rewind();

        buf.get(senddatabuf,index,2);
        index+=2;
        CodeFormat.crcencode(senddatabuf);
        String readOutMsg = DigitalTrans.byte2hex(senddatabuf);
        mIsatart = true;
        mSSVStatus1.setText("");
        mSSVStatus2.setText("");
        verycutstatus(readOutMsg);
    }

    private void verycutstatus(String readOutMsg) {
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
    private void verycutstatus(String readOutMsg,int timeout) {
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
}
