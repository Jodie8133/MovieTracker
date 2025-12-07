package model;

import java.util.ArrayList;

public class User {
    private String username;
    private String password;
    private ArrayList<String> watchlist;
    private ArrayList<String> history;

    public User(String username, String password, ArrayList<String> watchlist, ArrayList<String> history) {
        this.username = username;
        this.password = password;
        this.watchlist = watchlist;
        this.history = history;                
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<String> getWatchList() {
        return watchlist;
    }

    public ArrayList<String> getHistory() {
        return history;
    }

    public void addToWatchlist(String movieId) {
        if (!watchlist.contains(movieId)) {
            watchlist.add(movieId);
        }
    }

    public void removeFromWatchlist(String movieId) {
        watchlist.remove(movieId);
    }

    public void addToHistory(String movieId) {
        if (!history.contains(movieId)) {
            history.add(movieId);
        }
    }

    public String serializeWatchlist() {
        return String.join(";", watchlist);
    }

    public String serializeHistory() {
        return String.join(";", history);
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
}

