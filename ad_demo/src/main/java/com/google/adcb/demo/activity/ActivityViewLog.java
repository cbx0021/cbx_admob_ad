package com.google.adcb.demo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import androidx.appcompat.app.AppCompatActivity;

import com.addemo.cbx_admob_ad.ad_manager.ADCBMyApplication;
import com.google.adcb.demo.databinding.ActivityViewLogBinding;

import java.util.Locale;

public class ActivityViewLog extends AppCompatActivity {
    private ActivityViewLogBinding binding;

    public static Intent newIntent(Context context) {
        return new Intent(context, ActivityViewLog.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewLogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        onInit();

        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        binding.btnClearLog.setOnClickListener(v -> {
            clearLog();
        });
        binding.btnRefresh.setOnClickListener(v -> {
            onInit();
        });
    }

    private void clearLog() {
        ADCBMyApplication.VIEW_LOG_DATA = "";
        binding.tvLog.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        onInit();
    }

    private void onInit() {
        binding.tvLog.setText("");
        String[] logs = ADCBMyApplication.VIEW_LOG_DATA.toString().split(";");
        for (int i = 0; i < logs.length; i++) {
            String log = logs[i];
            binding.tvLog.append(String.format(Locale.getDefault(), "%3d ", (i + 1)));
            SpannableString colorLog = new SpannableString(log);
            if (log.toUpperCase().contains("AD REQUEST")) {
                colorLog.setSpan(new ForegroundColorSpan(Color.parseColor("#7CB9E8")), 0, log.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (log.toUpperCase().contains("AD LOADED")) {
                colorLog.setSpan(new ForegroundColorSpan(Color.parseColor("#007FFF")), 0, log.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (log.toUpperCase().contains("AD FAILED")) {
                colorLog.setSpan(new ForegroundColorSpan(Color.parseColor("#FF5733")), 0, log.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (log.toUpperCase().contains("FAILED COUNTER")) {
                colorLog.setSpan(new ForegroundColorSpan(Color.parseColor("#A52A2A")), 0, log.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (log.toUpperCase().contains("AD SHOW")) {
                colorLog.setSpan(new ForegroundColorSpan(Color.parseColor("#04AF70")), 0, log.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            binding.tvLog.append(colorLog);
            binding.tvLog.append("\n");
        }
    }
}