package com.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.Scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    public static class Quote {
        protected String dialog;
        protected String character_id;
        protected String movie_id;
    
        // Constructor
        public Quote() throws IOException, InterruptedException {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().header("Authorization","Bearer qHiH75f6bA4YIbz61Ubf")
                .uri(URI.create("https://the-one-api.dev/v2/quote")).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(response.body());
            JsonNode docs = jsonResponse.get("docs");
    
            // Selecting a random QUOTE from the API JSON
            int randomIndex = new Random().nextInt(docs.size());
            JsonNode randomQuote = docs.get(randomIndex);
    
            this.dialog = randomQuote.get("dialog").asText();
            this.character_id = randomQuote.get("character").asText();
            this.movie_id = randomQuote.get("movie").asText();
        }
    
        // Getters
        public String getDialog() {
            return dialog;
        }
    
        public String getCharacterId() {
            return character_id;
        }
    
        public String getMovieId() {
            return movie_id;
        }
    }

    public static class Movie extends Quote {
        private String movie_name;
    
        public Movie() throws IOException, InterruptedException {
            super();
    
            HttpClient client = HttpClient.newHttpClient();
            String url = "https://the-one-api.dev/v2/movie/" + movie_id;
            HttpRequest request = HttpRequest.newBuilder().header("Authorization","Bearer qHiH75f6bA4YIbz61Ubf")
               .uri(URI.create(url))
                .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
            ObjectMapper mapper = new ObjectMapper();
            JsonNode movieResponse = mapper.readTree(response.body());
            JsonNode movieDocs = movieResponse.get("docs");
            JsonNode movie = movieDocs.get(0);
            this.movie_name = movie.get("name").asText();
        }
    
        public String getMovieName() {
            return movie_name;
        }
    }

    public static class Character extends Movie {
        private String name;
    
        public Character() throws IOException, InterruptedException {
            super();
    
            HttpClient client = HttpClient.newHttpClient();
            String url = "https://the-one-api.dev/v2/character/" + getCharacterId();
            HttpRequest request = HttpRequest.newBuilder().header("Authorization","Bearer qHiH75f6bA4YIbz61Ubf")
                .uri(URI.create(url))
                .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
            ObjectMapper mapper = new ObjectMapper();
            JsonNode characterResponse = mapper.readTree(response.body());
            JsonNode characterDocs = characterResponse.get("docs");
            JsonNode character = characterDocs.get(0);
            this.name = character.get("name").asText();
        }
    
        public String getName() {
            return name;
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        // Fetching a QUOTE from the API
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().header("Authorization","Bearer qHiH75f6bA4YIbz61Ubf")
            .uri(URI.create("https://the-one-api.dev/v2/quote")).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = mapper.readTree(response.body());
        JsonNode docs = jsonResponse.get("docs");

        // Selecting a random QUOTE from the API JSON
        int randomIndex = new Random().nextInt(docs.size());
        JsonNode randomQuote = docs.get(randomIndex);

        System.out.println(randomQuote);

        // Parsing the random quote json
        String dialog = randomQuote.get("dialog").asText();
        String character_id = randomQuote.get("character").asText();
        String movie_id = randomQuote.get("movie").asText();

        System.out.println("Dialog: " + dialog);
        System.out.println("Character ID: " + character_id);

        // Fetching the Character of the quote from his ID
        String url = "https://the-one-api.dev/v2/character/" + character_id;
        request = HttpRequest.newBuilder().header("Authorization","Bearer qHiH75f6bA4YIbz61Ubf")
            .uri(URI.create(url))
            .build();
        
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Print character JSON
        System.out.println(response.body());

        // Parsing and getting the name of the character
        JsonNode characterResponse = mapper.readTree(response.body());
        JsonNode characterDocs = characterResponse.get("docs");
        JsonNode character = characterDocs.get(0);
        String name = character.get("name").asText();

        System.out.println("Character Name: " + name);
        
        Character character_1 = new Character(); // Fetches a random quote from the API and gets the character
        
        System.out.println("Dialog: " + character_1.getDialog());
        System.out.println("Character Name: " + character_1.getName());
        System.out.println("Movie Name: " + character_1.getMovieName());

        Scanner scanner = new Scanner(System.in);
        Character character_scan = new Character(); // Fetches a random quote from the API and gets the character

        System.out.println("Here's a quote: " + character_scan.getDialog());
        System.out.println("Can you guess the character who said this quote?");
        String userGuess = scanner.nextLine();

        if (userGuess.equalsIgnoreCase(character_scan.getName())) {
            System.out.println("Correct! The quote is from " + character_scan.getName() + " in the movie " + character_scan.getMovieName() + ".");
        } else {
            System.out.println("Sorry, that's incorrect. The quote is from " + character_scan.getName() + " in the movie " + character_scan.getMovieName() + ".");
        }

        scanner.close();
    }
}