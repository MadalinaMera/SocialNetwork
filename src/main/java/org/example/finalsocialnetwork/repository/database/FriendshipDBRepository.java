package org.example.finalsocialnetwork.repository.database;

import org.example.finalsocialnetwork.Constants;
import org.example.finalsocialnetwork.domain.Friendship;
import org.example.finalsocialnetwork.domain.Pair;
import org.example.finalsocialnetwork.domain.User;
import org.example.finalsocialnetwork.domain.validators.Validator;
import org.example.finalsocialnetwork.repository.FriendshipRepository;
import org.example.finalsocialnetwork.util.Page;
import org.example.finalsocialnetwork.util.Pageable;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendshipDBRepository extends AbstractDBRepository<Pair<Long, Long>, Friendship> implements FriendshipRepository<Pair<Long, Long>, Friendship> {

    public FriendshipDBRepository(Validator<Friendship> validator,DataSource dataSource) {
        super(validator, dataSource);
    }

    @Override
    public Optional<Friendship> findOne(Pair<Long,Long> id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(getSelectByIdQuery())) {
            stmt.setLong(1, id.getFirst());  // username1
            stmt.setLong(2, id.getSecond());  // username2
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return Optional.of(resultSetToEntity(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    protected Friendship resultSetToEntity(ResultSet resultSet) throws SQLException {
        Long iduser1 = resultSet.getLong("iduser1");
        Long iduser2 = resultSet.getLong("iduser2");
        LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
        return new Friendship(iduser1, iduser2, date);
    }

    @Override
    protected PreparedStatement createInsertStatement(Connection connection, Friendship friendship) throws SQLException {
        String sql = "INSERT INTO friendships (iduser1, iduser2, date) VALUES (?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setLong(1, friendship.getIduser1());
        stmt.setLong(2, friendship.getIduser2());
        stmt.setTimestamp(3, Timestamp.valueOf(friendship.getDate().format(Constants.DATE_TIME_FORMATTER)));
        return stmt;
    }

    @Override
    protected PreparedStatement createUpdateStatement(Connection connection, Friendship friendship) throws SQLException {
        String sql = "UPDATE friendships SET date = ? WHERE iduser1 = ? AND iduser2 = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, friendship.getDate().format(Constants.DATE_TIME_FORMATTER));
        stmt.setLong(2, friendship.getIduser1());
        stmt.setLong(3, friendship.getIduser2());
        return stmt;
    }

    @Override
    protected PreparedStatement createDeleteStatement(Connection connection, Pair<Long, Long> id) throws SQLException {
        String sql = "DELETE FROM friendships WHERE iduser1 = ? AND iduser2 = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setLong(1, id.getFirst());
        stmt.setLong(2, id.getSecond());
        return stmt;
    }

    @Override
    protected String getSelectAllQuery() {
        return "SELECT * FROM friendships";
    }

    @Override
    protected String getSelectByIdQuery() {
        return "SELECT * FROM friendships WHERE iduser1 = ? AND iduser2 = ?";
    }
    @Override
    public Page<User> findAllOnPage(Pageable pageable, Long id) {
        List<User> friends = new ArrayList<>();
        String query = "SELECT u.* FROM users u " +
                "INNER JOIN friendships f ON (u.id = f.iduser1 OR u.id = f.iduser2) " +
                "WHERE (f.iduser1 = ? OR f.iduser2 = ?) AND u.id <> ? " +
                "LIMIT ? OFFSET ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id);
            statement.setLong(2, id);
            statement.setLong(3, id);
            statement.setInt(4, pageable.getPageSize());
            statement.setInt(5, pageable.getPageSize() * pageable.getPageNumber());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long iduser = resultSet.getLong("id");
                String username = resultSet.getString("username");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                User user = new User(username, firstName, lastName);
                user.setId(iduser);
                friends.add(user);
            }

            // Obține numărul total de prieteni
            int totalElements = 0;
            try (PreparedStatement countStatement = connection.prepareStatement(
                    "SELECT COUNT(*) FROM friendships f " +
                            "WHERE (f.iduser1 = ? OR f.iduser2 = ?)")) {
                countStatement.setLong(1, id);
                countStatement.setLong(2, id);
                ResultSet countResultSet = countStatement.executeQuery();
                if (countResultSet.next()) {
                    totalElements = countResultSet.getInt(1);
                }
            }

            return new Page<>(friends, totalElements);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

}
