package com.yyzy.constellation.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.yyzy.constellation.R;
import com.yyzy.constellation.activity.LoginActivity;
import com.yyzy.constellation.adapter.LuckItemAdapter;
import com.yyzy.constellation.entity.StarInfoEntity;
import com.yyzy.constellation.utils.AssetsUtils;
import com.yyzy.constellation.utils.DialogUtils;
import com.yyzy.constellation.utils.StringUtils;
import com.yyzy.constellation.weather.activity.WeatherActivity;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import skin.support.SkinCompatManager;

import static android.content.Context.MODE_PRIVATE;

public class MeFragment extends Fragment implements View.OnClickListener {
    private int flag = Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK;
    private StarInfoEntity starInfoEntity;
    private List<StarInfoEntity.StarinfoDTO> mDatas;
    private Map<String, Bitmap> imgMap;
    private CircleImageView cv;
    private TextView tvName,tvJie,tvTui,tvHuan,tvWeather,tvNameLookup;
    private SharedPreferences spf;
    private int selectPos = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取上个界面传过来的数据
        Bundle bundle = getArguments();
        starInfoEntity = (StarInfoEntity) bundle.getSerializable("info");
        mDatas = starInfoEntity.getStarinfo();
        //把星座图片和名称保存
        spf = getContext().getSharedPreferences("star_spf", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        cv = view.findViewById(R.id.meFrag_cv);
        tvName = view.findViewById(R.id.meFrag_tv_name);
        tvJie = view.findViewById(R.id.meFrag_tv_jieshao);
        tvHuan = view.findViewById(R.id.meFrag_tv_huanfu);
        tvTui = view.findViewById(R.id.meFrag_tv_tuichu);
        tvWeather = view.findViewById(R.id.meFrag_tv_weather);
        tvNameLookup = view.findViewById(R.id.meFrag_tv_nameLookup);
        cv.setOnClickListener(this);
        tvJie.setOnClickListener(this);
        tvHuan.setOnClickListener(this);
        tvTui.setOnClickListener(this);
        tvWeather.setOnClickListener(this);
        tvNameLookup.setOnClickListener(this);

        //获取保存在sp里面的状态  并设置
        String name = spf.getString("name", "白羊座");
        String logoname = spf.getString("logoname", "baiyang");
        imgMap = AssetsUtils.getContentLogoImgMap();
        Bitmap bitmap = imgMap.get(logoname);
        cv.setImageBitmap(bitmap);
        tvName.setText(name);
    }

    protected String findByKey(String key) {
        SharedPreferences sp = getActivity().getSharedPreferences("sp_ttit", MODE_PRIVATE);
        return sp.getString(key, "");
    }

    protected void insertVal(String key, String val) {
        SharedPreferences sp = getActivity().getSharedPreferences("sp_ttit", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, val);
        editor.commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meFrag_cv:
                showDialog();
                break;
            case R.id.meFrag_tv_jieshao:
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("功能介绍");
                alertDialog.setMessage(StringUtils.setContent());
                alertDialog.setCancelable(true);
                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
                break;
            case R.id.meFrag_tv_huanfu:
                String skin = findByKey("skin");
                if (skin.equals("night")){
                    // 恢复应用默认皮肤
                    SkinCompatManager.getInstance().restoreDefaultTheme();
                    insertVal("skin","defualt");
                }else {
                    SkinCompatManager.getInstance().loadSkin("night", SkinCompatManager.SKIN_LOADER_STRATEGY_BUILD_IN); // 后缀加载
                    insertVal("skin","night");
                }
                break;
            case R.id.meFrag_tv_tuichu:
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext())
                        .setTitle("温馨提示！")
                        .setMessage("你确定退出吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //获取存储在sp里面的用户名和密码以及两个复选框状态
                                SharedPreferences sharedPreferences = getContext().getSharedPreferences("busApp", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                //清空所有
                                editor.clear();
                                //提交
                                editor.commit();
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.setFlags(flag);
                                startActivity(intent);
                                Toast.makeText(getContext(), "您已成功退出！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //取消按钮
                            }
                        });
                dialog.create().show();
                break;
            case R.id.meFrag_tv_weather:
                startActivity(new Intent(getContext(),WeatherActivity.class));
                break;
            case R.id.meFrag_tv_nameLookup:

                break;
        }
    }

    private void showDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.mefrag_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(view).create();
        dialog.setTitle("请选择您的星座:");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        GridView gv = view.findViewById(R.id.meFrag_dialog_gv);
        //为GridView的Item设置点击事件
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StarInfoEntity.StarinfoDTO dto = mDatas.get(position);
                String name = dto.getName();
                String logoname = dto.getLogoname();
                Bitmap bitmap = imgMap.get(logoname);
                tvName.setText(name);
                cv.setImageBitmap(bitmap);
                selectPos = position;   //保存最后一次选中的位置
                dialog.cancel();
            }
        });
        //设置适配器
        LuckItemAdapter adapter = new LuckItemAdapter(getContext(), mDatas);
        gv.setAdapter(adapter);
        dialog.show();
        //自定义alertDialog大小
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        //lp.width = 1100;//定义宽度
        lp.height = 1600;//定义高度
        dialog.getWindow().setAttributes(lp);
    }

    //失去焦点时   保存图片和星座名称
    @Override
    public void onPause() {
        super.onPause();
        StarInfoEntity.StarinfoDTO starinfoDTO = mDatas.get(selectPos);
        String name = starinfoDTO.getName();
        String logoname = starinfoDTO.getLogoname();
        SharedPreferences.Editor edit = spf.edit();
        edit.putString("name",name);
        edit.putString("logoname",logoname);
        edit.commit();
    }
}