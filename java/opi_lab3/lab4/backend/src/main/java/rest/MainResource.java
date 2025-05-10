package rest;

import beans.Result;
import beans.User;

import com.google.gson.Gson;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import dto.PointRequest;
import service.ResultService;
import service.UserService;

import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.*;
import util.JWTUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Path("/main")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MainResource {

    @EJB
    private UserService userService;

    @EJB
    private ResultService resultService;

    @DELETE
    @Path("/clear/{userToken}")
    public Response clear(@PathParam("userToken") String token){
        try {
            resultService.clearResultsForUser(userService.findUserByUsername(JWTUtil.extractUsername(token)));
            return Response.ok().build();
        }catch (Exception exc){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Can't clear").build();
        }
    }

    @POST
    @Path("/submit-point")
    public Response submitPoint(PointRequest request) {
        Long now = System.nanoTime();
        Gson gson = new Gson();

        if (JWTUtil.extractUsername(request.getUserToken()).isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("User not found").build();
        }
        System.out.println(gson.toJson(request));
        User user =  userService.findUserByUsername(JWTUtil.extractUsername(request.getUserToken()));
        boolean isHit = resultService.checkHit(request.getX(), request.getY(), request.getR());
        Result result = resultService.saveResult(user, request.getX(), request.getY(), request.getR(), isHit, LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")), System.nanoTime()-now);
        System.out.println(gson.toJson(result));
        return Response.ok(gson.toJson(result)).build();
    }

    @GET
    @Path("/results/{userToken}")
    public Response getResults(@PathParam("userToken") String userToken) {
        User user = userService.findUserByUsername(JWTUtil.extractUsername(userToken));
        System.out.println(JWTUtil.extractUsername(userToken) + " is getting his results");
        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("User not found, please re-login").build();
        }

        List<Result> results = resultService.getResultsForUser(user);
        results.forEach(result -> result.setUser(null));
        Gson gson = new Gson();
        return Response.ok(gson.toJson(results)).build();
    }
}
