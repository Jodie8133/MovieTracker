package service;

import data.MovieDatabase;
import model.Movie;
import model.User;

import java.util.*;


public class GenreTimeBasedStrategy implements RecommendationStrategy {

    @Override
    public List<Movie> getRecommendations(User user, MovieDatabase movieDb, int n) {
        List<Movie> recommendations = new ArrayList<>();
        Set<String> watchedIds = new HashSet<>(user.getHistory());

        List<Movie> watchedMovies = new ArrayList<>();
        for (String movieId : user.getHistory()) {
            Movie m = movieDb.getMovieById(movieId);
            if (m != null) watchedMovies.add(m);
        }

        Map<String, Double> genreWeights = calculateGenreWeights(watchedMovies);

        int preferredYearRange = calculatePreferredYearRange(watchedMovies);

        Map<Movie, Double> movieScores = new HashMap<>();
        for (Movie movie : movieDb.getAllMovies()) {
            if (watchedIds.contains(movie.getId())) continue;

            double score = calculateMovieScore(movie, genreWeights, preferredYearRange);
            movieScores.put(movie, score);
        }

        List<Map.Entry<Movie, Double>> sorted = new ArrayList<>(movieScores.entrySet());
        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        for (int i = 0; i < Math.min(n, sorted.size()); i++) {
            recommendations.add(sorted.get(i).getKey());
        }

        return recommendations;
    }

    private Map<String, Double> calculateGenreWeights(List<Movie> watchedMovies) {
        Map<String, Double> weights = new HashMap<>();
        int totalMovies = watchedMovies.size();

        for (int i = 0; i < watchedMovies.size(); i++) {
            Movie movie = watchedMovies.get(i);
            String genre = movie.getGenre();
            double recencyWeight = 1.0 + (totalMovies - i) * 0.15;

            weights.put(genre, weights.getOrDefault(genre, 0.0) + recencyWeight);
        }

        return weights;
    }

    private int calculatePreferredYearRange(List<Movie> watchedMovies) {
        if (watchedMovies.isEmpty()) return -1;

        int recentCount = Math.min(5, watchedMovies.size());
        int sum = 0;
        double totalWeight = 0;

        for (int i = 0; i < recentCount; i++) {
            Movie movie = watchedMovies.get(i);
            double weight = 1.0 + (recentCount - i) * 0.25;
            sum += movie.getYear() * weight;
            totalWeight += weight;
        }

        return (int)(sum / totalWeight);
    }

    private double calculateMovieScore(Movie movie, Map<String, Double> genreWeights, int preferredYear) {
        String movieGenre = movie.getGenre();

        double genreScore = 0;
        if (genreWeights.containsKey(movieGenre)) {
            double baseGenreWeight = genreWeights.get(movieGenre);
            double maxWeight = Collections.max(genreWeights.values());
            genreScore = 50 * (baseGenreWeight / maxWeight);
        } else {
            genreScore = 0;
        }

        double yearScore = 0;
        if (preferredYear != -1) {
            int yearDiff = Math.abs(movie.getYear() - preferredYear);
            if (yearDiff <= 20) {
                yearScore = 15 * (1.0 - (yearDiff / 20.0));
            }
        }

        double ratingScore = 35 * (movie.getRating() / 10.0);

        double totalScore = genreScore + yearScore + ratingScore;

        if (genreScore == 0) {
            totalScore = ratingScore * 0.5;
        }

        return totalScore;
    }
}
