package com.javigabe.filmtracker;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.javigabe.filmtracker.resources.Film;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private ArrayList<Film> films;

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        public ImageView poster;
        public TextView filmName;
        public TextView filmGenre;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.poster);
            filmName = itemView.findViewById(R.id.filmName);
            filmGenre = itemView.findViewById(R.id.filmGenre);
        }
    }

    public HomeAdapter(ArrayList<Film> films) {
        this.films = films;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items, parent,false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Film film = films.get(position);

        if (film.getPoster() != null) {
            Log.e("ADAPTER", "Entra");
            holder.poster.setImageBitmap(film.getPoster());
        } else {
            holder.poster.setImageResource(R.drawable.movie);
        }
        holder.filmName.setText(film.getName());
        holder.filmGenre.setText(film.getGenre());
    }

    @Override
    public int getItemCount() {
        return films.size();
    }
}
