package util;
import beans.User;
import io.jsonwebtoken.*;

import java.util.Date;

public class JWTUtil {

    private static final String SECRET_KEY = "12345678910111213141516171819202122232425262728293031321";


    public static String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static Claims extractClaims(String token) {
        JwtParser jwtParser = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build();
        Jws<Claims> jwsClaims = jwtParser.parseClaimsJws(token);
        return jwsClaims.getBody();
    }

    public static String extractUsername(String token) {
        try {
            return extractClaims(token).getSubject();
        }catch (Exception exc){
            return "";
        }
    }

    public static boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public static boolean validateToken(String token, User user) {
        return (user.getUsername().equals(extractUsername(token)) && !isTokenExpired(token));
    }
}
