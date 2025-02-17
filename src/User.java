import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private String username;
    private String email;
    private String password;
    private List<Integer> ratings;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.ratings = new ArrayList<>();
    }
    public String getEmail() {
        return email;
    }
    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public void rateUser(int rating) {
        ratings.add(rating);
    }
    public double getAverageRating() {
//        int[] sum= {0};
//        int[] counter= {0};
//        ratings.forEach(rating -> {
//            sum[0] += rating;
//            counter[0]++;
//        });
//        double averageRating = sum[0]>0? (double) sum[0] /counter[0]:0.0;

        if(ratings.isEmpty())
        {
            return 0.0;
        }
        return ratings.stream().mapToInt(Integer::intValue).average().orElse(0.0);


    }


    @Override
    public String toString() {
        return username + "," + email + "," + password + " (Rating: "+getAverageRating()+")";
    }
}
