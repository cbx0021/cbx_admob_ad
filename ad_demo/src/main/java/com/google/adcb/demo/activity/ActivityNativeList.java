package com.google.adcb.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.addemo.cbx_admob_ad.ad_manager.ADCBAdLoader;
import com.google.adcb.demo.adapter.AdapterNativeAdList;
import com.google.adcb.demo.databinding.ActivityNativeListBinding;

import java.util.ArrayList;

public class ActivityNativeList extends AppCompatActivity {
    private ActivityNativeListBinding binding;

    public static Intent newIntent(Context context) {
        return new Intent(context, ActivityNativeList.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNativeListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ADCBAdLoader.log("ActivityNativeList -> OnCreate()");

        onInit();

        binding.toolbar.setNavigationOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
        binding.btnViewLog.setOnClickListener(v -> startActivity(ActivityViewLog.newIntent(ActivityNativeList.this)));
    }

    private void onInit() {
        //TODO Handle OnBackPress
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ADCBAdLoader.finishWithAd(ActivityNativeList.this);
            }
        });

        ADCBAdLoader.getInstance().showNativeSmall(this, binding.ltNative);
        ArrayList<String> items = new ArrayList<>();
        items.add("Result 1");
        items.add("Result 2");
        items.add("Result 3");
        items.add("Result 4");
        items.add("Result 5");
        items.add("Result 6");
        items.add("Result 7");
        items.add("Result 8");
        items.add("Result 9");
        items.add("Result 10");
        items.add("Result 11");
        items.add("Result 12");
        items.add("Result 13");
        items.add("Result 14");
        items.add("Result 15");
        items.add("Result 16");
        items.add("Result 17");
        items.add("Result 18");
        items.add("Result 19");
        items.add("Result 20");
        items.add("Result 21");
        items.add("Result 22");
        items.add("Result 23");
        binding.rvItem.setAdapter(new AdapterNativeAdList(this, items, (item, position) -> {
            Intent intent = new Intent();
            intent.putExtra(Intent.EXTRA_TEXT, item);
            //TODO finish activity with result ad
            ADCBAdLoader.finishWithResultAd(ActivityNativeList.this, RESULT_OK, intent);
        }));
    }

    @Override
    protected void onDestroy() {
        ADCBAdLoader.log("ActivityNativeList -> OnDestroy()");
        super.onDestroy();
    }
}