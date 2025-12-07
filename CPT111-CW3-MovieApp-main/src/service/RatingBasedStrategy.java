package service;

import model.Movie;
import data.MovieDatabase;
import model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RatingBasedStrategy implements RecommendationStrategy {

    @Override
    public List<Movie> getRecommendations(User user, MovieDatabase movieDb, int n) {
        List<Movie> recommendations = new ArrayList<>();
        Set<String> watched = new HashSet<>(user.getHistory());

        for (Movie movie : movieDb.getAllMovies()) {
            if (!watched.contains(movie.getId())) {
                recommendations.add(movie);
            }
        }

        recommendations.sort((m1, m2) -> Double.compare(m2.getRating(), m1.getRating()));

        return recommendations.subList(0, Math.min(n, recommendations.size()));
    }
}
