package com.github.wubuku.aptos.utils;

import com.github.wubuku.aptos.bean.*;

import java.io.IOException;
import java.util.Map;

public class TokenUtils {
    private TokenUtils() {
    }

    public static TokenCollectionData getCollectionData(String baseUrl, String creator, String collectionName) throws IOException {
        AccountResource<Map> accountResource = NodeApiUtils.getAccountResource(baseUrl, creator,
                "0x3::token::Collections", Map.class, null);
        String handle = (String) ((Map<String, Object>) accountResource.getData().get("collection_data")).get("handle");
        return NodeApiUtils.getTableItem(baseUrl, handle,
                "0x1::string::String",
                "0x3::token::CollectionData",
                collectionName, TokenCollectionData.class, null);
    }

    public static TokenData getTokenData(String baseUrl, String creator, String collectionName,
                                         String tokenName) throws IOException {
        AccountResource<Map> accountResource = NodeApiUtils.getAccountResource(baseUrl, creator,
                "0x3::token::Collections", Map.class, null);
        String handle = (String) ((Map<String, Object>) accountResource.getData().get("token_data")).get("handle");
        TokenDataId tokenDataId = new TokenDataId(creator, collectionName, tokenName);
        return NodeApiUtils.getTableItem(baseUrl, handle,
                "0x3::token::TokenDataId",
                "0x3::token::TokenData",
                tokenDataId, TokenData.class, null);
    }

    public static Token getToken(String baseUrl,
                                 String creator, String collectionName, String tokenName,
                                 String propertyVersion) throws IOException {
        TokenDataId tokenDataId = new TokenDataId(creator, collectionName, tokenName);
        return getTokenForAccount(baseUrl, creator, newTokenId(propertyVersion, tokenDataId));
    }

    private static TokenId newTokenId(String propertyVersion, TokenDataId tokenDataId) {
        return new TokenId(tokenDataId,
                propertyVersion == null ? "0" : propertyVersion
        );
    }

    public static Token getTokenForAccount(String baseUrl,
                                           String accountAddress,
                                           String creator, String collectionName, String tokenName,
                                           String propertyVersion) throws IOException {
        TokenDataId tokenDataId = new TokenDataId(creator, collectionName, tokenName);
        return getTokenForAccount(baseUrl, accountAddress, newTokenId(propertyVersion, tokenDataId));
    }

    public static Token getTokenForAccount(String baseUrl, String accountAddress, TokenId tokenId) throws IOException {
        AccountResource<Map> accountResource = NodeApiUtils.getAccountResource(baseUrl, accountAddress,
                "0x3::token::TokenStore", Map.class, null);
        String handle = (String) ((Map<String, Object>) accountResource.getData().get("tokens")).get("handle");
        Token token = NodeApiUtils.getTableItem(baseUrl, handle,
                "0x3::token::TokenId",
                "0x3::token::Token",
                tokenId, Token.class, null);
        if (token == null) {
            return new Token(tokenId, "0", new TokenPropertyMap());
        }
        return token;
    }

}
