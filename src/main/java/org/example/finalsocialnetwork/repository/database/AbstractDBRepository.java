package org.example.finalsocialnetwork.repository.database;

import org.example.finalsocialnetwork.domain.Friendship;
import org.example.finalsocialnetwork.domain.validators.Validator;
import org.example.finalsocialnetwork.domain.Entity;
import org.example.finalsocialnetwork.repository.Repository;
import org.example.finalsocialnetwork.util.Page;
import org.example.finalsocialnetwork.util.Pageable;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDBRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    private final Validator<E> validator;
    protected final DataSource dataSource;

    public AbstractDBRepository(Validator<E> validator, DataSource dataSource) {
        this.validator = validator;
        this.dataSource = dataSource;
    }

    protected abstract E resultSetToEntity(ResultSet resultSet) throws SQLException;

    protected abstract PreparedStatement createInsertStatement(Connection connection, E entity) throws SQLException;

    protected abstract PreparedStatement createUpdateStatement(Connection connection, E entity) throws SQLException;

    protected abstract PreparedStatement createDeleteStatement(Connection connection, ID id) throws SQLException;

    protected abstract String getSelectAllQuery();

    protected abstract String getSelectByIdQuery();

    @Override
    public Iterable<E> findAll() {
        List<E> entities = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(getSelectAllQuery())) {
            while (resultSet.next()) {
                entities.add(resultSetToEntity(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entities;
    }

    @Override
    public Optional<E> findOne(ID id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(getSelectByIdQuery())) {
            stmt.setObject(1, id);
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
    public Optional<E> save(E entity) {
        validator.validate(entity);
        Optional<E> existingEntity = findOne(entity.getId());
        if (existingEntity.isEmpty()) {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement stmt = createInsertStatement(connection, entity)) {
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        }
        return existingEntity;
    }

    @Override
    public Optional<E> delete(ID id) {
        Optional<E> entity = findOne(id);
        if (entity.isPresent()) {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement stmt = createDeleteStatement(connection, id)) {
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return entity;
    }

    @Override
    public Optional<E> update(E entity) {
        validator.validate(entity);
        Optional<E> existingEntity = findOne(entity.getId());
        if (existingEntity.isPresent()) {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement stmt = createUpdateStatement(connection, entity)) {
                stmt.executeUpdate();
                return Optional.empty();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return Optional.of(entity);
    }
}