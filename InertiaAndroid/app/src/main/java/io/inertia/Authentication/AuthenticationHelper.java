package io.inertia.Authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import io.inertia.Application.ApplicationConstants;
import io.inertia.Models.OAuthToken;
import io.inertia.Services.InertiaApi.Web.InertiaWebApiConstants;
import io.inertia.Utilities.DateUtilities;

/**
 * Created by Luke Wilimitis on 11/5/16.
 *
 * Class designed to be used statically as an interface for services to be able to request the access token
 */
public class AuthenticationHelper {

    private static final String TAG = ApplicationConstants.APPLICATION_TAG + AuthenticationHelper.class.getSimpleName();

    private static Account sAccount;
    private static AccountManager sAccountManager;
    private static Activity sActivity;

    /**
     * Inject dependencies for use with application services
     * @param account The account for which tokens will be requested from
     * @param accountManager The instance used to request tokens from
     * @param activity The activity that the LoginActivity will be launched from
     */
    public static void setup(Account account, AccountManager accountManager, Activity activity) {
        sAccount = account;
        sAccountManager = accountManager;
        sActivity = activity;
    }

    /**
     * Request an access token from the manager, and if the token has expired, invalidate it and request a new one
     * @return
     */
    public static String getAccessToken() {
        return getAccessToken(InertiaWebApiConstants.AUTH_DEFAULT_ATTEMPTS);
    }

    /**
     * Synchronously request an access token from the manager, and if the token has expired, invalidate it and request a new one
     * @param attempts The number or attempts allowed to attempt to gain an access token
     * @return
     */
    public static synchronized String getAccessTokenSynchronized(int attempts) {

        String accessToken = null;

        while (attempts > 0) {
            try {
                // Check if the token has been cached
                accessToken = sAccountManager.peekAuthToken(sAccount, AuthenticationConstants.AUTH_ACCOUNT_TYPE);

                // If it hasn't been cached, get a new one
                if (accessToken == null) {
                    accessToken = requestAccessToken();

                    // If we can get a new one, let's cache it
                    if (accessToken != null) {
                        sAccountManager.setAuthToken(sAccount, AuthenticationConstants.AUTH_ACCOUNT_TYPE, accessToken);
                    }
                }

                // If we still don't have one, we've failed miserably
                if (TextUtils.isEmpty(accessToken)) {
                    Log.d(TAG, "failed to retrieve an access token, manager returned empty string");
                    return null;
                } else {
                    // Check if the access token is expired first
                    if (isAccessTokenExpired()) {
                        invalidateToken(accessToken);
                        attempts = attempts - 1;
                        continue;
                    }

                    return accessToken;
                }
            } catch (Exception e) {
                if (accessToken != null) {
                    invalidateToken(accessToken);
                }

                Log.d(TAG, "failed to retrieve an access token due to exception: " + e.getMessage());
                return null;
            }
        }

        Log.d(TAG, "failed to retrieve an access token, maximum number of attempts reached");
        return null;
    }

    /**
     * Request an access token from the manager, and if the token has expired, invalidate it and synchronously request a new one
     * @param attempts The number or attempts allowed to attempt to gain an access token
     * @return
     */
    public static String getAccessToken(int attempts) {

        String accessToken = null;

        while (attempts > 0) {
            try {
                // Check if the token has been cached
                accessToken = sAccountManager.peekAuthToken(sAccount, AuthenticationConstants.AUTH_ACCOUNT_TYPE);

                // If it hasn't been cached, get a new one
                if (accessToken == null) {
                    accessToken = requestAccessTokenSynchronized();
                }

                // If we still don't have one, we've failed miserably
                if (TextUtils.isEmpty(accessToken)) {
                    Log.d(TAG, "failed to retrieve an access token, manager returned empty string");
                    return null;
                } else {
                    // Check if the access token is expired first
                    if (isAccessTokenExpired()) {
                        invalidateToken(accessToken);
                        attempts = attempts - 1;
                        continue;
                    }

                    return accessToken;
                }
            } catch (Exception e) {
                if (accessToken != null) {
                    invalidateToken(accessToken);
                }

                Log.d(TAG, "failed to retrieve an access token due to exception: " + e.getMessage());
                return null;
            }
        }

        Log.d(TAG, "failed to retrieve an access token, maximum number of attempts reached");
        return null;
    }

    /**
     * Returns true if the date represented by the user data for auth token expiration
     * is sooner than the current date, otherwise false. Returns false as well if
     * the user data for auth token expiration data does not exist
     * @return
     */
    private static boolean isAccessTokenExpired() {

        String expirationDateUserData = sAccountManager.getUserData(sAccount, AuthenticationConstants.AUTH_TOKEN_EXPIRATION_DATE);

        if (expirationDateUserData != null) {
            Date expirationDate = new Date(Long.valueOf(expirationDateUserData));
            Date now = new Date();
            if (now.after(expirationDate)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Request an access token from the manager
     * @return
     * @throws AuthenticatorException
     * @throws OperationCanceledException
     * @throws IOException
     */
    private static String requestAccessToken() throws AuthenticatorException, OperationCanceledException, IOException {
        final AccountManagerFuture<Bundle> future = sAccountManager.getAuthToken(sAccount, AuthenticationConstants.AUTH_TOKEN_TYPE_FULL_ACCESS, null, sActivity, null, null);
        Bundle authTokenResult = future.getResult();
        String accessToken = authTokenResult.getString(AccountManager.KEY_AUTHTOKEN);
        return accessToken;
    }

    /**
     * Request an access token from the manager synchronously
     * @return
     * @throws AuthenticatorException
     * @throws OperationCanceledException
     * @throws IOException
     */
    private static synchronized String requestAccessTokenSynchronized() throws AuthenticatorException, OperationCanceledException, IOException {

        // First, see if another thread already cached the access token
        String accessToken = sAccountManager.peekAuthToken(sAccount, AuthenticationConstants.AUTH_ACCOUNT_TYPE);
        if (accessToken != null) {
            return accessToken;
        }

        // If it isn't already cached, let's ask the manager for one directly
        final AccountManagerFuture<Bundle> future = sAccountManager.getAuthToken(sAccount, AuthenticationConstants.AUTH_TOKEN_TYPE_FULL_ACCESS, null, sActivity, null, null);
        Bundle authTokenResult = future.getResult();
        accessToken = authTokenResult.getString(AccountManager.KEY_AUTHTOKEN);

        // If we can get a new one, let's cache it
        if (accessToken != null) {
            sAccountManager.setAuthToken(sAccount, AuthenticationConstants.AUTH_ACCOUNT_TYPE, accessToken);
        }

        return accessToken;
    }

    /**
     * Invalidate the access token for the user, by invoking the manager
     * @param token
     * @return
     */
    public static boolean invalidateToken(String token) {
        try {
            sAccountManager.invalidateAuthToken(AuthenticationConstants.AUTH_ACCOUNT_TYPE, token);
        } catch (Exception e) {
            Log.d(TAG, "failed to invalidate auth token");
            return false;
        }
        return true;
    }

    /**
     * Invalidate the current access token held by the manager
     * @return
     */
    public static void invalidateCurrentToken() {
        try {
            String accessToken = sAccountManager.peekAuthToken(sAccount, AuthenticationConstants.AUTH_ACCOUNT_TYPE);
            if (accessToken == null) {
                accessToken = requestAccessToken();
            }
            invalidateToken(accessToken);
        } catch (Exception e) {
            Log.d(TAG, "failed to invalidate current cached access token: " + e.getMessage());
        }
    }

    /**
     * Convert an OAuthToken object into an AccessToken and return it
     * @param oAuthToken
     * @return The converted AccessToken
     */
    public static AccessToken convertToken(OAuthToken oAuthToken) {
        AccessToken accessToken = new AccessToken();
        accessToken.setAccessToken(oAuthToken.getAccessToken());
        accessToken.setRefreshToken(oAuthToken.getRefreshToken());
        accessToken.setExpirationDate(DateUtilities.addIntervalAmount(
                new Date(), Calendar.SECOND, Integer.valueOf(oAuthToken.getExpiresIn()) - AuthenticationConstants.AUTH_TOKEN_EXPIRATION_PADDING_SECONDS));
        return accessToken;
    }
}
