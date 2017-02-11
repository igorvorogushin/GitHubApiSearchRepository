package com.vorogushinigor.github.view;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vorogushinigor.github.R;
import com.vorogushinigor.github.databinding.FragmentMainBinding;
import com.vorogushinigor.github.model.Repository;
import com.vorogushinigor.github.viewmodel.ViewModelMain;

import java.util.List;


public class MainFragment extends Fragment implements ViewModelMain.CallBack {

    private FragmentMainBinding binding;
    private ViewModelMain viewModel;
    private LinearLayoutManager linearLayoutManager;
    private MainAdapter mainAdapter;


    private int totalItems;
    private int currentItems;
    private String line;
    private int page;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        View view = binding.getRoot();
        linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerview.setLayoutManager(linearLayoutManager);
        binding.recyclerview.addOnScrollListener(scrollListener);
        if (viewModel == null)
            viewModel = new ViewModelMain();
        viewModel.setCallBack(this);
        binding.setVm(viewModel);
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onUnSubscribe();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (viewModel != null) viewModel.onSubscribe();
    }

    public void filter(String line, int page) {
        this.line = line;
        this.page = page;
        viewModel.query(line, page);
    }

    @Override
    public void initData(int totalItems, List<Repository.Items> listItems) {
        this.totalItems = totalItems;
        this.currentItems = listItems.size();
        mainAdapter = new MainAdapter(listItems);
        binding.recyclerview.setAdapter(mainAdapter);
    }

    @Override
    public void updateAdapter(int pos, int size) {
        page++;
        currentItems = size;
        mainAdapter.insert(pos);
    }

    @Override
    public void messageError(String message) {
        //on future
        Toast toast = Toast.makeText(getActivity(), getString(R.string.error) + ": " + message, Toast.LENGTH_SHORT);
        toast.show();
    }


    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = linearLayoutManager.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            int firstVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

            if (!viewModel.isLoading()) {
                if ((visibleItemCount + firstVisibleItems) >= totalItemCount - 3) {
                    if (totalItems != currentItems) {
                        viewModel.query(line, page + 1);
                    }
                }
            }
        }
    };

}
