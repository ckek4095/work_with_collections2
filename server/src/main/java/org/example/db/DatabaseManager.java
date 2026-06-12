package org.example.db;

import java.sql.*;
import java.util.*;

import org.example.PasswordHasher;
import org.example.User;
import org.example.models.LabWork;
import org.example.models.Coordinates;
import org.example.models.Difficulty;
import org.example.models.Discipline;

public class DatabaseManager {
    private final String url;
    private final String user;
    private final String password;
    private Connection connection;

    public DatabaseManager(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
        System.out.println("Подключено к PostgreSQL");
    }

    public void disconnect() {
        try { if (connection != null) connection.close(); }
        catch (SQLException e) { e.printStackTrace(); }
    }

    public Set<LabWork> loadCollection() throws SQLException {
        Set<LabWork> collection = new HashSet<>();
        String sql = "SELECT * FROM lab_works";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                collection.add(mapResultSetToLabWork(rs));
            }
        }
        return collection;
    }

    public boolean registerUser(String login, String password) {
        String hash = PasswordHasher.hash(password);
        String sql = "INSERT INTO users (login, password_hash) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, login);
            stmt.setString(2, hash);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Ошибка регистрации: " + e.getMessage());
            return false;
        }
    }

    public User authenticate(String login, String password) {
        String sql = "SELECT id, login, password_hash FROM users WHERE login = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hash = rs.getString("password_hash");
                if (PasswordHasher.verify(password, hash)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setLogin(login);
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long saveLabWorkAndGetId(LabWork lab, int userId) throws SQLException {
        long newId = getNextId();
        lab.setId(newId);

        String sql = """
            INSERT INTO lab_works (id, name, coord_x, coord_y, creation_date,
                minimal_point, difficulty, discipline_name, discipline_labs_count, user_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, lab.getId());
            stmt.setString(2, lab.getName());
            stmt.setInt(3, lab.getCoordinates().getX());
            stmt.setInt(4, lab.getCoordinates().getY());
            stmt.setTimestamp(5, Timestamp.valueOf(lab.getCreationDate()));
            stmt.setFloat(6, lab.getMinimalPoint());
            stmt.setString(7, lab.getDifficulty() != null ? lab.getDifficulty().name() : null);
            stmt.setString(8, lab.getDiscipline().getName());
            stmt.setInt(9, lab.getDiscipline().getLabsCount());
            stmt.setInt(10, userId);

            int affected = stmt.executeUpdate();
            return affected > 0 ? newId : null;
        }
    }

    // Старый метод для обратной совместимости
    public boolean saveLabWork(LabWork lab, int userId) throws SQLException {
        return saveLabWorkAndGetId(lab, userId) != null;
    }

    public boolean updateLabWork(LabWork lab, int userId) throws SQLException {
        if (!isOwner(lab.getId(), userId)) {
            System.err.println("Пользователь " + userId + " не владеет объектом " + lab.getId());
            return false;
        }

        String sql = """
            UPDATE lab_works SET name=?, coord_x=?, coord_y=?, minimal_point=?,
                difficulty=?, discipline_name=?, discipline_labs_count=?
            WHERE id=? AND user_id=?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, lab.getName());
            stmt.setInt(2, lab.getCoordinates().getX());
            stmt.setInt(3, lab.getCoordinates().getY());
            stmt.setFloat(4, lab.getMinimalPoint());
            stmt.setString(5, lab.getDifficulty() != null ? lab.getDifficulty().name() : null);
            stmt.setString(6, lab.getDiscipline().getName());
            stmt.setInt(7, lab.getDiscipline().getLabsCount());
            stmt.setLong(8, lab.getId());
            stmt.setInt(9, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteLabWork(long id, int userId) throws SQLException {
        if (!isOwner(id, userId)) return false;

        String sql = "DELETE FROM lab_works WHERE id=? AND user_id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean isOwner(long labId, int userId) throws SQLException {
        String sql = "SELECT user_id FROM lab_works WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, labId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int ownerId = rs.getInt("user_id");
                return ownerId == userId;
            }
            return false;
        }
    }

    private long getNextId() throws SQLException {
        String sql = "SELECT nextval('lab_works_id_seq')";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();
            return rs.getLong(1);
        }
    }

    private LabWork mapResultSetToLabWork(ResultSet rs) throws SQLException {
        LabWork lab = new LabWork();
        lab.setId(rs.getLong("id"));
        lab.setName(rs.getString("name"));
        lab.setCoordinates(new Coordinates(rs.getInt("coord_x"), rs.getInt("coord_y")));
        lab.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
        lab.setMinimalPoint(rs.getFloat("minimal_point"));

        String diff = rs.getString("difficulty");
        if (diff != null) {
            lab.setDifficulty(Difficulty.valueOf(diff));
        }

        lab.setDiscipline(new Discipline(
                rs.getString("discipline_name"),
                rs.getInt("discipline_labs_count")
        ));

        int ownerId = rs.getInt("user_id");
        lab.setOwnerId(ownerId);

        return lab;
    }

    public int deleteAllUserLabWorks(Integer userId) throws SQLException {
        String sql = "DELETE FROM lab_works WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate(); // вернет количество удаленных строк
        }
    }
}



/*
-- ownerId = 1 (3 работы)
INSERT INTO lab_works (id, name, coord_x, coord_y, creation_date, minimal_point, difficulty, discipline_name, discipline_labs_count, user_id)
VALUES
    (nextval('lab_works_id_seq'), 'Анализ алгоритмов сортировки', 10, 25, NOW(), 15.5, 'EASY', 'Алгоритмы и структуры данных', 3, 1),
    (nextval('lab_works_id_seq'), 'Нейронные сети и глубокое обучение', -50, 100, NOW(), 80.0, 'INSANE', 'Машинное обучение', 5, 1),
    (nextval('lab_works_id_seq'), 'Объектно-ориентированное программирование', 5, 50, NOW(), 60.0, 'HARD', 'Java-разработка', 3, 1);

-- ownerId = 2 (2 работы)
INSERT INTO lab_works (id, name, coord_x, coord_y, creation_date, minimal_point, difficulty, discipline_name, discipline_labs_count, user_id)
VALUES
    (nextval('lab_works_id_seq'), 'Базы данных и SQL', 30, -15, NOW(), 45.0, 'NORMAL', 'Базы данных', 2, 2),
    (nextval('lab_works_id_seq'), 'Распределённые вычисления', 75, -200, NOW(), 70.0, 'VERY_HARD', 'Параллельное программирование', 4, 2);
 */