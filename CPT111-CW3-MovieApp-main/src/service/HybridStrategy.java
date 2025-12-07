package service;

import data.MovieDatabase;
import model.Movie;
import model.User;

import java.util.*;

public class HybridStrategy implements RecommendationStrategy {
    @Override
    public List<Movie> getRecommendations(User user, MovieDatabase movieDb, int n) {

        List<Movie> genreRecs = new GenreTimeBasedStrategy()
                .getRecommendations(user, movieDb, n/2 + 1);
        List<Movie> ratingRecs = new RatingBasedStrategy()
                .getRecommendations(user, movieDb, n/2 + 1);

        Set<Movie> combined = new LinkedHashSet<>();
        combined.addAll(genreRecs);
        combined.addAll(ratingRecs);

        List<Movie> result = new ArrayList<>(combined);

        if (result.size() < n) {
            result = supplementByRating(user, movieDb, n, result);
        }

        return result.subList(0, Math.min(n, result.size()));
    }


    private List<Movie> supplementByRating(User user, MovieDatabase movieDb, int targetSize, List<Movie> currentList) {
            List<Movie> finalResult = new ArrayList<>(currentList);

            if (finalResult.size() >= targetSize) {
                return finalResult;
            }

            Set<String> watched = new HashSet<>(user.getHistory());

            List<Movie> availableMovies = new ArrayList<>();
            Set<Movie> currentSet = new HashSet<>(finalResult); // 用于快速查找

            for (Movie movie : movieDb.getAllMovies()) {
                String movieId = movie.getId();

                if (!watched.contains(movieId) && !currentSet.contains(movie)) {
                    availableMovies.add(movie);
                }
            }

            if (availableMovies.isEmpty()) {
                return finalResult;
            }

            availableMovies.sort(new Comparator<Movie>() {
                @Override
                public int compare(Movie m1, Movie m2) {
                    return Double.compare(m2.getRating(), m1.getRating());
                }
            });

            int needed = targetSize - finalResult.size();
            int count = Math.min(needed, availableMovies.size());

            for (int i = 0; i < count; i++) {
                finalResult.add(availableMovies.get(i));
            }

            return finalResult;
    }
}

