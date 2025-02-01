package edu.pmdm.delpinodepaz_victoriaimdbapp.Movies;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private String id;
    private String photo;         // URL de la carátula
    private String title;         // Título de la película
    private String description;   // Descripción de la película
    private String releaseDate;   // Fecha de estreno
    private String rating;        // Calificación de la película

    public Movie() {
    }

    public Movie(String description, String title, String photo, String rating, String releaseDate, String id) {
        this.description = description;
        this.title = title;
        this.photo = photo;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.id = id;
    }

    protected Movie(Parcel in) {
        id = in.readString();
        photo = in.readString();
        title = in.readString();
        description = in.readString();
        releaseDate = in.readString();
        rating = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(photo);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(releaseDate);
        parcel.writeString(rating);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", photo='" + photo + '\'' +
                ", title='" + title + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }
}
