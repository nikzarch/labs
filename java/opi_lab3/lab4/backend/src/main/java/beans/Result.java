package beans;


import jakarta.persistence.*;

@Entity
@Table(name = "results")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double x;
    private double y;
    private double r;
    private boolean hit;

    @Column(name = "time_now")
    private String timeNow;

    @Column(name = "execution_time")
    private long executionTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)

    private User user;

    public Result() {}

    public Result(double x, double y, double r, boolean hit, String timeNow, long executionTime) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.hit = hit;
        this.timeNow = timeNow;
        this.executionTime = executionTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public String getTimeNow() {
        return timeNow;
    }

    public void setTimeNow(String timeNow) {
        this.timeNow = timeNow;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
