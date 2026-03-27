package com.example.rivarly.util;

import com.example.rivarly.entity.Person;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A utility class for handling JSON Web Tokens (JWT). This class provides methods for generating
 * JWT tokens, extracting claims, and validating tokens. It utilizes a secret key injected from
 * the application's configuration to ensure secure signing and verification of tokens.
 */
@Component
public class JwtUtil {

    /**
     * Represents the secret key used for signing and verifying JWT tokens.
     * This value is injected from the application configuration property 'jwt.secretKey'.
     * The secret key ensures the integrity and authenticity of JWT tokens by acting as the signing key
     * in operations such as token generation and validation.
     */
    @Value("${jwt.secretKey}")
    private String secretKey;

    /**
     * Represents the expiration time for JWT tokens, specified in milliseconds.
     * This value is injected from the application configuration property 'jwt.expirationTime'.
     * It defines the duration after which a generated JWT token becomes invalid and can no longer be used.
     */
    @Value("${jwt.expirationMs}")
    private long jwtExpirationTime;

    @Value("${jwt.refreshExpirationMs}")
    private long refreshExpirationTime;

    /**
     * Generates a JWT (JSON Web Token) for a given person based on their unique identifier and privileges. 
     * The method collects relevant claims including the person's ID and privilege names, then creates a signed token.
     *
     * @param person the Person object containing the details such as ID, nickname, and privileges to create the token
     * @return a signed JWT token as a String containing the person's claims
     */
    public String generateToken(Person person) {
        Map<String, Object> claims = new HashMap<>();

        //Part with person Id
        claims.put("personId", person.getPersonID());

        //Part with person privilege
        claims.put("privilege", person.getPrivileges().stream()
                .map(privilege-> privilege.getPrivilegeName())
                .collect(Collectors.toList()));

        return createToken(claims, person.getNickname());
    }

    /**
     * Creates a JWT (JSON Web Token) by encoding the provided claims and subject into a signed and compact token.
     *
     * @param claims a map containing key-value pairs representing the claims to be included in the token
     * @param subject the subject or principal for which the token is being generated
     * @return a signed JWT token as a compacted String
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public ResponseCookie generateRefreshJwtCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/api/auth/refreshtoken")
                .maxAge(refreshExpirationTime)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie getCleanRefreshJwtCookie() {
        return ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .path("/api/auth/refreshtoken")
                .maxAge(0) // Миттєве видалення
                .build();
    }



    /**
     * Retrieves the signing key used for generating and verifying JWT tokens.
     * The key is derived from a secret key constant and utilizes HMAC SHA algorithm.
     *
     * @return the secret signing key as a Key object for token operations
     */
    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Extracts the nickname (subject) from a given JWT token.
     *
     * @param token the JWT token from which to extract the nickname
     * @return the nickname stored in the token's subject claim
     */
    public String getNicknameFromJWT(String token){
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from a JWT token using the provided claims resolver function.
     *
     * @param token          the JWT token from which to extract the claim
     * @param claimsResolver a function that specifies how to extract and transform the desired claim
     * @param <T>            the type of the claim value to be returned
     * @return the extracted claim value of type T
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from a JWT token by parsing and verifying it with the signing key.
     *
     * @param token the JWT token to parse and extract claims from
     * @return Claims object containing all the claims present in the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if a given JWT token has expired by comparing its expiration date with the current date.
     *
     * @param token the JWT token to check for expiration
     * @return true if the token has expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from a given JWT (JSON Web Token).
     *
     * @param token the JWT as a String from which the expiration date is to be extracted
     * @return the expiration date of the token as a Date object
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts the username from a given JWT (JSON Web Token) by retrieving the subject claim.
     *
     * @param token the JWT token from which to extract the username
     * @return the username stored in the token's subject claim as a String
     */
    public String extractUsername(String token) {
        return extractClaim(token, claims -> claims.getSubject());
    }

    /**
     * Verifies the validity of a provided JWT (JSON Web Token) by checking its username
     * and whether it has expired.
     *
     * @param token       the JWT token to validate
     * @param userDetails the user details used to verify the token's username
     * @return true if the token is valid (username matches and token is not expired), false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try{
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        }
        catch (ExpiredJwtException e){
            return false;
        }
    }
}
