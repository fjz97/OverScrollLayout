package com.fjz97.overscrolllayoutdemo;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.fjz97.overscrolllayout.OverScrollLayout;

public class MainActivity extends AppCompatActivity {

    OverScrollLayout osl;
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(new MovieAdapter());

        osl = findViewById(R.id.osl);
        osl.setOnOverScrollReleaseListener(new OverScrollLayout.OnOverScrollReleaseListener() {
            @Override
            public void onRelease() {
                Toast.makeText(MainActivity.this, "回调", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {

        @NonNull
        @Override
        public MovieAdapter.MovieHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_movie, viewGroup, false);
            return new MovieHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieAdapter.MovieHolder movieHolder, int i) {
            movieHolder.iv.setImageResource(R.drawable.movie);
        }

        @Override
        public int getItemCount() {
            return 5;
        }

        class MovieHolder extends RecyclerView.ViewHolder {
            ImageView iv;

            MovieHolder(@NonNull View itemView) {
                super(itemView);
                iv = itemView.findViewById(R.id.iv_movie);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "item点击", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
