package org.example;

import org.example.Entities.Message;
import org.example.Entities.Profile;
import org.example.Entities.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DAO {
    private static Connection conn;

    public DAO() {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://surus.db.elephantsql.com/fpyrqbbv", "fpyrqbbv", "yybz3iPPXJqAmOFME7Co4DJ-a-yeyAPx");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Profile getUnseenProfile(String sessionId) throws SQLException {
        ResultSet rs = conn.prepareStatement("select profile_id, profile_name, age, image, description from profiles\n" +
                "    except\n" +
                "select profiles.profile_id, profile_name, age, image, description from profiles join likes l on profiles.profile_id = l.profile_id where user_id = (select user_id from users where session_id='" + sessionId + "');").executeQuery();
        while(rs.next()) {
            return new Profile(rs.getInt("profile_id"), rs.getString("profile_name"), rs.getInt("age"), rs.getString("image"), rs.getString("description"));
        }
        return null;
    }

    public void likeProfile(String sessionId, String profileId, String decision) throws SQLException {
        conn.prepareStatement("insert into likes values ((select user_id from users where session_id = '" + sessionId + "'), " + profileId + ", "  + decision + ");").execute();
    }

    public List<Profile> getLikedProfiles(String sessionId) throws SQLException {
        List<Profile> profiles = new ArrayList<>();
        ResultSet rs = conn.prepareStatement("select * from likes join profiles p on p.profile_id = likes.profile_id where user_id=(select user_id from users where session_id='" + sessionId + "') and liked=true;").executeQuery();
        while (rs.next()) {
            profiles.add(new Profile(rs.getInt("profile_id"), rs.getString("profile_name"), rs.getInt("age"), rs.getString("image"), rs.getString("description")));
        }
        return profiles;
    }

    public List<Message> getMessages(String sessionId, String profileId) throws SQLException {
        ResultSet rs = conn.prepareStatement("select user_id, profile_id, message, to_char(time, 'Mon DD, HH24:MI') formattedTime, is_user_sender from messages " +
                "where user_id = (select user_id from users where session_id='" + sessionId + "') and profile_id = " + profileId + " order by formattedTime;").executeQuery();
        List<Message> toReturn = new ArrayList<>();
        while (rs.next()) {
            toReturn.add(new Message(rs.getInt("profile_id"), rs.getString("message"), rs.getString("formattedTime"), rs.getBoolean("is_user_sender")));
        }
        return toReturn;
    }

    public void addMessage(Message message) throws SQLException {
        conn.prepareStatement("insert into messages values ('" + message.getMessage() + "', current_timestamp, (select user_id from users where session_id='" + message.getSessionId() + "'), " + message.getProfileId() +", true);").execute();
             conn.prepareStatement("insert into messages values ('ясно', current_timestamp, (select user_id from users where session_id='" + message.getSessionId() + "'), " + message.getProfileId() +", false);").execute();

    }

    public Profile getProfileById(int id) throws SQLException {
        ResultSet rs = conn.prepareStatement("select * from profiles where profile_id=" + id).executeQuery();
        Profile toReturn = null;
        while (rs.next()) {
            toReturn = new Profile(rs.getInt("profile_id"), rs.getString("profile_name"), rs.getInt("age"), rs.getString("image"), rs.getString("description"));
        }
        return toReturn;
    }

    public String login(String email, String password) throws SQLException {
        ResultSet rs = conn.prepareStatement("select * from users where email='" + email + "' and password='" + password + "';").executeQuery();
        String sessionId = null;
        while (rs.next()) {
            if (rs.getString("email") == null) return null;
            sessionId = rs.getString("session_id");
        }
        return sessionId;
    }

    public boolean doesUserWithSuchSessionIdExist(String sessionId) throws SQLException {
        ResultSet rs = conn.prepareStatement("select * from users where session_id = '" + sessionId + "'").executeQuery();
        while (rs.next()) {
            if (rs.getString("user_id") != null) {
                return true;
            }
        }
        return false;
    }

    public String getUsersEmail(String sessionId) throws SQLException {
        ResultSet rs = conn.prepareStatement("select email from users where session_id = '" + sessionId + "'").executeQuery();
        String toReturn = "";
        while (rs.next()) {
            toReturn = rs.getString("email");
        }
        return toReturn;
    }
// UUID.randomUUID().toString()
    public void signUp(String email, String password) throws SQLException {
        String id = UUID.randomUUID().toString();
        conn.prepareStatement("insert into users (email, password, session_id) VALUES ('" + email + "', '" + password  + "', '" + id + "')").execute();
        conn.prepareStatement("insert into messages (message, time, user_id, profile_id, is_user_sender) VALUES ('привет', now(), (select user_id from users where session_id='" + id + "'), 113, false);").execute();
        conn.prepareStatement("insert into messages (message, time, user_id, profile_id, is_user_sender) VALUES ('привет', now(), (select user_id from users where session_id='" + id + "'), 21, false);").execute();
        conn.prepareStatement("insert into messages (message, time, user_id, profile_id, is_user_sender) VALUES ('привет', now(), (select user_id from users where session_id='" + id + "'), 862, false);").execute();
        conn.prepareStatement("insert into messages (message, time, user_id, profile_id, is_user_sender) VALUES ('привет', now(), (select user_id from users where session_id='" + id + "'), 522, false);").execute();
        conn.prepareStatement("insert into messages (message, time, user_id, profile_id, is_user_sender) VALUES ('привет', now(), (select user_id from users where session_id='" + id + "'), 722, false);").execute();
    }

    public boolean doesUserWithSuchEmailAlreadyExist(String email) throws SQLException {
        ResultSet rs = conn.prepareStatement("select * from fpyrqbbv.public.users where email='" + email +"'").executeQuery();
        while (rs.next()) {
            if (rs.getString(1)!=null) return true;
        }
        return false;
    }
}
