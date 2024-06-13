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

    public static double calculateSimilarity(String str1, String str2) {
        int maxLength = Math.max(str1.length(), str2.length());
        int minLength = Math.min(str1.length(), str2.length());
        int commonChars = 0;
    
        for (int i = 0; i < minLength; i++) {
            if (str1.charAt(i) == str2.charAt(i)) {
                commonChars++;
            }
        }
    
        return (double) commonChars / maxLength;
    }


    public static void main(String[] args) throws IOException, InterruptedException {

        Scanner ui = new Scanner(System.in);

        boolean gameSet = true;
        int gameOption; 
        while (gameSet) {

            System.out.println("Welcome to the Middle-Earth guessing game!");
            System.out.println("1 - PLAY!");
            System.out.println("2 - EXIT!");
            
            gameOption = ui.nextInt();
            ui.nextLine();

            switch (gameOption) {
                case 1:
                   boolean gameLoop = true;
                 while (gameLoop){


                    // Fechting API data
                    Character gameChar = new Character();
                    

                    
                    System.out.println("\n" +"Movie Name: " + gameChar.getMovieName());
                    System.out.println("Can you guess the character who said this quote below?");
                    System.out.println(gameChar.getDialog() + "\n");
                    
                    System.out.println("Who said that little hobbit?");
                    String playerGuess = ui.nextLine();

                    if (calculateSimilarity(playerGuess.toLowerCase(), gameChar.getName().toLowerCase()) >= 0.2 ) {
                        System.out.println("\n"+"Correct! The quote is from " + gameChar.getName() + " in the movie " + gameChar.getMovieName() + ".");
                    } else {
                        System.out.println("\n"+"Sorry, that's incorrect. The quote is from " + gameChar.getName() + " in the movie " + gameChar.getMovieName() + ". \n");
                    }

                    System.out.println("Do you want to play again little hobbit? (Y/N)");
                    String playAgain;
                    playAgain = ui.nextLine();

                    if (playAgain == "n"){
                        System.out.println("Thanks for playing you're a real hobbit!");
                        gameSet = false;
                        gameLoop = false;
                    }
                }
                break;

                case 2:
                    System.out.println("Thanks for playing you're a real hobbit!");
                    gameSet = false;
                    break;
            
                default:
                    break;
            }
        }
    }
}