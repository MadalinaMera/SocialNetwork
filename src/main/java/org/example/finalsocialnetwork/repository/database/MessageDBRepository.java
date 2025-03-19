package org.example.finalsocialnetwork.repository.database;

import org.example.finalsocialnetwork.Constants;
import org.example.finalsocialnetwork.domain.Message;
import org.example.finalsocialnetwork.domain.User;
import org.example.finalsocialnetwork.domain.validators.Validator;
import org.example.finalsocialnetwork.util.Page;
import org.example.finalsocialnetwork.util.Pageable;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDBRepository extends AbstractDBRepository<Long, Message> {

    public MessageDBRepository(Validator<Message> validator, DataSource dataSource) {
        super(validator, dataSource);
    }

    @Override
    protected Message resultSetToEntity(ResultSet resultSet) throws SQLException {
        Long id_message = resultSet.getLong("id_message");
        String text= resultSet.getString("text");
        LocalDateTime timestamp = resultSet.getTimestamp("timestamp").toLocalDateTime();
        Long senderId = resultSet.getLong("senderid");
        Long receiverId = resultSet.getLong("receiverid");
        Message m = new Message(text, timestamp, senderId, receiverId);
        m.setId(id_message);
        return m;
    }

    @Override
    protected PreparedStatement createInsertStatement(Connection connection, Message message) throws SQLException {
        String sql = "INSERT INTO messages (text, timestamp, senderid, receiverid) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, message.getText());
        stmt.setTimestamp(2, Timestamp.valueOf(message.getTimestamp().format(Constants.DATE_TIME_FORMATTER)));
        stmt.setLong(3, message.getSenderId());
        stmt.setLong(4, message.getReceiverId());
        return stmt;
    }

    @Override
    protected PreparedStatement createUpdateStatement(Connection connection, Message message) throws SQLException{
        String sql = "UPDATE messages SET text = ?, timestamp = ?, senderid = ?, receiverid = ? WHERE id_message = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, message.getText());
        stmt.setTimestamp(2, Timestamp.valueOf(message.getTimestamp().format(Constants.DATE_TIME_FORMATTER)));
        stmt.setLong(3, message.getSenderId());
        stmt.setLong(4, message.getReceiverId());
        stmt.setLong(5, message.getId());
        return stmt;
    }

    @Override
    protected PreparedStatement createDeleteStatement(Connection connection, Long Id) throws SQLException{
        String sql = "DELETE FROM messages WHERE id_message = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setLong(1, Id);
        return stmt;
    }

    protected String getSelectAllQuery(){
        return "SELECT * FROM messages";
    }

    protected String getSelectByIdQuery(){
        return "SELECT * FROM messages WHERE id_message = ?";
    }

    public Iterable<Message> getMessagesBetweenUsers(Long id1, Long id2) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE (senderid = ? AND receiverid = ?) OR (senderid = ? AND receiverid = ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id1);
            stmt.setLong(2, id2);
            stmt.setLong(3, id2);
            stmt.setLong(4, id1);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                Message message = new Message(
                        resultSet.getString("text"),
                        resultSet.getTimestamp("timestamp").toLocalDateTime(),
                        resultSet.getLong("senderid"),
                        resultSet.getLong("receiverid")
                );
                message.setId(resultSet.getLong("id_message"));
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public Page<User> findAllOnPage(Pageable pageable, Long idmessage) {
        return null;
    }
}
