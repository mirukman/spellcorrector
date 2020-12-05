package com.mirukman.spellcorrector.item;

import com.google.gson.JsonObject;

public class MovieItem {
    
    private final String title;
    private final String englishTitle;
    private final String releaseYear;
    private final String nation;
    private final String type;
    private final String genre;
    private final String status;
    private final String director;
    private final String producer;

    public static class Builder {
        private final String title;
        private String englishTitle;
        private String releaseYear;
        private String nation;
        private String type;
        private String genre;
        private String status;
        private String director;
        private String producer;

        public Builder(String title) {
            this.title = title;
        }

        public Builder englishTitle(String englishTitle) {
            this.englishTitle = englishTitle;
            return this;
        }

        public Builder releaseYear(String releaseYear) {
            this.releaseYear = releaseYear;
            return this;
        }

        public Builder nation(String nation) {
            this.nation = nation;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder genre(String genre) {
            this.genre = genre;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder director(String director) {
            this.director = director;
            return this;
        }

        public Builder producer(String producer) {
            this.producer = producer;
            return this;
        }

        public MovieItem build() {
            return new MovieItem(this);
        }

    }

    private MovieItem(Builder builder) {
        this.title = builder.title;
        this.englishTitle = builder.englishTitle;
        this.releaseYear = builder.releaseYear;
        this.nation = builder.nation;
        this.type = builder.type;
        this.genre = builder.genre;
        this.status = builder.status;
        this.director = builder.director;
        this.producer = builder.producer;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("제목: " + title)
                .append(", 영어제목: " + englishTitle)
                .append(", 개봉연도: " + releaseYear)
                .append(", 국가: " + nation)
                .append(", 종류: " + type)
                .append(", 장르: " + genre)
                .append(", 상태: " + status)
                .append(", 감독: " + director)
                .append(", 제작사: " + producer);

        return builder.toString();
    }
    
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();

        obj.addProperty("title", title);
        obj.addProperty("english_title", englishTitle);
        obj.addProperty("release_year", releaseYear);
        obj.addProperty("nation", nation);
        obj.addProperty("type", type);
        obj.addProperty("genre", genre);
        obj.addProperty("status", status);
        obj.addProperty("director", director);
        obj.addProperty("producer", producer);

        return obj;
    }

}
