package com.sensorcontrol.ui.activity;

import android.widget.ListView;
import com.sensorcontrol.R;
import com.sensorcontrol.base.BaseActivity;
import com.sensorcontrol.bean.ListBean;
import com.sensorcontrol.ui.adapter.BtnListAdapter;

import java.util.List;

import butterknife.BindView;

/**
 * Created by lizhe on 2017/9/29 0029.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class BtnListActivity extends BaseActivity {

    @BindView(R.id.list_btn)
    ListView listBtn;

    private BtnListAdapter mAdapter;
    private int type;
    private List mList;
    private ListBean mBean;
    @Override
    protected int setLayout() {
        return R.layout.activity_btnlist;
    }

    @Override
    protected void init() {
        type = getIntent().getIntExtra("type",-1);
        if (type == -1){
            return;
        }
        mAdapter = new BtnListAdapter(this,type,this);
        listBtn.setAdapter(mAdapter);
    }

    @Override
    protected void setData() {
        mBean = (ListBean) getIntent().getSerializableExtra("data");
        mList = mBean.getList();
        if (mList == null){
            return;
        }
        mAdapter.setmList(mList);
    }

}
