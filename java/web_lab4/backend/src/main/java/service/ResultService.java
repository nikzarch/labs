package service;

import beans.Result;
import beans.User;
import db.Results;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.List;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;


@Stateless
@Path("/points")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ResultService {
    @PersistenceContext(unitName = "db")
    private EntityManager em;
    @EJB
    private Results results;
    public Result saveResult(User user, double x, double y, double r, boolean isHit, String timeNow, long executionTime) {
        Result result = new Result();
        result.setUser(user);
        result.setX(x);
        result.setY(y);
        result.setR(r);
        result.setHit(isHit);
        result.setTimeNow(timeNow);
        result.setExecutionTime(executionTime);
        em.persist(result);
        return result;
    }
    public List<Result> getResultsForUser(User user) {
        TypedQuery<Result> query = em.createQuery("SELECT r FROM Result r WHERE r.user = :user", Result.class);
        query.setParameter("user", user);
        return query.getResultList();
    }


    public boolean checkHit(double x, double y, double r){
            r = Math.abs(r);
            if (x >= 0) {
                if (y <= 0) {
                    return (y >= -r / 2) && (x <= r); // circle
                } else {
                    return (y <= r - 2 * x); // triangle
                }
            } else {
                if (y > 0) {
                    return false; // 2 quarter
                } else {
                    return ((x * x) + (y * y) <= (r / 2) * (r / 2)); // circle
                }
            }
    }

    public void clearResultsForUser(User user) {
        Query query = em.createQuery("DELETE FROM Result r WHERE r.user = :user");
        query.setParameter("user", user);
        int deletedCount = query.executeUpdate();
        System.out.println("Deleted " + deletedCount + " results for user " + user.getUsername());
    }

}
