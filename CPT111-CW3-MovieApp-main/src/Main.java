// Initial commit - AlvinMun

import data.MovieDatabase;
import data.UserDatabase;
import model.Movie;
import model.User;
import service.RecommendationEngine;
import service.GenreTimeBasedStrategy;
import service.RatingBasedStrategy;
import service.HybridStrategy;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        MovieDatabase movieDb = new MovieDatabase("data/movies.csv");
        UserDatabase userDb = new UserDatabase("data/users.csv");
        RecommendationEngine recEngine = new RecommendationEngine();

        User currentUser = null;
        boolean running = true;

        while (running) {
            if (currentUser == null) {
                System.out.println("\n=== Movie Recommendation & Tracker ===");
                System.out.println("1. Login");
                System.out.println("2. Register");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");

                int choice = readInt(sc);

                switch (choice) {
                    case 1:
                        currentUser = handleLogin(sc, userDb);
                        break;
                    case 2:
                        currentUser = handleRegister(sc, userDb);
                        break;
                    case 3:
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }

            } else {
                System.out.println("\n=== Main Menu (Logged in as " + currentUser.getUsername() + ") ===");
                System.out.println("1. Browse movies");
                System.out.println("2. Add movie to watchlist");
                System.out.println("3. Remove movie from watchlist");
                System.out.println("4. View watchlist");
                System.out.println("5. Mark movie as watched");
                System.out.println("6. View history");
                System.out.println("7. Get recommendations");
                System.out.println("8. Logout");
                System.out.println("9. Exit");
                System.out.println("10. Change password");
                System.out.print("Choose an option: ");

                int choice = readInt(sc);

                switch (choice) {
                    case 1:
                        browseMovies(movieDb);
                        break;
                    case 2:
                        addMovieToWatchlist(sc, currentUser, movieDb, userDb);
                        break;
                    case 3:
                        removeMovieFromWatchlist(sc, currentUser, movieDb, userDb);
                        break;
                    case 4:
                        viewWatchlist(currentUser, movieDb);
                        break;
                    case 5:
                        markMovieAsWatched(sc, currentUser, movieDb, userDb);
                        break;
                    case 6:
                        viewHistory(currentUser, movieDb);
                        break;
                    case 7:
                        getRecommendations(sc, currentUser, movieDb, recEngine);
                        break;
                    case 8:
                        currentUser = null;
                        System.out.println("You have been logged out.");
                        break;
                    case 9:
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    case 10:
                        changePassword(sc, currentUser, userDb);
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        }
        sc.close();
    }

    private static int readInt(Scanner sc) {
        while (true) {
            String line = sc.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number: ");
            }
        }
    }

    private static User handleLogin(Scanner sc, UserDatabase userDb) {
        System.out.print("Username: ");
        String username = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        User user = userDb.login(username, password);

        if (user != null) {
            System.out.println("Login successful! Welcome, " + user.getUsername());
        } else {
            System.out.println("Invalid username or password.");
        }
        return user;
    }

    private static void browseMovies(MovieDatabase movieDb) {
        System.out.println("\n===All Movies ===");
        for (Movie m : movieDb.getAllMovies()) {
            System.out.println(m);
        }
    }

    private static void addMovieToWatchlist(Scanner sc, User user, MovieDatabase movieDb, UserDatabase userDb) {
        System.out.print("Enter movie ID to add to watchlist (e.g. M001): ");
        String id = sc.nextLine().trim();

        Movie movie = movieDb.getMovieById(id);
        if (movie == null) {
            System.out.println("No movie found with ID " + id);
            return;
        }

        user.addToWatchlist(id);
        userDb.saveUsers();
        System.out.println("Added to watchlist: " + movie);
    }

    private static void removeMovieFromWatchlist(Scanner sc, User user, MovieDatabase movieDb, UserDatabase userDb) {
        System.out.print("Enter movie ID to remove from watchlist: ");
        String id = sc.nextLine().trim();

        if (!user.getWatchList().contains(id)) {
            System.out.println("That movie is not in your watchlist.");
            return;
        }

        user.removeFromWatchlist(id);
        userDb.saveUsers();
        System.out.println("Removed movie " + id + " from watchlist.");
    }

    private static void viewWatchlist(User user, MovieDatabase movieDb) {
        System.out.println("\n=== Your Watchlist ===");
        if (user.getWatchList().isEmpty()) {
            System.out.println("Your watchlist is empty.");
            return;
        }

        for (String id : user.getWatchList()) {
            Movie m = movieDb.getMovieById(id);
            if (m != null) {
                System.out.println(m);
            } else {
                System.out.println("(Unknown movie ID: " + id + ")");
            }
        }
    }

    private static void markMovieAsWatched(Scanner sc, User user, MovieDatabase movieDb, UserDatabase userDb) {
        System.out.print("Enter movie ID to mark as watched: ");
        String id = sc.nextLine().trim();

        Movie movie = movieDb.getMovieById(id);
        if (movie == null) {
            System.out.println("No movie found with ID " + id);
            return;
        }

        user.addToHistory(id);
        user.removeFromWatchlist(id);
        userDb.saveUsers();

        System.out.println("Marked as watched: " + movie);
    }

    private static void viewHistory(User user, MovieDatabase movieDb) {
        System.out.println("\n=== Your Viewing History ===");
        if (user.getHistory().isEmpty()) {
            System.out.println("You have not watched any movies yet.");
            return;
        }
        for (String id : user.getHistory()) {
            Movie m = movieDb.getMovieById(id);
            if (m != null) {
                System.out.println(m);
            } else {
                System.out.println("(Unknown movie ID: " + id + ")");
            }
        }
    }

    private static void getRecommendations(Scanner sc, User user, MovieDatabase movieDb,
                                           RecommendationEngine recEngine) {

        System.out.println("\n=== Select Recommendation Strategy ===");
        System.out.println("1. Smart (Based on your favorite genres and recent years)");
        System.out.println("2. Top Rated (Highest rated movies)");
        System.out.println("3. Hybrid (Combines both methods)");
        System.out.print("Choose strategy (1-3): ");

        int strategyChoice = readInt(sc);

        switch (strategyChoice) {
            case 1:
                recEngine.setStrategy(new GenreTimeBasedStrategy());
                System.out.println("Using Smart strategy: Based on your watching patterns");
                break;
            case 2:
                recEngine.setStrategy(new RatingBasedStrategy());
                System.out.println("Using Top Rated strategy: Most popular movies");
                break;
            case 3:
                recEngine.setStrategy(new HybridStrategy());
                System.out.println("Using Hybrid strategy: Balanced recommendations");
                break;
            default:
                System.out.println("Invalid choice. Using default Smart strategy.");
                recEngine.setStrategy(new GenreTimeBasedStrategy());
        }

        System.out.print("How many recommendations would you like (1-20): ");
        int n = readInt(sc);
        n = Math.min(Math.max(1, n), 20);

        long startTime = System.currentTimeMillis();
        List<Movie> recs = recEngine.getTopNRecommendations(user, movieDb, n);
        long endTime = System.currentTimeMillis();

        System.out.printf("\n=== Recommended Movies (Generated in %d ms) ===\n", endTime - startTime);

        if (recs == null || recs.isEmpty()) {
            System.out.println("No recommendations available. Try watching more movies first!");
            return;
        }

        for (int i = 0; i < recs.size(); i++) {
            Movie m = recs.get(i);
            System.out.printf("%d. %s (Year: %d, Rating: %.1f, Genre: %s)%n",
                    i + 1, m.getTitle(), m.getYear(), m.getRating(), m.getGenre());
        }

        if (strategyChoice == 1) {
            showRecommendationReason(user, movieDb);
        }
    }

    private static void showRecommendationReason(User user, MovieDatabase movieDb) {
        List<String> history = user.getHistory();
        if (history.size() >= 2) {
            System.out.println("\n💡 Based on your recently watched: ");
            int count = Math.min(3, history.size());
            for (int i = 0; i < count; i++) {
                Movie m = movieDb.getMovieById(history.get(i));
                if (m != null) {
                    System.out.printf("   • %s (%d, %s)%n",
                            m.getTitle(), m.getYear(), m.getGenre());
                }
            }
        }
    }

    private static void changePassword(Scanner sc, User user, UserDatabase userDb) {
        System.out.print("Enter current password: ");
        String current = sc.nextLine().trim();

        if (!user.getPassword().equals(current)) {
            System.out.println("Current password is incorrect.");
            return;
        }
        System.out.print("Enter new password: ");
        String newPass = sc.nextLine().trim();

        System.out.print("Confirm new password: ");
        String confirm = sc.nextLine().trim();

        if (!newPass.equals(confirm)) {
            System.out.println("New passwords do not match.");
            return;
        }
        if (newPass.isEmpty()) {
            System.out.println("New password cannot be empty.");
            return;
        }

        user.setPassword(newPass);
        userDb.saveUsers();
        System.out.println("Password changed successfully.");
    }
    private static User handleRegister(Scanner sc, UserDatabase userDb) {
        System.out.println("\n=== Register New User ===");

        System.out.print("Choose a username: ");
        String username = sc.nextLine().trim();

        System.out.print("Choose a password: ");
        String password = sc.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username or password cannot be empty!");
            return null;
        }

        User newUser = userDb.register(username, password);

        if (newUser == null) {
            System.out.println("Username already exists. Please choose another.");
            return null;
        }

        System.out.println("Registration successful! You can now log in.");
        return newUser;
    }
}
