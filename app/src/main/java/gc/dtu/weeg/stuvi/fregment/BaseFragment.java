package gc.dtu.weeg.stuvi.fregment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import gc.dtu.weeg.stuvi.MainActivity;

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
//            Log.d("zl","title:"+str);
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
}
