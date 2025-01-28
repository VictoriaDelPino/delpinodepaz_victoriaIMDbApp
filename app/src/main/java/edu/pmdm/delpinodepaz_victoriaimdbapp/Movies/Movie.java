package edu.pmdm.delpinodepaz_victoriaimdbapp.Movies;

public class Movie {
        private String photo;         // URL de la carátula
        private String title;         // Título de la película
        private String description;   // Descripción de la película
        private String releaseDate;   // Fecha de estreno
        private String rating;        // Calificación de la película

        // Constructor
        public Movie(String photo, String title, String description, String releaseDate, String rating) {
            this.photo = photo;
            this.title = title;
            this.description = description;
            this.releaseDate = releaseDate;
            this.rating = rating;
        }

        // Getters y setters
        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }
    }

