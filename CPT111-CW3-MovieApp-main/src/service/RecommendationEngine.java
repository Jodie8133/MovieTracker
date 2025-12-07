package service;

import model.Movie;
import data.MovieDatabase;
import model.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecommendationEngine {
    private RecommendationStrategy strategy;

    public RecommendationEngine() {
        this.strategy = new RatingBasedStrategy();
    }

    public void setStrategy(RecommendationStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Movie> getTopNRecommendations(User user, MovieDatabase movieDb, int n) {
        if (strategy != null) {
            return strategy.getRecommendations(user, movieDb, n);
        } else {
            return new RatingBasedStrategy().getRecommendations(user, movieDb, n);
        }
    }
}