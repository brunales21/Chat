package org.chatapp.db;

import org.chatapp.exceptions.InvalidCredentialsException;
import org.chatapp.exceptions.NicknameInUseException;
import org.chatapp.exceptions.SessionAlreadyOpenException;
import org.chatapp.utils.DatabaseUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;

public class SQLiteManager {

    // Ruta de la base de datos
    private static final String DB_FOLDER = "src/main/resources/data";
    private static final String DB_FILE_NAME = "chat.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FOLDER + "/" + DB_FILE_NAME;
    private Connection connection;

    public SQLiteManager() {
        ensureDatabaseDirectoryExists();
        connect();
        createTable(DatabaseUtils.USER_TABLE_NAME);
    }

    // Método para asegurar que el directorio de la base de datos exista
    private void ensureDatabaseDirectoryExists() {
        Path dbPath = Paths.get(DB_FOLDER);
        try {
            if (!Files.exists(dbPath)) {
                Files.createDirectories(dbPath);
                System.out.println("Directorio de la base de datos creado: " + dbPath);
            }
        } catch (IOException e) {
            System.err.println("Error al crear el directorio de la base de datos: " + e.getMessage());
        }
    }

    // Método para conectar a la base de datos SQLite
    private void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Conexión a SQLite establecida en: " + DB_URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Método para crear la tabla "usuario" si no existe
    private void createTable(String name) {
        String sql = DatabaseUtils.createUserTable(name);
        try {
            connection.createStatement().execute(sql);
            System.out.println("Tabla " + name + " creada exitosamente.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Método para verificar credenciales
    public boolean login(String nickname, String password, boolean sessionOpened) throws InvalidCredentialsException, SessionAlreadyOpenException {
        String sql = "SELECT password FROM user WHERE nickname = ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, nickname);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (!verifyPassword(password, storedPassword)) {
                    throw new InvalidCredentialsException();
                } else {
                    System.out.println("Inicio de sesión exitoso.");
                    if (sessionOpened) {
                        throw new SessionAlreadyOpenException(nickname);
                    }
                    return true;
                }
            } else {
                throw new InvalidCredentialsException(); // No se encontró el usuario
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            System.out.println("Error al iniciar sesión: " + e.getMessage());
        }
        return false;
    }

    // Método para registrar un nuevo usuario
    public boolean registerUser(String nickname, String password) throws NicknameInUseException {
        String sql = "INSERT INTO "+DatabaseUtils.USER_TABLE_NAME+"(nickname, password) VALUES(?, ?)";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, nickname);
            String hashedPassword = hashPassword(password);
            pstmt.setString(2, hashedPassword);
            pstmt.executeUpdate();
            System.out.println("Usuario registrado correctamente.");
            return true;
        } catch (SQLException | NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
            if (e instanceof SQLException) {
                throw new NicknameInUseException(nickname);
            } else {
                System.out.println("Error al registrar usuario: " + e.getMessage());
                return false;
            }
        }
    }

    // Método para cerrar la conexión
    public void closeConnection() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Método para hashear la contraseña
    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

    // Método para verificar la contraseña
    private boolean verifyPassword(String inputPassword, String storedPassword) throws NoSuchAlgorithmException {
        String hashedInputPassword = hashPassword(inputPassword);
        return hashedInputPassword.equals(storedPassword);
    }

    // Método para vaciar una tabla en una base de datos SQLite
    public void truncateTable(String tableName) throws SQLException {
        String sql = "DELETE FROM " + tableName;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.executeUpdate();
            System.out.println("Tabla " + tableName + " truncada exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error truncando tabla " + tableName + ": " + e.getMessage());
            throw e; // Relanzamos la excepción para que pueda ser manejada por el código que llama a esta función
        }
    }
}
