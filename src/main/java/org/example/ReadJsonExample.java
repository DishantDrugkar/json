package org.example;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ReadJsonExample {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/json";
        String user = "postgres";
        String password = "root5113";
        String filePath = "demo.json";
        ObjectMapper objectMapper = new ObjectMapper();
        //ObjectMapper
        // jackson library that allows you to map JSON data to Java objects
       // objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            // Read JSON file and map to List of Person objects
            List<Person> people = objectMapper.readValue(new File(filePath), new TypeReference<List<Person>>() {});

            // Print each person
            for (Person person : people) {
                System.out.println(person);
            }
            insertDataIntoDatabase(people,url,user,password);
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void insertDataIntoDatabase(List<Person> people, String url, String user, String password) {
        String insertQuery = "INSERT INTO person (id, name, age) VALUES (?, ?, ?) ON CONFLICT (id) DO NOTHING;";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            System.out.println("Connected to the database!");

            // Iterate over the list of people and insert each person
            for (Person person : people) {
                preparedStatement.setInt(1,person.getId());
                preparedStatement.setString(2, person.getName());
                preparedStatement.setInt(3, person.getAge());
                preparedStatement.executeUpdate();
            }

            System.out.println("Data inserted successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
