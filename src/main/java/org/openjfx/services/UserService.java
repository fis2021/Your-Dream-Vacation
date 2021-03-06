package org.openjfx.services;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectRepository;
import org.openjfx.exceptions.PasswordIncorrectException;
import org.openjfx.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

import static org.openjfx.services.FileSystemService.getPathToFile;

public class UserService {
    private static ObjectRepository<User> userRepository;
    private static Nitrite database;

    public static ObjectRepository<User> getUserRepository() {
        return userRepository;
    }

    public static void initDatabase() {
        FileSystemService.initDirectory();
        database = Nitrite.builder()
                .filePath(getPathToFile("registration-database.db").toFile())
                .openOrCreate("test", "test");

        userRepository = database.getRepository(User.class);
    }

    public static void addUser1(String username, String password, String role, String name, String eMail, String phoneNumber) {
        userRepository.insert(new User(username, encodePassword(username, password), role, name, eMail, phoneNumber));
    }

    public static void addUser2(String username, String password, String role, String name, String eMail, String phoneNumber, String nameOfAgency) {
        userRepository.insert(new User(username, encodePassword(username, password), role, name, eMail, phoneNumber, nameOfAgency));
    }

    public static boolean checkUserDoesAlreadyExist(String username, String password) throws PasswordIncorrectException{
        for (User user : userRepository.find()) {
            if (Objects.equals(username, user.getUsername())) {
                if(!Objects.equals(user.getPassword(), encodePassword(username,password)))
                    throw new PasswordIncorrectException(password);
                else return true;
            }
        }
        return false;
    }

    public static String encodePassword(String salt, String password) {
        MessageDigest md = getMessageDigest();
        md.update(salt.getBytes(StandardCharsets.UTF_8));

        byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));

        // This is the way a password should be encoded when checking the credentials
        return new String(hashedPassword, StandardCharsets.UTF_8)
                .replace("\"", ""); //to be able to save in JSON format
    }

    private static MessageDigest getMessageDigest() {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-512 does not exist!");
        }
        return md;
    }

    public static Nitrite getDatabase() {
        return database;
    }

    public static List<User> getAllUsers() {
        return userRepository.find().toList();
    }

}