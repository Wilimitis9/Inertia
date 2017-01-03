package io.inertia.Services.InertiaApi.Web;

import java.io.IOException;

import io.inertia.Authentication.AuthenticationHelper;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by root on 11/16/16.
 */

public class InertiaWebApiInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Add an auth token
        String accessToken = AuthenticationHelper.getAccessToken();
        Request authorisedRequest = originalRequest.newBuilder()
                .header(InertiaWebApiConstants.AUTH_HEADER, InertiaWebApiConstants.AUTH_HEADER_BEARER + " " + accessToken)
                .build();

        return chain.proceed(authorisedRequest);
    }
}
