package com.ensa.domain;

import java.io.Serializable;

public class TokenVerify implements Serializable {
    private static final long serialVersionUID = 1L;

    public TokenVerify() {
    }

    private String token;
    private String tokenId;

    public TokenVerify(String token, String tokenId) {
        this.token = token;
        this.tokenId = tokenId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }
}
