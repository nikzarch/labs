package auth;

import beans.User;
import com.google.gson.Gson;
import dto.AuthResponse;
import dto.LoginRequest;
import dto.RegisterRequest;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import service.UserService;
import util.JWTUtil;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;

@Path("/auth")
public class LoginResource {

    @EJB
    private UserService userService;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        System.out.println("got user " + username + " to login");
        User user = userService.authenticate(username, password);
        if (user != null) {
            String token = JWTUtil.generateToken(user);
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(new AuthResponse(token));
            System.out.println("authorized successfuly with token: " + token);
            return Response.ok(jsonResponse).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\": \"Invalid username or password\"}")
                    .build();
        }
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        System.out.println("got user " + username + " to register");
        if (userService.userExists(username)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"User already exists\"}")
                    .build();
        }
        User newUser = userService.createUser(username, password);
        String token = JWTUtil.generateToken(newUser);
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(new AuthResponse(token));
        System.out.println("registered successfuly with token: " + token);
        return Response.status(Response.Status.CREATED).entity(jsonResponse).build();
    }
}
