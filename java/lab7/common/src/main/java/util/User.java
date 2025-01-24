package util;

import java.io.Serializable;

public class User  implements Serializable {

    private String name;
    private String password;

    public User(String userName, String userPassword){
        this.name = userName;
        this.password = userPassword;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
