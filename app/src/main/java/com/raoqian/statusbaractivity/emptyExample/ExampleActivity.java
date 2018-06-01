package com.raoqian.statusbaractivity.emptyExample;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.raoqian.listemptyclickhelper.ListHelper;
import com.raoqian.statusbaractivity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raoqian on 2018/4/16.
 */

public class ExampleActivity extends Activity {
    RecyclerView baseRecycler;
    GuideAdapter mAdapter;
    EditText inputView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_fragement);
        baseRecycler = findViewById(R.id.base_recycler);
        inputView = findViewById(R.id.input);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        baseRecycler.setLayoutManager(llm);
        mAdapter = new GuideAdapter(this);
        baseRecycler.setAdapter(mAdapter);

        View helperEmptyView = getLayoutInflater().inflate(R.layout.view_empty_on_recycleview2, null, false);
        helper.addEmptyViewOnRecycler(this, baseRecycler, helperEmptyView);//使用自定义空视图
//        helper2.addEmptyViewOnRecycler(this,baseRecycler); //使用默认空视图

    }

    ListHelper helper = new ListHelper() {
        @Override
        public void onItemCountChange(int itemCount, View empty) {
            TextView emptyText = helperEmptyView.findViewById(R.id.empty_text);
            if (itemCount > 0) {
                helperEmptyView.setVisibility(View.GONE);
            } else {
                helperEmptyView.setVisibility(View.VISIBLE);
                emptyText.setText("空视图");
            }
        }

        @Override
        public void onAction(View empty) {
            TextView emptyText = helperEmptyView.findViewById(R.id.empty_text);
            emptyText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ExampleActivity.this, "点击了空视图", Toast.LENGTH_SHORT).show();
                }
            });
        }
        //        @Override
//        public void onItemCountChange(int itemCount, TextView emptyText, ImageView emptyImage) {
//            if (itemCount > 0) {
//                helperEmptyView.setVisibility(View.GONE);
//            } else {
//                helperEmptyView.setVisibility(View.VISIBLE);
//                emptyText.setText("空视图");
//            }
//        }
//
//        @Override
//        public void onAction(TextView emptyText, ImageView emptyImage) {
//            emptyText.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(ExampleActivity.this, "点击了空视图", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
    };

    ListHelper helper2 = new ListHelper() {
        @Override
        public void onItemCountChange(int itemCount, TextView emptyText, ImageView emptyImage) {
            if (itemCount > 0) {
                helperEmptyView.setVisibility(View.GONE);
            } else {
                helperEmptyView.setVisibility(View.VISIBLE);
                emptyText.setText("空视图");
            }
        }

        @Override
        public void onAction(TextView emptyText, ImageView emptyImage) {
            emptyText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ExampleActivity.this, "点击了空视图", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    public void onSure(View view) {
        int count = Integer.parseInt(inputView.getText().toString());
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            datas.add("数据" + i);
        }
        mAdapter.setData(datas);
    }

    class GuideAdapter extends BaseAdapter<GuideHoler, String> {

        public GuideAdapter(Activity context) {
            super(context);
        }

        @Override
        public GuideHoler onCreateHolder(ViewGroup parent, int viewType) {
            View view = getView(R.layout.recycler_item_guide, parent);
            return new GuideHoler(view);
        }

        @Override
        public void onBindingHolder(GuideHoler holder, final int position) {
            holder.mTitle.setText(getDataItem(position));
        }
    }


    class GuideHoler extends BaseHolder {
        public TextView mTitle;

        public GuideHoler(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.list_item);
        }
    }
}

