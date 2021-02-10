package com.fjz97.overscrolllayoutdemo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.fjz97.overscrolllayout.OverScrollLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    OverScrollLayout osl;
    RecyclerView rv;
    MovieAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(adapter = new MovieAdapter());

        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                final int dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                if (from < to) {
                    for (int i = from; i < to; i++) {
                        Collections.swap(adapter.data, i, i + 1);
                    }
                } else if (from > to) {
                    for (int i = from; i > to; i--) {
                        Collections.swap(adapter.data, i, i - 1);
                    }
                }
                adapter.notifyItemMoved(from, to);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                adapter.data.remove(position);
                adapter.notifyItemRemoved(position);
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                osl.enableOverScroll();
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                osl.disableOverScroll();
            }
        });
        //helper.attachToRecyclerView(rv);

        osl = findViewById(R.id.osl);
        osl.setOnOverScrollReleaseListener(new OverScrollLayout.OnOverScrollReleaseListener() {
            @Override
            public void onRelease() {
                Toast.makeText(MainActivity.this, "回调", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {

        List<Integer> data = new ArrayList<>();

        MovieAdapter() {
            data.add(R.drawable.post1);
            data.add(R.drawable.post2);
            data.add(R.drawable.post3);
            data.add(R.drawable.post4);
            data.add(R.drawable.post5);
        }

        @NonNull
        @Override
        public MovieAdapter.MovieHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_movie, viewGroup, false);
            return new MovieHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieAdapter.MovieHolder movieHolder, int i) {
            movieHolder.iv.setImageResource(data.get(i));
        }

        @Override
        public int getItemCount() {
            return data.size();
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
//                iv.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        Toast.makeText(MainActivity.this, "长按", Toast.LENGTH_SHORT).show();
//                        return true;
//                    }
//                });
            }
        }
    }
}
