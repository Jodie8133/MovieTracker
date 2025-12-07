package data;

import model.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserDatabase {
    private HashMap<String, User> users = new HashMap<>();
    private String filePath;

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b)); // 转成 16 进制字符串
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm SHA-256 not found");
        }
    }
//判断密码是否为哈希加密过的
    private boolean isHashedPassword(String password) {
        return password != null && password.matches("[0-9a-f]{64}");
    }

    public UserDatabase(String filePath) {
        this.filePath = filePath;
        loadUsersFromFile(filePath);
    }

    private void loadUsersFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", -1);

                if (parts.length < 4) continue;
                
                String username = parts[0].trim();
                String password = parts[1].trim();
                String watchlistStr = parts[2].trim();
                String historyStr = parts[3].trim();

                ArrayList<String> watchlist = parseList(watchlistStr);
                ArrayList<String> history = parseHistory(historyStr);

                User user = new User(username, password, watchlist, history);
                users.put(username, user);
            }
        } catch (IOException e) {
            System.out.println("Error loading users.csv " + e.getMessage());
        }
    }

    private ArrayList<String> parseList(String s) {
        ArrayList<String> list = new ArrayList<>();

        if (s == null || s.isEmpty()) return list;

        String[] arr = s.split(";");
        for (String id : arr) {
            id = id.trim();
            if (!id.isEmpty()) {
                list.add(id);
            }
        }
        return list;
    }

    private ArrayList<String> parseHistory(String s) {
        ArrayList<String> list = new ArrayList<>();

        if (s == null || s.isEmpty()) return list;

        String[] arr = s.split(";");
        for (String entry : arr) {
            entry = entry.trim();
            if (entry.isEmpty()) continue;

            String[] parts = entry.split("@");
            String movieId = parts[0].trim();

            if (!movieId.isEmpty()) {
                list.add(movieId);
            }
        }
        return list;
    }
//如果没有加密过，在登录后再给他加密
    public User login(String username, String password) {
        User u = users.get(username);
        if (u == null) return null;

        String stored = u.getPassword();

        if (isHashedPassword(stored)) {
            String hashedInput = hashPassword(password);
            if (stored.equals(hashedInput)) {
                return u;
            } else {
                return null;
            }
        }

        if (stored.equals(password)) {
            String newHashed = hashPassword(password);
            u.setPassword(newHashed);
            saveUsers();
            return u;
        }

        return null;
    }

    public void saveUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("username, password, watchlist, history");
            bw.newLine();

            for (User u : users.values()) {
                String watchlistStr = u.serializeWatchlist();
                String historyStr = u.serializeHistory();

                String line = u.getUsername() + "," + u.getPassword() + "," + watchlistStr + "," + historyStr;

                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving users.csv: " + e.getMessage());
        }
    }

    // (Advanced feature idea: createUser(), changePassword(), etc.)
    public User register(String username, String password) {
        if (users.containsKey(username)) {
            return null; // 用户已存在
        }

        String hashed = hashPassword(password);

        User newUser = new User(username, hashed, new ArrayList<>(), new ArrayList<>());
        users.put(username, newUser);
        saveUsers();
        return newUser;
    }
    public String hashForExternalUse(String password) {
        return hashPassword(password);
    }


    public HashMap<String, User> getUsers() {
        return users;
    }
}
