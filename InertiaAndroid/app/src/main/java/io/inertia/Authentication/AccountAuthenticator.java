package io.inertia.Authentication;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import javax.inject.Inject;

import io.inertia.Activities.LoginActivity;
import io.inertia.Application.ApplicationConstants;
import io.inertia.Application.InertiaApplication;
import io.inertia.Models.OAuthToken;
import io.inertia.Services.InertiaApi.Web.Definitions.OAuthTokenWebApi;
import io.inertia.Services.InertiaApi.Web.InertiaWebApiConstants;

/**
 * Created by Luke Wilimitis on 10/16/16.
 */
public class AccountAuthenticator extends AbstractAccountAuthenticator {

    private final String TAG = ApplicationConstants.APPLICATION_TAG + this.getClass().getSimpleName();

    private final Context mContext;

    @Inject
    OAuthTokenWebApi mOAuthTokenWebApi;

    public AccountAuthenticator(Context context) {
        super(context);
        mContext = context;

        InertiaApplication.getInertiaWebApiComponent().inject(this);
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Log.d(TAG, "addAccount started");

        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(LoginActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(LoginActivity.ARG_AUTH_TOKEN_TYPE, authTokenType);
        // TODO Figure out if we need this arg
        intent.putExtra(LoginActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        Log.d(TAG, "getAuthToken started");

        final Bundle bundle = new Bundle();

        // Error out if we get a request for a token we don't support
        if (!authTokenType.equals(AuthenticationConstants.AUTH_TOKEN_TYPE_FULL_ACCESS)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }

        // Extract the username and password (refresh token) and ask server for an AuthToken
        final AccountManager accountManager = AccountManager.get(mContext);

        // If we have a refresh token, try to use it to get a new access token
        final String refreshToken = accountManager.getPassword(account);
        if (refreshToken != null) {
            Log.d(TAG, "getAuthToken try to get another access token via the refresh token");
            try {
                OAuthToken oAuthToken = mOAuthTokenWebApi.getOAuthToken(refreshToken, InertiaWebApiConstants.GRANT_TYPE_REFRESH_TOKEN).execute().body();

                // If we get a valid response, return the auth token to the caller
                if (oAuthToken != null && oAuthToken.getAccessToken() != null) {
                    AccessToken accessToken = AuthenticationHelper.convertToken(oAuthToken);
                    bundle.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                    bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, AuthenticationConstants.AUTH_ACCOUNT_TYPE);
                    bundle.putString(AccountManager.KEY_AUTHTOKEN, accessToken.getAccessToken());
                    accountManager.setPassword(account, accessToken.getRefreshToken());
                    accountManager.setUserData(account, AuthenticationConstants.AUTH_TOKEN_EXPIRATION_DATE, String.valueOf(accessToken.getExpirationDate().getTime()));
                    return bundle;
                }
            } catch (Exception e) {
                Log.d(TAG, "getAuthToken exception while attempting to refresh access token: " + e.getMessage());
                //e.printStackTrace();
            }
        }

        // If we don't have a refresh token, start the login activity
        Log.d(TAG, "getAuthToken starting login activity because we couldn't refresh the access token :/");
        final Intent intent = new Intent(mContext, LoginActivity.class);
        intent.putExtra(LoginActivity.ARG_ACCOUNT_NAME, account.name);
        intent.putExtra(LoginActivity.ARG_AUTH_TOKEN_TYPE, authTokenType);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        if (AuthenticationConstants.AUTH_TOKEN_TYPE_FULL_ACCESS.equals(authTokenType)) {
            return AuthenticationConstants.AUTH_TOKEN_TYPE_FULL_ACCESS_LABEL;
        }
        else {
            return authTokenType + " (Label)";
        }
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        return null;
    }
}
