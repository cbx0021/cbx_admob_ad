package com.google.adcb.demo.adapter;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.addemo.cbx_admob_ad.ad_manager.ADCBItemClickListener;
import com.addemo.cbx_admob_ad.ad_manager.ADCBAdLoader;
import com.google.adcb.demo.databinding.LayoutItemBinding;
import com.google.adcb.demo.databinding.LayoutItemNativeAdBinding;

import java.util.ArrayList;
import java.util.Random;

public class AdapterNativeAdList extends RecyclerView.Adapter<ViewHolder> {
    private final ArrayList<Object> filteredItems;
    Activity context;
    ArrayList<String> languages;
    Handler handleAd;
    private final ADCBItemClickListener listener;

    public AdapterNativeAdList(Activity context, ArrayList<String> items, ADCBItemClickListener listener) {
        this.context = context;
        this.languages = items;
        this.filteredItems = adAdsToList(5, items);
        this.handleAd = new Handler(context.getMainLooper());
        this.listener = listener;
    }

    private ArrayList<Object> adAdsToList(int step, ArrayList<String> items) {
        int counter = 0;
        ArrayList<Object> newLists = new ArrayList<>();
        int i = 0;
        do {
            if (i != 0 && step != 0 && counter % step == 0) {
                if (items.size() != 1) {
                    newLists.add(null);
                }
            }
            if (i < items.size()) {
                String item = items.get(i);
                if (items.size() == 1 && step != 0) {
                    newLists.add(item);
                    newLists.add(null);
                } else {
                    newLists.add(item);
                }
            }
            counter++;
            i++;
        } while (i <= items.size());
        return newLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 11) {
            return new NativeAdViewHolder(LayoutItemNativeAdBinding.inflate(LayoutInflater.from(context), parent, false));
        } else {
            return new MyViewHolder(LayoutItemBinding.inflate(LayoutInflater.from(context), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object item = filteredItems.get(position);
        if (item instanceof String item1 && holder instanceof MyViewHolder holder1) {
            holder1.binding.tvTitle.setText("Click Here (");
            holder1.binding.tvTitle.append(item1);
            holder1.binding.tvTitle.append(")");
            holder1.binding.getRoot().setOnClickListener(v -> listener.OnItemClick(item1, holder1.getAdapterPosition()));
        } else if (item == null && holder instanceof NativeAdViewHolder holder1) {
            if (ADCBAdLoader.getInstance().getNativeAds().size() == 0) {
                handleAd.post(new Runnable() {
                    @Override
                    public void run() {
                        if (ADCBAdLoader.getInstance().getNativeAds().size() > 0) {
                            handleAd.removeCallbacks(this);
                            notifyItemChanged(holder.getAdapterPosition());
                        } else {
                            handleAd.postDelayed(this, 1000);
                        }
                    }
                });
            }
            if(new Random().nextBoolean()) {
                ADCBAdLoader.getInstance().showNativeSmallList(context, holder1.binding.ltNative);
            }else{
                ADCBAdLoader.getInstance().showNativeLargeList(context, holder1.binding.ltNative);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return filteredItems.get(position) == null ? 11 : 0;
    }

    public int getItemCount() {
        return filteredItems.size();
    }

    static class MyViewHolder extends ViewHolder {
        LayoutItemBinding binding;

        public MyViewHolder(@NonNull LayoutItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    static class NativeAdViewHolder extends ViewHolder {
        LayoutItemNativeAdBinding binding;

        NativeAdViewHolder(LayoutItemNativeAdBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}