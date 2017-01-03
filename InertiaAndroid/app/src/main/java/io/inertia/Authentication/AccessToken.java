package io.inertia.Authentication;

import java.util.Date;

/**
 * Created by Luke Wilimitis on 10/26/16.
 */
public class AccessToken {
    private String accessToken;
    private String refreshToken;
    private Date expirationDate;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
