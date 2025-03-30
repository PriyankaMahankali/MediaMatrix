package simplemenu;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class MediaMat3 {

    private static final String URL = "jdbc:postgresql://localhost:5432/MoviesDB";
    private static final String USER = "postgres";
    private static final String PASSWORD = "priyanka";

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("------------MediaMatrix----------------");
        movies();
    }

    // Connect to the database
    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ResultSet executeQuery(String query) {
        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static short firstCat() {
        System.out.println("Select your media type.");
        System.out.println("1. Movies");
        System.out.println("2. Books");
        return sc.nextShort();
    }

    static void languageMenu() {
        System.out.println("Select the required language.");
        System.out.println("1. English");
        System.out.println("2. Telugu");
        System.out.println("3. Hindi");

        short choice = sc.nextShort();
        languageChoices_Movies(choice);
    }

    static void movies() {
        switch (firstCat()) {
            case 1:
                languageMenu();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    // Generic method to display unique options for directors, actors, producers, age ratings, or awards
    static void displayAndSelectOptions(short languageChoice, String criterion) {
        String language = getLanguage(languageChoice);
        String query = "SELECT DISTINCT " + criterion + " FROM movies WHERE language_of_film = ?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, language);
            ResultSet rs = stmt.executeQuery();

            List<String> options = new ArrayList<>();
            int i = 1;
            
            if (criterion.equals("star_crew")) {
                // Use a Set to store unique actors, handling special characters and trimming spaces
                Set<String> uniqueActors = new HashSet<>();

                while (rs.next()) {
                    String[] actors = rs.getString("star_crew")
                                        .replace("{", "")
                                        .replace("}", "")
                                        .split(",");

                    for (String actor : actors) {
                        // Trim spaces and handle special characters
                        String cleanedActor = actor.trim().replaceAll("[^\\w\\s]", "");
                        if (!cleanedActor.isEmpty()) {
                            uniqueActors.add(cleanedActor);
                        }
                    }
                }

                // Display unique actors
                System.out.println("Available Actors:");
                for (String actor : uniqueActors) {
                    options.add(actor);
                    System.out.println(i + ". " + actor);
                    i++;
                }
            } else {
                // For other criteria, display directly from the result set
                System.out.println("Available " + criterion + "s:");
                while (rs.next()) {
                    String option = rs.getString(criterion);
                    options.add(option);
                    System.out.println(i + ". " + option);
                    i++;
                }
            }

            System.out.print("Select a " + criterion + " number: ");
            int selectedOption = sc.nextInt();
            if (selectedOption > 0 && selectedOption <= options.size()) {
                String selected = options.get(selectedOption - 1);
                searchMoviesByCriterion(selected, criterion, language);
            } else {
                System.out.println("Invalid choice.");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }
    }



    static void displayUniqueAwards(short languageChoice) {
        displayAndSelectOptions(languageChoice, "awards");
    }

    // Function to search movies based on selected criterion
 // Function to search movies based on selected criterion
 // Function to search movies based on selected criterion
    static void searchMoviesByCriterion(String selectedOption, String criterion, String language) {
        String query;
        if (criterion.equals("star_crew")) {
            // Use the ANY operator for array comparison in PostgreSQL
            query = "SELECT id, name FROM movies WHERE ? = ANY(star_crew) AND language_of_film = ?";
        } else {
            // Standard LIKE query for other fields
            query = "SELECT id, name FROM movies WHERE " + criterion + " LIKE ? AND language_of_film = ?";
        }

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (criterion.equals("star_crew")) {
                // Set selectedOption directly for star_crew array matching
                stmt.setString(1, selectedOption);
                stmt.setString(2, language);
            } else {
                // Use LIKE for other criteria
                stmt.setString(1, "%" + selectedOption + "%");
                stmt.setString(2, language);
            }

            ResultSet rs = stmt.executeQuery();
            List<Integer> movieIds = new ArrayList<>();
            System.out.println("\nMovies with " + criterion + ": " + selectedOption);
            int i = 1;
            while (rs.next()) {
                int id = rs.getInt("id");
                movieIds.add(id);
                System.out.println(i + ". " + rs.getString("name"));
                i++;
            }

            // Check if there are no movies found
            if (movieIds.isEmpty()) {
                System.out.println("No movies found with the selected " + criterion + ": " + selectedOption);
                return;
            }

            System.out.print("Select a movie number to view details: ");
            int movieChoice = sc.nextInt();
            int selectedMovieId = movieIds.get(movieChoice - 1);
            displayMovieInfo(selectedMovieId);
        } catch (SQLException e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }
    }

    // Search movies by year of release
    static void searchByYearOfRelease(short languageChoice) {
        System.out.print("Enter year of release: ");
        int year = sc.nextInt();
        String language = getLanguage(languageChoice);
        String query = "SELECT id, name FROM movies WHERE year_of_release = ? AND language_of_film = ?";
        displayMovieList(query, year, language);
    }

    // Search movies by approximate rating
    static void searchByRating(short languageChoice) {
        System.out.print("Enter rating (e.g., 8.4): ");
        float rating = sc.nextFloat();
        String language = getLanguage(languageChoice);
        String query = "SELECT id, name FROM movies WHERE average_rating BETWEEN ? AND ? AND language_of_film = ?";
        displayMovieList(query, rating - 0.5f, rating + 0.5f, language);
    }

    // Display movie list and allow selection
    static void displayMovieList(String query, Object... params) {
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            ResultSet rs = stmt.executeQuery();
            List<Integer> movieIds = new ArrayList<>();
            int count = 1;
            System.out.println("\nMatching Movies:");
            while (rs.next()) {
                System.out.println(count + ". " + rs.getString("name"));
                movieIds.add(rs.getInt("id"));
                count++;
            }

            if (!movieIds.isEmpty()) {
                System.out.print("Select a movie number to view details: ");
                int movieChoice = sc.nextInt();
                int selectedMovieId = movieIds.get(movieChoice - 1);
                displayMovieInfo(selectedMovieId);
            } else {
                System.out.println("No matching movies found.");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }
    }

 // Display full information about the selected movie
    static void displayMovieInfo(int movieId) {
    	
    	// ANSI escape sequences for bold, italic, and underline
        String bold = "\033[1m";
        String italic = "\033[3m";
        String underline = "\033[4m";
        String reset = "\033[0m"; // Resets the formatting
    	
        String query = "SELECT * FROM movies WHERE id = " + movieId;
        ResultSet rs = executeQuery(query);

        try {
            if (rs.next()) {
                System.out.println("\n\033[1;3;4m" + rs.getString("name") + "\033[0m"); // Bold and italic title
                System.out.println("\033[0;1mDuration           :\033[0;0m " + rs.getString("duration") + " mins");
                System.out.println("\033[0;1mDirector           :\033[0;0m " + rs.getString("director"));
                System.out.println("\033[0;1mProducer           :\033[0;0m " + rs.getString("producer"));
                System.out.println("\033[0;1mAverage Rating     :\033[0;0m " + rs.getFloat("average_rating") + "\n" +getStars(rs.getDouble("average_rating")));
                System.out.println("\033[0;1mPlot               :\033[0;0m " + rs.getString("plot"));
                System.out.println("\033[0;1mGenre              :\033[0;0m " + rs.getString("genre"));
                System.out.println("\033[0;1mLanguage           :\033[0;0m " + rs.getString("language_of_film"));
                System.out.println("\033[0;1mStreaming Platforms:\033[0;0m " + rs.getString("streaming_platforms"));

             // Display Songs as a list
                String[] songs = rs.getString("songs")
                                   .replace("{", "")
                                   .replace("}", "")
                                   .split(",");

                System.out.println("\n\033[0;1mSongs:\033[0;0m");
                for (String song : songs) {
                    System.out.println(" - " + song.trim().replace("\"", ""));
                }
                
                // Display Star Crew as a list
                String[] stars = rs.getString("star_crew").replace("{", "").replace("}", "").split(",");
                System.out.println("\n\n\033[0;1mCast:\033[0;0m");
                for (String star : stars) {
                    System.out.println(" - " + star.trim());
                }

             // Display Reviews with corresponding sources
                System.out.println("\n\n\033[0;1mReviews:\033[0;0m");
                for (int i = 1; i <= 5; i++) {
                    String[] reviews = (String[]) rs.getArray("review" + i).getArray();
                    for (int j = 0; j < reviews.length; j += 2) {
                        System.out.println("\nReview " + i + ": " + reviews[j].trim() + " - " + reviews[j + 1].trim());
                    }
                }

                System.out.println("-------------------------------------------------------------");
                
                //System.out.println("Path to ASCII Art Poster:");
                //System.out.println(rs.getString("poster_file_path"));
                //System.out.println("-----------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
 // Method to display rating with stars
 // Method to display rating with stars, always showing 10 stars in total
    static String getStars(double rating) {
        StringBuilder stars = new StringBuilder();
        int fullStars = (int) rating;
        int emptyStars = 10 - fullStars; // Adjust for a total of 10 stars

        // Append filled stars (★)
        for (int i = 0; i < fullStars; i++) 
            stars.append("★");

        // Add a half star if needed
        if (rating - fullStars >= 0.5) {
            stars.append("⯪");
            emptyStars--;
        }

        // Append empty stars (☆) for the remaining slots
        for (int i = 0; i < emptyStars; i++)
            stars.append("☆");

        return stars.toString();
    }



    static void languageChoices_Movies(short languageChoice) {
        System.out.println("\nSelect your search criterion.");
        System.out.println("1. Genre");
        System.out.println("2. Age rating");
        System.out.println("3. Director");
        System.out.println("4. Actor");
        System.out.println("5. Year of release");
        System.out.println("6. Rating");
        System.out.println("7. Producer");
        System.out.println("8. Award");

        short choice = sc.nextShort();
        switch (choice) {
            case 1:
                displayAndSelectGenres(languageChoice);
                break;
            case 2:
                displayAndSelectOptions(languageChoice, "age_rating");
                break;
            case 3:
                displayAndSelectOptions(languageChoice, "director");
                break;
            case 4:
                displayAndSelectOptions(languageChoice, "star_crew");
                break;
            case 5:
                searchByYearOfRelease(languageChoice);
                break;
            case 6:
                searchByRating(languageChoice);
                break;
            case 7:
                displayAndSelectOptions(languageChoice, "producer");
                break;
            case 8:
                displayUniqueAwards(languageChoice);
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    static String getLanguage(short choice) {
        switch (choice) {
            case 1:
                return "English";
            case 2:
                return "Telugu";
            case 3:
                return "Hindi";
            default:
                return "Unknown";
        }
    }

    static void displayAndSelectGenres(short languageChoice) {
        Set<String> genres = new HashSet<>();
        String query = "SELECT DISTINCT genre FROM movies WHERE language_of_film = ?";
        String language = getLanguage(languageChoice);

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, language);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String genre = rs.getString("genre").replaceAll("[{}]", "").trim();
                String[] genreList = genre.split(",");
                for (String g : genreList) {
                    genres.add(g.trim());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> genresList = new ArrayList<>(genres);
        System.out.println("\nAvailable Genres:");
        for (int i = 0; i < genresList.size(); i++) {
            System.out.println((i + 1) + ". " + genresList.get(i));
        }

        System.out.print("\nSelect a genre number: ");
        int genreChoice = sc.nextInt();
        if (genreChoice > 0 && genreChoice <= genresList.size()) {
            String selectedGenre = genresList.get(genreChoice - 1);
            searchMoviesByCriterion(selectedGenre, "genre", language);
        } else {
            System.out.println("Invalid choice.");
        }
    }
}
