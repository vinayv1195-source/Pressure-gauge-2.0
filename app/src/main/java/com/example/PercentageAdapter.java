package com.example;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PercentageAdapter extends RecyclerView.Adapter<PercentageAdapter.ViewHolder> {

    public interface OnPercentageSelectedListener {
        void onPercentageSelected(PressurePercentageCalculator.PercentageItem item);
    }

    private final List<PressurePercentageCalculator.PercentageItem> items = new ArrayList<>();
    private int selectedPercentage = 50;
    private OnPercentageSelectedListener listener;

    public void setItems(List<PressurePercentageCalculator.PercentageItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public void setOnPercentageSelectedListener(OnPercentageSelectedListener listener) {
        this.listener = listener;
    }

    public void setSelectedPercentage(int percentage) {
        this.selectedPercentage = percentage;
        notifyDataSetChanged();
    }

    public int getSelectedPercentage() {
        return selectedPercentage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_percentage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PressurePercentageCalculator.PercentageItem item = items.get(position);
        boolean isSelected = (item.getPercentage() == selectedPercentage);

        holder.tvPercentBadge.setText(item.getPercentage() + "%");
        holder.progressScale.setProgress(item.getPercentage());
        holder.tvPressureValue.setText(item.getFormattedPressure());
        holder.tvUnitLabel.setText(item.getUnit());

        if (isSelected) {
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_card_highlight));
            holder.tvPercentBadge.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_badge_percent_active));
            holder.tvPercentBadge.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            holder.tvPressureValue.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.cyan_accent));
            holder.progressScale.setProgressTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.cyan_accent));
        } else {
            holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_card_industrial));
            holder.tvPercentBadge.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_badge_percent));
            holder.tvPercentBadge.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_primary));
            holder.tvPressureValue.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_blue_result));
            holder.progressScale.setProgressTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.electric_blue));
        }

        holder.itemView.setOnClickListener(v -> {
            setSelectedPercentage(item.getPercentage());
            if (listener != null) {
                listener.onPercentageSelected(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public int findPositionForPercentage(int percent) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getPercentage() == percent) {
                return i;
            }
        }
        return -1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvPercentBadge;
        final ProgressBar progressScale;
        final TextView tvPressureValue;
        final TextView tvUnitLabel;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPercentBadge = itemView.findViewById(R.id.tvPercentBadge);
            progressScale = itemView.findViewById(R.id.progressScale);
            tvPressureValue = itemView.findViewById(R.id.tvPressureValue);
            tvUnitLabel = itemView.findViewById(R.id.tvUnitLabel);
        }
    }
}
