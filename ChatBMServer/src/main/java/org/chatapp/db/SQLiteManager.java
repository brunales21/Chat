package org.chatapp.db;

import org.chatapp.exceptions.InvalidCredentialsException;
import org.chatapp.exceptions.NicknameInUseException;
import org.chatapp.exceptions.SessionAlreadyOpenException;
import org.chatapp.utils.DatabaseUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;

public class SQLiteManager {

    // Nombre de la base de datos
    public static final String URL = "jdbc:sqlite:chat.db";
    private Connection connection;

    public SQLiteManager() {
        connect();
        createTable(DatabaseUtils.USER_TABLE_NAME);
    }

    // Método para conectar a la base de datos SQLite
    private void connect() {
        try {
            connection = DriverManager.getConnection(URL);
            System.out.println("Conexión a SQLite establecida.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Método para crear la tabla "usuario" si no existe
    private void createTable(String tableName) {
        String sql = DatabaseUtils.getTableCreationQuery(tableName);
        try {
            connection.createStatement().execute(sql);
            System.out.println("Tabla " + tableName + " creada exitosamente.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Método para verificar credenciales
    public boolean login(String nickname, String password, boolean sessionOpened) throws InvalidCredentialsException, SessionAlreadyOpenException {
        String sql = "SELECT contraseña FROM usuario WHERE nickname = ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, nickname);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("contraseña");
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
    public boolean registerUser(String nickname, String contraseña) throws NicknameInUseException {
        String sql = "INSERT INTO usuario(nickname, contraseña) VALUES(?, ?)";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, nickname);
            String hashedPassword = hashPassword(contraseña);
            pstmt.setString(2, hashedPassword);
            pstmt.executeUpdate();
            System.out.println("Usuario registrado correctamente.");
            return true;
        } catch (SQLException | NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
            if (e instanceof SQLException sqlException) {
                if (sqlException.getSQLState().equals("23000") && sqlException.getErrorCode() == 19) {
                    throw new NicknameInUseException(nickname);
                } else {
                    System.out.println("Error al registrar usuario: " + e.getMessage());
                    return false;
                }
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
