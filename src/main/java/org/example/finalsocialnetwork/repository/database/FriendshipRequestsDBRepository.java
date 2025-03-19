package org.example.finalsocialnetwork.repository.database;
import org.example.finalsocialnetwork.domain.FriendshipRequest;
import org.example.finalsocialnetwork.domain.User;
import org.example.finalsocialnetwork.domain.Pair;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public class FriendshipRequestsDBRepository{

    private final DataSource dataSource;
    private final UserDBRepository userDBRepository;
    public FriendshipRequestsDBRepository(DataSource dataSource, UserDBRepository userDBRepository) {
        this.dataSource = dataSource;
        this.userDBRepository = userDBRepository;
    }
    public Optional<FriendshipRequest> save(FriendshipRequest entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("INSERT INTO friendshiprequests (iduser1, iduser2, date) VALUES (?, ?, ?)")) {
            stmt.setLong(1, entity.getIduser1());
            stmt.setLong(2, entity.getIduser2());
            stmt.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.of(entity);
        }
        return Optional.empty();
    }

    public Optional<FriendshipRequest> delete(Pair<Long, Long> id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM friendshiprequests WHERE iduser1 = ? AND iduser2 = ?")) {
            stmt.setLong(1, id.getFirst());
            stmt.setLong(2, id.getSecond());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.of(new FriendshipRequest(id.getFirst(), id.getSecond(), null));
        }
        return Optional.empty();
    }

    public List<Pair<User, LocalDateTime>> getAllFriendshipRequestsForUser(Long id) {
        List<Pair<User, LocalDateTime>> friendshipRequests = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM friendship_requests WHERE \"to\" = ?")) {
            stmt.setLong(1, id);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    Long fromId = resultSet.getLong("from");
                    Long toId  = resultSet.getLong("to");
                    LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                    Optional<User> userOptional = userDBRepository.findOne(toId);
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        friendshipRequests.add(new Pair<>(user, date));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendshipRequests;
    }
}
