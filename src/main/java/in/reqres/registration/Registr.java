package in.reqres.registration;

public class Registr {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Registr(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
