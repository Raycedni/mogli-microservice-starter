package com.mogli.microservicebase.security.jwt;

import com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class JWTDecoder implements JwtDecoder {
    @Override
    public Jwt decode(String token) throws JwtException {
        JWT parsedJwt = null;
        try {
            parsedJwt = JWTParser.parse(token);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> headers = new LinkedHashMap<>(parsedJwt.getHeader().toJSONObject());
        Map<String, Object> claims = new HashMap<>();
        try {
//            ArrayList<String> test = (ArrayList) ((LinkedTreeMap) parsedJwt.getJWTClaimsSet().getClaims().get("realm_access")).get("roles");
//            test.forEach(o -> claims.put(o, o));
            for (String key : parsedJwt.getJWTClaimsSet().getClaims().keySet()) {
                Object value = parsedJwt.getJWTClaimsSet().getClaims().get(key);
                if (key.equals("exp") || key.equals("iat")) {
                    value = ((Date) value).toInstant();
                }
                claims.put(key, value);
            }
            claims.put("scopes", claimsAsScopes(parsedJwt));
        } catch (
                ParseException e) {
            throw new RuntimeException(e);
        }
        return Jwt.withTokenValue(token)
                .headers(h -> h.putAll(headers))
                .claims(c -> c.putAll(claims))
                .build();
    }

    private String claimsAsScopes(JWT jwt) {
        StringBuilder scopeString = new StringBuilder();
        try {
            for (String claim : (ArrayList<String>) ((LinkedTreeMap) jwt.getJWTClaimsSet().getClaims().get("realm_access")).get("roles")) {
                scopeString.append(" ").append(claim);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return scopeString.toString();
    }
}