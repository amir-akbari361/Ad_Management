package DAL;
import Model.*;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;




public class DatabaseConnection {

    private static Properties props = new Properties();
    static {
        try {
            InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties");
            if (input == null) {
                System.err.println("config.properties file not found in resources folder");
            }
            props.load(input);
            input.close();
        } catch (IOException e) {
            System.err.println("Error loading config.properties: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static final String DB_URL = props.getProperty("db.url");
    private static final String USER = props.getProperty("db.user");
    private static final String PASSWORD = props.getProperty("db.password");

    public static Connection getConnection() throws SQLException {
        if (DB_URL == null || USER == null || PASSWORD == null) {
            throw new SQLException("Database configuration not found. Check config.properties file.");
        }
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }


    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users";
        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Error loading users" + e.getMessage());
        }
        return users;
    }

    public static List<Ad> loadAds() {
        List<Ad> ads = new ArrayList<>();
        String sql = "SELECT * FROM Ads";
        try (Connection con = DatabaseConnection.getConnection();
             Statement pstmt = con.createStatement();
             ResultSet rs = pstmt.executeQuery(sql)
        ) {
            while (rs.next()) {
                Ad ad = new Ad(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        rs.getString("owner_email"),
                        rs.getString("phone"),
                        rs.getString("image_path")
                );
                ads.add(ad);
            }

        } catch (SQLException e) {
            System.out.println("Error while loading ads " + e);
        }
        return ads;
    }

    public static List<Message> loadMessages(List<User> users) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String selectQuery = "SELECT * FROM messages";
        try (Connection con = DatabaseConnection.getConnection();
             Statement stm = con.createStatement();
             ResultSet rs = stm.executeQuery(selectQuery)
        ) {
            while (rs.next()) {
                String sender = rs.getString("sender_email");
                String recip = rs.getString("recipient_email");
                String content = rs.getString("content");
                User senderUser = null;
                User recipUser = null;
                for (User user : users) {
                    if (user.getEmail().equals(sender)) {
                        senderUser = user;
                    }
                    if (user.getEmail().equals(recip)) {
                        recipUser = user;
                    }
                }
                Date sentAt = new Date(rs.getTimestamp("sent_at").getTime());

                Message message = new Message(senderUser, recipUser, content);
                message.setTimestamp(sentAt);
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public static void loadMessagesForUser(User recipient, List<Message> messages) throws SQLException {
        String selectQuery = "SELECT sender_email, recipient_email, content, sent_at FROM messages WHERE recipient_email = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(selectQuery)) {
            pst.setString(1, recipient.getEmail());
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    User sender = (User) rs.getObject("sender_email");
                    User recip = recipient;
                    String content = rs.getString("content");
                    Date sentAt = new Date(rs.getTimestamp("sent_at").getTime());

                    Message message = new Message(sender, recip, content);
                    message.setTimestamp(sentAt);
                    messages.add(message);
                }
            }
        }
    }

    public static void saveMessage(Message message) throws SQLException {
        String insertQuery = "INSERT INTO messages (sender_email, recipient_email, content, sent_at) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(insertQuery);) {
            pst.setString(1, message.getSender().getEmail());
            pst.setString(2, message.getRecipient().getEmail());
            pst.setString(3, message.getContent());
            pst.setTimestamp(4, new java.sql.Timestamp(message.getTimestamp().getTime()));
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveUsers(User user) {
        String sql = "Insert into Users (username,email,password) values (?,?,?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.execute();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e);
        }
    }

    public static void deleteAd(int adId) {
        String sql = "DELETE FROM Ads WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, adId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting ad: " + e.getMessage());
        }
    }

    public static void saveAds(Ad ad) {
        if (ad instanceof VehicleAd) {
            String insertAdSQL = "INSERT INTO Ads (title, description, price, category, owner_email, phone, image_path) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
            String insertVehicleAdSQL = "INSERT INTO VehicleAds (ad_id, year, mileage, accident_status) VALUES (?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement adStatement = conn.prepareStatement(insertAdSQL);
                adStatement.setString(1, ad.getTitle());
                adStatement.setString(2, ad.getDescription());
                adStatement.setDouble(3, ad.getPrice());
                adStatement.setString(4, ad.getCategory());
                adStatement.setString(5, ad.getOwner());
                adStatement.setString(6, ad.getPhone());
                adStatement.setString(7, ad.getImagePath());
                ResultSet rs = adStatement.executeQuery();
                if (rs.next()) {
                    int adId = rs.getInt("id");

                    PreparedStatement vehicleStatement = conn.prepareStatement(insertVehicleAdSQL);
                    vehicleStatement.setInt(1, adId);
                    vehicleStatement.setInt(2, ((VehicleAd) ad).getYear());
                    vehicleStatement.setInt(3, ((VehicleAd) ad).getMileage());
                    vehicleStatement.setBoolean(4, ((VehicleAd) ad).isAccidented());
                    vehicleStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (ad instanceof ElectronicsAd) {
            String insertAdSQL = "INSERT INTO Ads (title, description, price, category, owner_email, phone, image_path) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
            String insertElectronicsAdSQL = "INSERT INTO ElectronicsAds (ad_id, model, color, trade) VALUES (?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement adStatement = conn.prepareStatement(insertAdSQL);
                adStatement.setString(1, ad.getTitle());
                adStatement.setString(2, ad.getDescription());
                adStatement.setDouble(3, ad.getPrice());
                adStatement.setString(4, ad.getCategory());
                adStatement.setString(5, ad.getOwner());
                adStatement.setString(6, ad.getPhone());
                adStatement.setString(7, ad.getImagePath());

                ResultSet rs = adStatement.executeQuery();
                if (rs.next()) {
                    int adId = rs.getInt("id");
                    PreparedStatement electronicsStatement = conn.prepareStatement(insertElectronicsAdSQL);
                    electronicsStatement.setInt(1, adId);
                    electronicsStatement.setString(2, ((ElectronicsAd) ad).getModel());
                    electronicsStatement.setString(3, ((ElectronicsAd) ad).getColor());
                    electronicsStatement.setBoolean(4, ((ElectronicsAd) ad).isTrade());
                    electronicsStatement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            String sql = "INSERT INTO Ads (title, description, price, category, owner_email, phone, image_path) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setString(1, ad.getTitle());
                pstmt.setString(2, ad.getDescription());
                pstmt.setDouble(3, ad.getPrice());
                pstmt.setString(4, ad.getCategory());
                pstmt.setString(5, ad.getOwner());
                pstmt.setString(6, ad.getPhone());
                pstmt.setString(7, ad.getImagePath());

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("Inserted, new id = " + id);
                } else {
                    throw new SQLException("No ID returned.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateAd(Ad ad) throws SQLException {
        String updateAdSQL = "UPDATE Ads SET title = ?, description = ?, price = ?, phone = ?, image_path = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateAdSQL)) {
            ps.setString(1, ad.getTitle());
            ps.setString(2, ad.getDescription());
            ps.setDouble(3, ad.getPrice());
            ps.setString(4, ad.getPhone());
            ps.setString(5, ad.getImagePath());
            ps.setInt(6, ad.getId());
            ps.executeUpdate();
        }
        if (ad instanceof ElectronicsAd electronicsAd) {
            String updateElectronicsSQL = "UPDATE ElectronicsAds SET model = ?, color = ?, trade = ? WHERE ad_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(updateElectronicsSQL)) {
                ps.setString(1, electronicsAd.getModel());
                ps.setString(2, electronicsAd.getColor());
                ps.setBoolean(3, electronicsAd.isTrade());
                ps.setInt(4, electronicsAd.getId());
                ps.executeUpdate();
            }
        } else if (ad instanceof VehicleAd vehicleAd) {
            String updateVehicleSQL = "UPDATE VehicleAds SET year = ?, mileage = ?, accident_status = ? WHERE ad_id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(updateVehicleSQL)) {
                ps.setInt(1, vehicleAd.getYear());
                ps.setInt(2, vehicleAd.getMileage());
                ps.setBoolean(3, vehicleAd.isAccidented());
                ps.setInt(4, vehicleAd.getId());
                ps.executeUpdate();
            }
        }
    }

    public static void addRating(String userEmail, String raterEmail, int rating) {
        String sql = "INSERT INTO UserRatings (seller_email, rater_email, rating) VALUES (?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, userEmail);
            pst.setString(2, raterEmail);
            pst.setInt(3, rating);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //----------->>>>>>>>>>>>>>محاسبه میانگین و تعداد جدید امتیازات

        String avgAndCountQuery = "Select AVG(rating) As avg_rating,COUNT(*) AS ratings_count from UserRatings where seller_email = ? ";
        double averageRating = 0.0;
        int ratingsCount = 0;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(avgAndCountQuery);) {
            pst.setString(1, userEmail);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    averageRating = rs.getDouble("avg_rating");
                    ratingsCount = rs.getInt("ratings_count");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //----------->>>>>>>>>>>>>>بروزرسانی جدول
        String updateUserQuery = "Update Users set average_rating = ?, ratings_count=? where email = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(updateUserQuery);) {
            pst.setDouble(1, averageRating);
            pst.setInt(2, ratingsCount);
            pst.setString(3, userEmail);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getUserRatingDetails(User user) throws SQLException {
        String query = "SELECT average_rating, ratings_count FROM Users WHERE email = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);) {
            pst.setString(1, user.getEmail());
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    double averageRating = rs.getDouble("average_rating");
                    int ratingsCount = rs.getInt("ratings_count");
                    user.setAverageRating(averageRating);
                    user.setRatingsCount(ratingsCount);
                }
            }
        }
    }

    public static void updateUserRatings(String sellerEmail) throws SQLException {
        String query = """
                    UPDATE Users
                    SET average_rating = (SELECT AVG(rating) FROM userratings WHERE seller_email = ?),
                        ratings_count = (SELECT COUNT(*) FROM userratings WHERE seller_email = ?)
                    WHERE email = ?;
                """;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);) {
            pst.setString(1, sellerEmail);
            pst.setString(2, sellerEmail);
            pst.setString(3, sellerEmail);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static User getUserByEmail(String email) throws SQLException {
        String query = "SELECT username, email, password, average_rating, ratings_count FROM Users WHERE email = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(query);) {
            pst.setString(1, email);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password")
                    );
                    user.setAverageRating(rs.getDouble("average_rating"));
                    user.setRatingsCount(rs.getInt("ratings_count"));
                    return user;
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}



/*

create table users
(   id serial primary key,
    username varchar(255) not null,
    email varchar(255) not null unique,
    password varchar(255) not null,
    average_rating double precision default 0.0,
    ratings_count integer default 0
);

create table messages
(
    id serial primary key,
    sender_email varchar(255) not null references users (email),
    recipient_email varchar(255) not null
    references users (email),
    content text not null,
    sent_at timestamp default CURRENT_TIMESTAMP
);

create table ads
(
    id serial primary key,
    title varchar(255) not null,
    description text,
    price double precision not null,
    category varchar(50) not null,
    owner_email varchar(255) not null references users (email) on delete cascade,
    phone varchar(15),
    image_path  varchar(255),
    created_at timestamp default CURRENT_TIMESTAMP
);

create table userratings
(
    id serial primary key,
    seller_email varchar(255) not null references users (email) on delete cascade,
    rater_email varchar(255) not null references users (email) on delete cascade,
    rating integer constraint userratings_rating_check check ((rating >= 1) AND (rating <= 5)),
    created_at timestamp default CURRENT_TIMESTAMP
);

create table electronicsads
(
    ad_id integer not null primary key references ads on delete cascade,
    model varchar(50) not null,
    color varchar(30) not null,
    trade boolean not null
);

create table vehicleads
(
    ad_id integer not null primary key references ads(id) on delete cascade,
    year integer not null constraint vehicleads_year_check check (year > 1900),
    mileage integer not null constraint vehicleads_mileage_check check (mileage >= 0),
    accident_status boolean not null
);
*/