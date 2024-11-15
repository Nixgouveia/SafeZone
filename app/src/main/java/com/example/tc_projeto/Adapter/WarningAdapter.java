package com.example.tc_projeto.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.tc_projeto.model.WeatherWarning;
import com.example.tc_projeto.R;

public class WarningAdapter extends RecyclerView.Adapter<WarningAdapter.WarningViewHolder> {
    private List<WeatherWarning> warningList;

    public WarningAdapter(List<WeatherWarning> warningList) {
        this.warningList = warningList;
    }

    @NonNull
    @Override
    public WarningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_warning, parent, false);
        return new WarningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WarningViewHolder holder, int position) {
        WeatherWarning warning = warningList.get(position);
        holder.type.setText(warning.getAwarenessTypeName());
        holder.level.setText(warning.getAwarenessLevelID());
        holder.regiao.setText(warning.getIdAreaAviso());
        holder.startTime.setText(warning.getStartTime());
        holder.endTime.setText(warning.getEndTime());
    }

    @Override
    public int getItemCount() {
        return warningList.size();
    }


    public void updateWarnings(List<WeatherWarning> newWarnings) {
        this.warningList.clear();
        this.warningList.addAll(newWarnings);
        notifyDataSetChanged();
    }

    public static class WarningViewHolder extends RecyclerView.ViewHolder {
        TextView type, level, startTime, endTime, regiao;

        public WarningViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.text_type);
            level = itemView.findViewById(R.id.text_level);
            regiao = itemView.findViewById(R.id.text_reg);
            startTime = itemView.findViewById(R.id.text_start_time);
            endTime = itemView.findViewById(R.id.text_end_time);
        }
    }
}


