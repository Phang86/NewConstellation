package com.yyzy.constellation.news.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yyzy.constellation.R;
import com.yyzy.constellation.news.NewsInfoActivity;
import com.yyzy.constellation.news.adapter.NewsItemAdapter;
import com.yyzy.constellation.news.bean.NewsBean;
import com.yyzy.constellation.news.bean.TypeBean;
import com.yyzy.constellation.utils.MyToast;
import com.yyzy.constellation.weather.fragment.BaseFragment;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewsInfoFragment extends BaseFragment {

    private String url;
    private ListView listView;
    private List<NewsBean.ResultBean.DataBean> mData;
    private NewsItemAdapter adapter;
    private SmartRefreshLayout refreshLayout;

    public NewsInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_info, container, false);
        listView = view.findViewById(R.id.news_fragment_lv);
        refreshLayout = view.findViewById(R.id.smr_refreshLayout);
        Bundle bundle = getArguments();
        TypeBean typeBean = (TypeBean) bundle.getSerializable("type");
        url = typeBean.getUrl();
        mData = new ArrayList<>();
        setRefreshListener();
        adapter = new NewsItemAdapter(getActivity(), mData);
        listView.setAdapter(adapter);
        volleyLoadData(url);
        setListener();
        return view;
    }

    private void setRefreshListener() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                volleyLoadData(url);
                refreshLayout.finishRefresh(500);
            }
        });
    }

    private void setListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsBean.ResultBean.DataBean bean = mData.get(position);
                String url = bean.getUrl();
                String title = bean.getTitle();
                Intent intent = new Intent(getContext(), NewsInfoActivity.class);
                intent.putExtra("url",url);
                intent.putExtra("title",title);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        super.onErrorResponse(error);
    }

    @Override
    public void onResponse(String response) {
        NewsBean newsBean = new Gson().fromJson(response, NewsBean.class);
        if (newsBean.getError_code() == 10012 || newsBean.getResult() == null || newsBean.getReason().equals("超过每日可允许请求次数!")) {
            MyToast.showText(getActivity(),"今日请求次数上限！");
            refreshLayout.setVisibility(View.GONE);
            return;
        }else if (newsBean.getResult() != null){
            List<NewsBean.ResultBean.DataBean> data = newsBean.getResult().getData();
            refreshLayout.setVisibility(View.VISIBLE);
            mData.addAll(data);
            adapter.notifyDataSetChanged();
        }
    }
}