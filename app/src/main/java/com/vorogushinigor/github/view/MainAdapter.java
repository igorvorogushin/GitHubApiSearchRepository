package com.vorogushinigor.github.view;


import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vorogushinigor.github.R;
import com.vorogushinigor.github.databinding.AdapterMainBinding;
import com.vorogushinigor.github.databinding.AdapterProgressBinding;
import com.vorogushinigor.github.model.Repository;

import java.util.List;


public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int TAG_ITEM = 1;
    private final static int TAG_PROGRESS = 0;
    private List<Repository.Items> listRepository;


    @Override
    public int getItemViewType(int position) {
        return listRepository.get(position) != null ? TAG_ITEM : TAG_PROGRESS;
    }

    class ViewHolderItems extends RecyclerView.ViewHolder {
        private AdapterMainBinding binding;

        ViewHolderItems(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }


    class ViewHolderProgress extends RecyclerView.ViewHolder {
        private AdapterProgressBinding binding;

        ViewHolderProgress(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }


    MainAdapter(List<Repository.Items> listRepository) {
        this.listRepository = listRepository;
    }

    void insert(int position) {
        notifyItemInserted(position);
        notifyItemRangeChanged(position, listRepository.size());
        notifyItemChanged(position - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TAG_ITEM)
            return new ViewHolderItems(AdapterMainBinding.inflate(inflater, parent, false).getRoot());
        else
            return new ViewHolderProgress(AdapterProgressBinding.inflate(inflater, parent, false).getRoot());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderItems) {
            Repository.Items repository = listRepository.get(position);
            ((ViewHolderItems) holder).binding.setModel(repository);
            ((ViewHolderItems) holder).binding.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //on future
                }
            });
        } else {
            //for progress load
        }
    }

    @Override
    public int getItemCount() {
        return listRepository.size();
    }


    @BindingAdapter("bind:imageUrl")
    public static void loadImage(ImageView imageView, String path) {
        Picasso.with(imageView.getContext())
                .load(path)
                .error(R.drawable.ic_face_black_48dp)
                .placeholder(R.drawable.ic_face_black_48dp)
                .into(imageView);
    }

    @BindingAdapter("bind:text")
    public static void text(TextView textView, String text) {
        textView.setText(text);
    }


}
