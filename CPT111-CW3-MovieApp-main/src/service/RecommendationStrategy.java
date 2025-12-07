package service;

import model.Movie;
import data.MovieDatabase;
import model.User;

import java.util.*;

public interface RecommendationStrategy {
    List<Movie> getRecommendations(User user, MovieDatabase movieDb, int n);
}

