package io.inertia.Services.InertiaApi.Web.Definitions;

import io.inertia.Models.OAuthToken;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Luke Wilimitis on 11/13/16.
 */
public interface OAuthTokenWebApi {

    @POST("o/token/")
    @Headers({
            "charset: utf-8",
            "Content-Type: application/x-www-form-urlencoded"
    })
    Call<OAuthToken> getOAuthToken(
            @Query("username") String username,
            @Query("password") String password,
            @Query("grant_type") String grantType);

    @POST("o/token/")
    @Headers({
            "charset: utf-8",
            "Content-Type: application/x-www-form-urlencoded"
    })
    Call<OAuthToken> getOAuthToken(
            @Query("refresh_token") String refreshToken,
            @Query("grant_type") String grantType);
}
