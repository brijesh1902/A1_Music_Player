package com.brizzs.a1musicplayer.ui.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.brizzs.a1musicplayer.R;
import com.brizzs.a1musicplayer.databinding.ActivityOnBoardingBinding;
import com.brizzs.a1musicplayer.databinding.ViewOnboardScreenBinding;
import com.brizzs.a1musicplayer.model.DataModel;
import com.brizzs.a1musicplayer.ui.main.MainActivity;
import com.brizzs.a1musicplayer.utils.MyApplication;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OnBoardingActivity extends AppCompatActivity {

    ActivityOnBoardingBinding binding;
    OnBoardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding);

        // Look for and REMOVE or comment out this block:
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            // This code typically applies padding equal to the system bar height
            // to prevent content from going behind the bars.
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Removing the logic that sets padding here will also help disable the effect.
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        adapter = new OnBoardingAdapter();
        adapter.submitList(getBoardList());
        binding.viewPager.setAdapter(adapter);

        setOnBoardingIndicator();
        setCurrentOnBoardingIndicators(0);

    }

    @Override
    protected void onResume() {
        super.onResume();

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnBoardingIndicators(position);
            }
        });

        binding.btnNext.setOnClickListener(view -> {
            if (binding.viewPager.getCurrentItem() + 1 < adapter.getItemCount()) {
                binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1);
            } else {
                MyApplication.getSharePreference().setFirstRun(false);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

    }

    private void setOnBoardingIndicator() {
        ImageView[] indicators = new ImageView[adapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_dot));
            indicators[i].setLayoutParams(layoutParams);
            binding.dotsLayout.addView(indicators[i]);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setCurrentOnBoardingIndicators(int index) {
        int childCount = binding.dotsLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) binding.dotsLayout.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_dot_selected));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_dot));
            }
        }

        binding.title.setText(getBoardList().get(index).getTitle());
        binding.desc.setText(getBoardList().get(index).getDescription());

    }

    private List<DataModel> getBoardList() {
        List<DataModel> list = new ArrayList<>();


        list.add(new DataModel(getString(R.string.title_1), getString(R.string.desc_1), R.drawable.img_onboard1));
        list.add(new DataModel(getString(R.string.title_2), getString(R.string.desc_2), R.drawable.img_onboard2));
        list.add(new DataModel(getString(R.string.title_3), getString(R.string.desc_3), R.drawable.img_onboard3));

        return list;
    }

    private class OnBoardingAdapter extends ListAdapter<DataModel, OnBoardingAdapter.ViewHolder> {

        public OnBoardingAdapter() {
            super(diffCallback);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ViewOnboardScreenBinding binding = ViewOnboardScreenBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DataModel model = getItem(position);

            Glide.with(holder.itemView.getContext()).load(model.getImage()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.binding.image);

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ViewOnboardScreenBinding binding;

            public ViewHolder(@NonNull ViewOnboardScreenBinding itemView) {
                super(itemView.getRoot());
                binding = itemView;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }

    DiffUtil.ItemCallback<DataModel> diffCallback = new DiffUtil.ItemCallback<DataModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull DataModel oldItem, @NonNull DataModel newItem) {
            return Objects.equals(oldItem.getTitle(), newItem.getTitle());
        }

        @Override
        public boolean areContentsTheSame(@NonNull DataModel oldItem, @NonNull DataModel newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };

}