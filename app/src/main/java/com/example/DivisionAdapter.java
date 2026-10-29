package com.example;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DivisionAdapter extends RecyclerView.Adapter<DivisionAdapter.ViewHolder> {

    private final List<GaugeDivisionCalculator.DivisionItem> items = new ArrayList<>();

    public void setItems(List<GaugeDivisionCalculator.DivisionItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_division, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GaugeDivisionCalculator.DivisionItem item = items.get(position);
        holder.tvDivisionMarkIndex.setText("Div #" + item.getDivisionNumber());
        holder.tvDivisionMarkValue.setText(item.getDisplayText());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvDivisionMarkIndex;
        final TextView tvDivisionMarkValue;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDivisionMarkIndex = itemView.findViewById(R.id.tvDivisionMarkIndex);
            tvDivisionMarkValue = itemView.findViewById(R.id.tvDivisionMarkValue);
        }
    }
}
