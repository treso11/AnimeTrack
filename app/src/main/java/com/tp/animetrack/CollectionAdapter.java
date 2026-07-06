package com.tp.animetrack;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

    private List<Anime> animeList = new ArrayList<>();
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position, Anime anime);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position, Anime anime);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setAnimeList(List<Anime> list) {
        this.animeList = list;
        notifyDataSetChanged();
    }

    public Anime getAnimeAt(int position) {
        return animeList.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_anime_collection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Anime anime = animeList.get(position);

        holder.tvTitle.setText(anime.getTitle());
        holder.tvStatus.setText(anime.getStatus());
        holder.ratingBar.setRating(anime.getRating());

        // Chargement de l'image avec GLIDE
        String posterUrl = anime.getPosterUrl();
        if (posterUrl != null && !posterUrl.isEmpty()) {
            String fullUrl = "https://image.tmdb.org/t/p/w185" + posterUrl;
            Glide.with(holder.itemView.getContext())
                    .load(fullUrl)
                    .placeholder(null)
                    .error(android.R.drawable.ic_menu_gallery)  // icône Android native
                    .centerCrop()
                    .into(holder.ivPoster);
        } else {
            // Utilise la même icône Android native par défaut
            holder.ivPoster.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Clic normal
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(position, anime);
            }
        });

        // Long clic pour menu contextuel
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(position, anime);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return animeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle;
        TextView tvStatus;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}