package com.dataplus.tabyspartner.adapter;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.dataplus.tabyspartner.R;
import com.dataplus.tabyspartner.model.SliderItem;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SldierViewHolder>{

    private List<SliderItem> sliderItems;
    private ViewPager2 viewPager2;
    private Callback c;

    public interface Callback {
        void onAction(int action);
    }

   public SliderAdapter(List<SliderItem> sliderItems, ViewPager2 viewPager2, Callback c) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
        this.c = c;
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
            this.imageView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    c.onAction(motionEvent.getAction());
                    return true;
                }
            });
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
