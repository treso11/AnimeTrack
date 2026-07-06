package com.tp.animetrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private Context context;
    private List<AnimeSearchResult> results;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AnimeSearchResult anime);
    }

    public SearchResultAdapter(Context context, List<AnimeSearchResult> results, OnItemClickListener listener) {
        this.context = context;
        this.results = results;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnimeSearchResult anime = results.get(position);
        holder.tvTitle.setText(anime.getTitle());

        String year = anime.getReleaseDate();
        if (year != null && year.length() >= 4) {
            holder.tvYear.setText(year.substring(0, 4));
        } else {
            holder.tvYear.setText("Date inconnue");
        }

        holder.tvRating.setText(String.valueOf(anime.getVoteAverage()));

        String posterUrl = "https://image.tmdb.org/t/p/w185" + anime.getPosterPath();
        Glide.with(context)
                .load(posterUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivPoster);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(anime));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public void updateResults(List<AnimeSearchResult> newResults) {
        this.results = newResults;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle, tvYear, tvRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvYear = itemView.findViewById(R.id.tvYear);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }
}