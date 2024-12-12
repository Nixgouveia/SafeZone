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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_warning2, parent, false);
        return new WarningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WarningViewHolder holder, int position) {
        WeatherWarning warning = warningList.get(position);
        holder.type.setText("Aviso de " + warning.getAwarenessTypeName());
        holder.level.setText("Risco " + warning.getAwarenessLevelID());
        holder.regiao.setText("Para " + setDistrict(warning.getIdAreaAviso()));
        holder.startTime.setText("Início: " + warning.getStartTime());
        holder.endTime.setText("Fim: " + warning.getEndTime());
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

    public String setDistrict(String reg) {
        if (reg.equals("BGC")) {
            return "Bragança";
        }
        if (reg.equals("AVR")) {
            return "Aveiro";
        }
        if (reg.equals("BEJ")) {
            return "Beja";
        }
        if (reg.equals("BRA")) {
            return "Braga";
        }
        if (reg.equals("CBR")) {
            return "Castelo Branco";
        }
        if (reg.equals("COI")) {
            return "Coimbra";
        }
        if (reg.equals("EVR")) {
            return "Évora";
        }
        if (reg.equals("FAR")) {
            return "Faro";
        }
        if (reg.equals("GUA")) {
            return "Guarda";
        }
        if (reg.equals("LEI")) {
            return "Leiria";
        }
        if (reg.equals("LIS")) {
            return "Lisboa";
        }
        if (reg.equals("PTG")) {
            return "Portalegre";
        }
        if (reg.equals("PRT")) {
            return "Porto";
        }
        if (reg.equals("STM")) {
            return "Santarém";
        }
        if (reg.equals("STB")) {
            return "Setúbal";
        }
        if (reg.equals("VCT")) {
            return "Viana do Castelo";
        }
        if (reg.equals("VRL")) {
            return "Vila Real";
        }
        if (reg.equals("VSE")) {
            return "Viseu";
        }
        // Caso o valor de 'reg' não seja encontrado, retorna "Desconhecido"
        return "Desconhecido";
    }

    public String colorWarning(String color){
        String colorW = " ";
        if(color.equals("yellow")){
            colorW="R.layout.item_warning2";
        } else if (color.equals("orange")) {
            colorW="R.layout.item_warning3";
        }else if (color.equals("red")) {
            colorW="R.layout.item_warning4";
        }else if (color.equals("green")) {
            colorW="R.layout.item_warning";
        }
        return colorW;
    }


}


