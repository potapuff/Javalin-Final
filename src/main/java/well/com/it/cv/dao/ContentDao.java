package well.com.it.cv.dao;

import java.sql.*;
import java.util.*;
import well.com.it.cv.model.Content;

public class ContentDao {
    private static final String DB_URL = "jdbc:sqlite:cv.db";

    public void initializeDb() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = """
                CREATE TABLE IF NOT EXISTS content (
                    id TEXT PRIMARY KEY,
                    content TEXT,
                    section TEXT
                )
            """;
            conn.createStatement().execute(sql);
            
            // Insert default content if empty
            if (getAllContent().isEmpty()) {
                insertDefaultContent();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Content> getAllContent() {
        List<Content> contents = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            ResultSet rs = conn.createStatement()
                .executeQuery("SELECT * FROM content ORDER BY section");
            while (rs.next()) {
                contents.add(new Content(
                    rs.getString("id"),
                    rs.getString("content"),
                    rs.getString("section")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return contents;
    }

    public void updateContent(String id, String content) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "UPDATE content SET content = '" + content + "' WHERE id = '" + id + "'";
            conn.createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertDefaultContent() {
        String[][] defaults = {
            {"about", "# About Me\nPixel art enthusiast and software developer", "About"},
            {"skills", "## Skills\n- Java\n- Web Development\n- Pixel Art", "Skills"},
            {"experience", "## Experience\n1. Software Developer\n2. Creative Designer", "Experience"}
        };

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO content (id, content, section) VALUES (?, ?, ?)"
            );
            for (String[] content : defaults) {
                stmt.setString(1, content[0]);
                stmt.setString(2, content[1]);
                stmt.setString(3, content[2]);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Content getContent(String id) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT * FROM content WHERE id = '" + id + "'";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            
            if (rs.next()) {
                return new Content(
                    rs.getString("id"),
                    rs.getString("content"),
                    rs.getString("section")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
} 