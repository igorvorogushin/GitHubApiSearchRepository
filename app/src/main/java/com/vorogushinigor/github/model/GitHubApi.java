package com.vorogushinigor.github.model;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface GitHubApi {
    @GET("/search/repositories")
    Observable<Repository> getRepository(@Query("q") String name,@Query("sort") String sort, @Query("order") String order,@Query("page") int page);
}
