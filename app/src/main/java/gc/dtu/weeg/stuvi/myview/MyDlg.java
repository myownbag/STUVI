package gc.dtu.weeg.stuvi.myview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import gc.dtu.weeg.stuvi.R;

public class MyDlg extends Dialog {
    Context m_Activity;
    Button but;
    RadioGroup radioGroup;
    int mIO=1;
    private Onbutclicked myonbutlisterner;
    public MyDlg(@NonNull Context context) {
        super(context);
        m_Activity=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mydlglayout);
        but=findViewById(R.id.dlgokbut);
        radioGroup=findViewById(R.id.dlgselect);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.selectread)
                {
                    mIO=1;
                }
                else if(checkedId==R.id.selectwrite)
                {
                    mIO=0;
                }
            }
        });
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myonbutlisterner!=null)
                {
                    myonbutlisterner.Onbutclicked(mIO);
                }
                MyDlg.this.dismiss();
            }
        });
        setTitle("请选择");
//        Log.d("zl","MyDlg onCreate");
    }
    public  interface Onbutclicked
    {
        public void Onbutclicked(int select);
    }
    public void SetOnbutclickListernerdlg(Onbutclicked onbutclicked)
    {
        myonbutlisterner=onbutclicked;
    }
}
