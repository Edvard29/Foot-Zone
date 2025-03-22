package com.example.footzone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.footzone.R;
import com.example.footzone.model.Footballer;

import java.util.List;

public class FootballerAdapter extends RecyclerView.Adapter<FootballerAdapter.ViewHolder> {

    private List<Footballer> footballers;
    private static boolean isGoalScorerPage;  // Флаг, указывающий на то, показываем ли бомбардиров или ассистентов

    // Конструктор адаптера
    public FootballerAdapter(List<Footballer> footballers, boolean isGoalScorerPage) {
        this.footballers = footballers;
        this.isGoalScorerPage = isGoalScorerPage;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Если страница бомбардиров, используем item_goal_scorer.xml
        int layoutId = isGoalScorerPage ? R.layout.item_goal_scorer : R.layout.item_assist_provider;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Footballer footballer = footballers.get(position);
        holder.nameTextView.setText(footballer.getName());

        // Если это страница бомбардиров, показываем голы
        if (isGoalScorerPage) {
            holder.statTextView.setText(footballer.getGoals() + " Goals");
        } else {
            // Если это страница ассистентов, показываем ассисты
            holder.statTextView.setText(footballer.getAssists() + " Assists");
        }
    }

    @Override
    public int getItemCount() {
        return footballers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView statTextView;  // Для отображения голов или ассистов

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_view_name);
            // В зависимости от макета это будет text_view_goals или text_view_assists
            statTextView = itemView.findViewById(isGoalScorerPage ? R.id.text_view_goals : R.id.text_view_assists);
        }
    }
}
