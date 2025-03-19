package org.example.finalsocialnetwork.repository.database;

import org.example.finalsocialnetwork.domain.User;
import org.example.finalsocialnetwork.domain.validators.Validator;
import org.example.finalsocialnetwork.util.Page;
import org.example.finalsocialnetwork.util.Pageable;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

public class UserDBRepository extends AbstractDBRepository<Long, User> {

    public UserDBRepository(Validator<User> validator,  DataSource dataSource) {
        super(validator,dataSource);
    }

    @Override
    public Optional<User> findOne(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(getSelectByIdQuery())) {
            stmt.setLong(1, id); // Folosim Long, conform semnăturii corecte
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(resultSetToEntity(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    @Override
    protected User resultSetToEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String username = resultSet.getString("username");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        User u = new User(firstName, lastName, username);
        u.setId(id);
        return u;
    }

    @Override
    protected PreparedStatement createInsertStatement(Connection connection, User user) throws SQLException {
        String sql = "INSERT INTO users (username, first_name, last_name) VALUES (?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getFirstName());
        stmt.setString(3, user.getLastName());
        return stmt;
    }

    @Override
    protected PreparedStatement createUpdateStatement(Connection connection, User user) throws SQLException {
        String sql = "UPDATE users SET first_name = ?, last_name = ? WHERE username = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, user.getFirstName());
        stmt.setString(2, user.getLastName());
        stmt.setString(3, user.getUsername());
        return stmt;
    }

    @Override
    protected PreparedStatement createDeleteStatement(Connection connection, Long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?"; // Presupunem că tabela are o coloană `id` pentru Long
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setLong(1, id);
        return stmt;
    }

    @Override
    protected String getSelectAllQuery() {
        return "SELECT * FROM users";
    }

    @Override
    protected String getSelectByIdQuery() {
        return "SELECT * FROM users WHERE id = ?";
    }
    @Override
    public Page<User> findAllOnPage(Pageable pageable, Long id) {
        return null;
    }

}