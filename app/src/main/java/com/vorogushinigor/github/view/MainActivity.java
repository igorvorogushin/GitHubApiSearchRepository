package com.vorogushinigor.github.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.vorogushinigor.github.R;
import com.vorogushinigor.github.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private static final String TAG_FRAGMENT_MAIN = MainFragment.class.getName();
    private static final String TAG_SAVED_SEARCH_VIEW = "search view";
    private static final String TAG_SAVED_SEARCH_VIEW_SHOW = "search view show";
    private static final String TAG_SAVED_SEARCH_FOCUS = "search view focus";
    private String currentQuery;
    private boolean searchViewDontShow = true;
    private boolean keyBoardShow = false;
    private SearchView searchView;


    private MainFragment fragmentMain;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        startFragment();

    }

    private void startFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentMain = (MainFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_MAIN);
        if (fragmentMain == null) {
            fragmentMain = new MainFragment();
            fragmentMain.setRetainInstance(true);
        }
        fragmentTransaction.replace(R.id.framelayout, fragmentMain, TAG_FRAGMENT_MAIN);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_items_search);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.menu_search));
        if (!TextUtils.isEmpty(currentQuery)) {
            searchItem.expandActionView();
            searchView.setQuery(currentQuery, false);
        }
        searchView.setIconified(searchViewDontShow);

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                keyBoardShow = hasFocus;
            }
        });

        if (!keyBoardShow)
            searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                if (fragmentMain != null)
                    fragmentMain.filter(newText, 1);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (fragmentMain != null)
                    fragmentMain.filter(newText, 1);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentQuery = savedInstanceState.getString(TAG_SAVED_SEARCH_VIEW);
        searchViewDontShow = savedInstanceState.getBoolean(TAG_SAVED_SEARCH_VIEW_SHOW);
        keyBoardShow = savedInstanceState.getBoolean(TAG_SAVED_SEARCH_FOCUS);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(TAG_SAVED_SEARCH_VIEW, searchView.getQuery().toString());
        outState.putBoolean(TAG_SAVED_SEARCH_VIEW_SHOW, searchView.isIconified());
        outState.putBoolean(TAG_SAVED_SEARCH_FOCUS, keyBoardShow);
        super.onSaveInstanceState(outState);
    }
}
