package com.example.weightapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class WeightsAdapter extends RecyclerView.Adapter<WeightsAdapter.WeightViewHolder> {

    private Context context;
    private List<Weight> weights;
    private OnWeightListener onWeightListener;

    public WeightsAdapter(Context context, List<Weight> weights, OnWeightListener onWeightListener) {
        this.context = context;
        this.weights = weights;
        this.onWeightListener = onWeightListener;
    }

    @Override
    public WeightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weight_item, parent, false);
        return new WeightViewHolder(view, onWeightListener);
    }

    @Override
    public void onBindViewHolder(WeightViewHolder holder, int position) {
        Weight weight = weights.get(position);
        holder.dateTextView.setText(weight.getDate());
        holder.weightTextView.setText(weight.getWeight());
    }

    @Override
    public int getItemCount() {
        return weights.size();
    }

    public static class WeightViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dateTextView, weightTextView;
        ImageButton deleteButton, editButton;
        OnWeightListener onWeightListener;

        public WeightViewHolder(View itemView, OnWeightListener onWeightListener) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            weightTextView = itemView.findViewById(R.id.weightTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
            this.onWeightListener = onWeightListener;
            deleteButton.setOnClickListener(this);
            editButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.deleteButton) {
                onWeightListener.onDeleteClick(getAdapterPosition());
            } else if (view.getId() == R.id.editButton) {
                onWeightListener.onEditClick(getAdapterPosition());
            }
        }
    }

    public interface OnWeightListener {
        void onDeleteClick(int position);
        void onEditClick(int position);
    }

}

