package in.reqres.registration;

public class SuccesReg {
    private Integer id;
    private String token;

    public int getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public SuccesReg() {
    }

    public SuccesReg(int id, String token) {
        this.id = id;
        this.token = token;
    }
}
