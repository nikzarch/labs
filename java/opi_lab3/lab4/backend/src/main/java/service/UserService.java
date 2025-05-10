package  service;

import beans.User;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.security.MessageDigest;





@Stateless
public class UserService {
    @PersistenceContext(unitName = "db")
    private EntityManager em;

    public User findUserById(Long id) {
        return em.find(User.class, id);
    }

    public User findUserByUsername(String username) {
        try {
            return em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    public User authenticate(String username, String password) {
        password = hashPassword(password);
        try {
            User user = em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();

            if (user != null && user.getPassword().equals(password)) {
                return user;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
    public boolean userExists(String username) {
        long count = em.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }
    public User createUser(String username, String password) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(hashPassword(password));
        em.persist(newUser);
        return newUser;
    }

    public String hashPassword(String password)  {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        }catch (Exception exc){
            exc.printStackTrace();
        }
        return password;
    }
}
