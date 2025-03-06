package com.example.carinfoapp.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carinfoapp.R;
import com.example.carinfoapp.data.DistributionBoxManager;
import com.example.carinfoapp.data.StateManager;
import com.example.carinfoapp.databinding.FragmentDistributionBoxBinding;
import com.example.carinfoapp.databinding.ItemOutputChannelBinding;
import com.example.carinfoapp.widget.GridItemDecoration;
import com.senptec.adapter.bean.receive.d1.channel.ChannelDataShow;

import java.util.ArrayList;
import java.util.HashMap;

public class DistributionBoxFragment extends Fragment implements DistributionBoxManager.OnCarInfoListener {
    public final ArrayList<ChannelDataShow> mList = new ArrayList<>();
    private final HashMap<Integer, MyOrientation> orientationMap = new HashMap<>();
    private MyOrientation mOrientation;
    private LayoutInflater inflater;
    private ViewGroup container;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DistributionBoxManager boxManager = StateManager.getInstance().distributionBoxManager;
        mList.addAll(boxManager.getList());
        boxManager.addOnCarInfoListener(this);
    }

    private void updateList() {
        int orientation = getResources().getConfiguration().orientation;
        mOrientation = orientationMap.get(orientation);
        if (mOrientation != null) {
            mOrientation.adapter.notifyItemRangeChanged(0, mList.size());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        updateUI();
        return mOrientation.binding.getRoot();
    }

    private void updateUI() {
        int orientation = getResources().getConfiguration().orientation;
        mOrientation = orientationMap.get(orientation);
        if (mOrientation == null) {
            FragmentDistributionBoxBinding binding = FragmentDistributionBoxBinding.inflate(inflater, container, false);
            mOrientation = new MyOrientation();
            mOrientation.binding = binding;
            mOrientation.adapter = new MyAdapter();
            mOrientation.binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            mOrientation.binding.recyclerView.setAdapter(mOrientation.adapter);
            mOrientation.binding.recyclerView.addItemDecoration(new GridItemDecoration(2, 20, 80));
            orientationMap.put(orientation, mOrientation);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StateManager.getInstance().distributionBoxManager.removeOnCarInfoListener(this);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_output_channel, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            ChannelDataShow item = mList.get(position);
            holder.binding.tvChannelName.setText(item.getName());
            holder.binding.tvChannelStatus.setText(item.getStatus());
            holder.binding.tvChannelAmpere.setText(item.getAmpere());
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    private static class MyHolder extends RecyclerView.ViewHolder {
        private final ItemOutputChannelBinding binding;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemOutputChannelBinding.bind(itemView);
        }
    }

    @Override
    public void onChanged(int index, ChannelDataShow channelDataShow) {
        updateRecyclerByPosition(index, channelDataShow);
    }


    private void updateRecyclerByPosition(int position, ChannelDataShow newValue) {
        if (mList.size() <= position) return;
        if (mOrientation == null) return;
        mOrientation.binding.recyclerView.post(new Runnable() {
            @Override
            public void run() {
                mOrientation.adapter.notifyItemChanged(position);
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateUI();
        updateList();
    }

    public static class MyOrientation {
        public FragmentDistributionBoxBinding binding;
        public MyAdapter adapter;
    }
}
