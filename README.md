# MediaMatrix

## Overview
MediaMatrix is a Java-based console application that allows users to browse and search for movies stored in a PostgreSQL database. It provides an interactive menu to filter movies by language, director, producer, actors, awards, year of release, and rating. The application also displays detailed information about selected movies, including duration, plot, genre, streaming availability, and reviews.

## Features
- Interactive text-based menu for browsing and filtering movies.
- Search movies by various criteria such as language, director, actors, awards, and rating.
- Displays detailed movie information, including star ratings and ASCII-stylized text formatting.
- Uses ANSI escape codes to format output for better readability.
- Supports PostgreSQL database integration for efficient data retrieval.
- **Upcoming Feature**: Book browsing and search functionality.

## Technologies Used
- **Java**: Core programming language.
- **PostgreSQL**: Database for storing movie information.
- **JDBC**: Java Database Connectivity for interacting with PostgreSQL.

## Prerequisites
- Java Development Kit (JDK) installed (Java 8 or higher recommended).
- PostgreSQL installed and configured.
- PostgreSQL JDBC Driver added to the project.

## Installation and Setup
1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/MediaMatrix.git
   cd MediaMatrix
   ```
2. Update database connection details in `MediaMat3.java`:
   ```java
   private static final String URL = "jdbc:postgresql://localhost:5432/MoviesDB";
   private static final String USER = "postgres";
   private static final String PASSWORD = "yourpassword";
   ```
3. Ensure PostgreSQL is running and the `MoviesDB` database exists.
4. Compile and run the program:
   ```sh
   javac -d . MediaMat3.java
   java simplemenu.MediaMat3
   ```

## Usage
1. Select media type (Currently supports Movies only).
2. Choose a language (English, Telugu, Hindi).
3. Filter movies by director, producer, actors, awards, year, or rating.
4. View detailed information for a selected movie.
5. Exit the application when finished.

## Example Output
```
------------MediaMatrix----------------
Select your media type.
1. Movies
2. Books
1
Select the required language.
1. English
2. Telugu
3. Hindi
1
Available Directors:
1. Christopher Nolan
2. Quentin Tarantino
Select a director number: 1
Movies directed by Christopher Nolan:
1. Inception
2. Interstellar
Select a movie number to view details: 1

Inception
Duration           : 148 mins
Director           : Christopher Nolan
Producer          : Emma Thomas
Average Rating     : 8.8
⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐
Plot               : A thief enters dreams to steal secrets.
Genre              : Sci-Fi, Thriller
Language           : English
Streaming Platforms: Netflix, Amazon Prime

Songs:
 - Time
 - Dream is Collapsing

Cast:
 - Leonardo DiCaprio
 - Joseph Gordon-Levitt

Reviews:
Review 1: "Mind-bending masterpiece!" - NY Times
Review 2: "A cinematic marvel." - Rotten Tomatoes
-------------------------------------------------------------
```
