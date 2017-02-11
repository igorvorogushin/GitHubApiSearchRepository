package com.vorogushinigor.github.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableBoolean;
import android.util.Log;

import com.vorogushinigor.github.model.GitHubApi;
import com.vorogushinigor.github.model.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ViewModelMain extends BaseObservable implements ViewModel {

    private final static String TAG_LOG = ViewModelMain.class.getName();
    private final static String URL = "https://api.github.com/";
    private final static String TYPE_SORT_STARS = "stars";
    private final static String TYPE_SORT_FORKS = "forks";
    private final static String TYPE_SORT_UPDATED = "updated";
    private final static String TYPE_ORDER_DESC = "desc";
    private final static String TYPE_ORDER_ASC = "asc";


    private int totalItems;
    private List<Repository.Items> listItems;
    private GitHubApi gitHubApi;
    private Observable<Repository> observable;
    private Subscription subscription;
    private Subscriber<Repository> subscriber;
    private CallBack callBack;
    private int page;

    private boolean loading = false;

    @Bindable
    public final ObservableBoolean visibleRecyclerView = new ObservableBoolean();

    public boolean isLoading() {
        return loading;
    }

    @Override
    public void onSubscribe() {
        if (callBack != null && listItems != null)
            if (listItems.size() > 0) callBack.initData(totalItems, listItems);
    }

    @Override
    public void onUnSubscribe() {
        callBack = null;
        loading = false;
    }

    public interface CallBack {
        void initData(int totalItems, List<Repository.Items> listItems);

        void updateAdapter(int pos, int size);

        void messageError(String message);
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public ViewModelMain() {
        createRetrofit();
    }

    public void query(String name, int page) {
        this.page = page;
        name = name.trim();

        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();

        if (name.equals("")) {
            visibleRecyclerView.set(false);
            return;
        }

        Log.v(TAG_LOG, "text: " + name + " page:" + String.valueOf(page));
        visibleRecyclerView.set(true);
        loading = true;

        createSubscriber();
        createObservable(name, TYPE_SORT_STARS, TYPE_ORDER_DESC, page);
        start();
    }

    private void createRetrofit() {
        gitHubApi = new Retrofit.Builder()
                .baseUrl(URL).client(getOkHttpClient())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(GitHubApi.class);
    }

    private void createSubscriber() {
        subscriber = new Subscriber<Repository>() {
            @Override
            public void onCompleted() {
                Log.v(TAG_LOG, "onCompleted");
                loading = false;
            }

            @Override
            public void onStart() {
                super.onStart();
                Log.v(TAG_LOG, "onStart");
            }

            @Override
            public void onError(Throwable e) {
                Log.v(TAG_LOG, "onError " + e.getMessage());
                if (callBack != null) callBack.messageError(e.getMessage());
                loading = false;
            }

            @Override
            public void onNext(Repository repository) {
                Log.v(TAG_LOG, "onNext");
                totalItems = repository.getTotal();


                if (page != 1) {
                    int pos = listItems.size();

                    //delete progress bar
                    if (listItems.get(listItems.size() - 1) == null)
                        listItems.remove(listItems.size() - 1);

                    listItems.addAll(repository.getListItems());

                    //add progress bar
                    if (totalItems != listItems.size())
                        listItems.add(null);

                    if (callBack != null) callBack.updateAdapter(pos, listItems.size());
                } else {
                    listItems = repository.getListItems();

                    //add progress bar
                    if (totalItems != listItems.size())
                        listItems.add(null);

                    if (callBack != null) callBack.initData(totalItems, listItems);
                }
                Log.v(TAG_LOG, "total: " + String.valueOf(totalItems) + " current: " + String.valueOf(listItems.size()));

            }
        };

    }

    private void createObservable(String name, String sort, String order, int page) {
        observable = gitHubApi.getRepository(name, sort, order, page);
    }

    private void start() {
        subscription = observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread()).subscribe(subscriber);
    }

    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
    }


}