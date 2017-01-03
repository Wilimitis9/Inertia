package io.inertia.Services.InertiaApi.Web.Modules;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.inertia.Services.InertiaApi.Web.Definitions.MessageWebApi;
import io.inertia.Services.InertiaApi.Web.Definitions.OAuthTokenWebApi;
import io.inertia.Services.InertiaApi.Web.InertiaWebApiAuthenticator;
import io.inertia.Services.InertiaApi.Web.InertiaWebApiConstants;
import io.inertia.Services.InertiaApi.Web.InertiaWebApiInterceptor;
import io.inertia.Services.InertiaApi.Web.OAuthTokenApiInterceptor;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by root on 11/14/16.
 */
@Module
public class InertiaWebApiModule {

    private Context context;

    public InertiaWebApiModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().
                addNetworkInterceptor(new InertiaWebApiInterceptor()).
                authenticator(new InertiaWebApiAuthenticator()).
                build();
        return okHttpClient;
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InertiaWebApiConstants.BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    @Provides
    @Singleton
    OAuthTokenWebApi provideOAuthTokenApi() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().
                addNetworkInterceptor(new OAuthTokenApiInterceptor()).
                build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InertiaWebApiConstants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(OAuthTokenWebApi.class);
    }

    @Provides
    @Singleton
    MessageWebApi provideUserApi(Retrofit retrofit) {
        return retrofit.create(MessageWebApi.class);
    }
}
