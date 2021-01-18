package com.example.tabyspartner.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tabyspartner.R;
import com.example.tabyspartner.model.SliderItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SldierViewHolder>{

    private List<SliderItem> sliderItems;
    private ViewPager2 viewPager2;

   public SliderAdapter(List<SliderItem> sliderItems, ViewPager2 viewPager2) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @NotNull
    @Override
    public SldierViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        return new SldierViewHolder(
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.slider_item_container,viewGroup,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SldierViewHolder sldierViewHolder, int i) {
        sldierViewHolder.setImage(sliderItems.get(i));
        if(i == sliderItems.size() - 2) {
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    class SldierViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        SldierViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageSlide);
        }
        void setImage(SliderItem sliderItem) {
            imageView.setImageResource(sliderItem.getImage());
        }
    }


    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sliderItems.addAll(sliderItems);
            notifyDataSetChanged();
        }
    };
}
