package gc.dtu.weeg.stuvi.fregment;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.Map;

import gc.dtu.weeg.stuvi.utils.InstrumemtItemseetingActivity;

public abstract class instrumentbaseFragment extends Fragment {
    protected InstrumemtItemseetingActivity mActivity;
    abstract public ArrayList<Map<String,String>> OnbutOKPress(byte [] sendbuf);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity=(InstrumemtItemseetingActivity) context;
    }
    public Map<String,String> findcursetting(int position,ArrayList<Map<String,String>> list,String keyname,String keyvalue){
       return null;
    }
}
