package edu.lehigh.cse216.phase0.backend.fko218;

import static spark.Spark.*;
import com.google.gson.*;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


class Datum {
    int index;
    String val1;
    String val2;
}


public class App {
    final ArrayList<Datum> datab;
    int index; // The next index in our database
    final Gson gson; //JSON parser

    /**
     * Get all data from our "database", and return it in JSON format
     */
    String getAllData() {
        String result = "";
        ArrayList<Datum> results = new ArrayList<>();
        synchronized (datab) {
            for (Datum d : datab) {
                if (d != null)
                    results.add(d);
            }
        }
        result = gson.toJson(results);
        return result;
    }

    /**
     * To insert and element, we assign it an index and put it in that
     * position in our "database"
     * @param d The element to insert
     */
    String insertDatum(Datum d) {
        // Only insert if the strings are both not null
        String str1 = "";
        String str2 = "";
        String str3 = "";
        String str4 = "";
        String str5 = "";

        Connection conn = database.getConnect(str1, str2, str3, str4, str5);
        if (d != null && d.val1 != null && d.val2 != null) {
            synchronized (datab) {
                int idx = index++;
                d.index = idx;
                datab.add(idx, d);
                database.insertRows(conn, idx, d.val1, d.val2);
            }
            return "{\"res\":\"ok\"}";
        }
        else {
            return "{\"res\":\"bad data\"}";
        }
    }

    App() {
        datab = new ArrayList<>();
        index = 0;
        gson = new Gson();
    }


    public static void main(String[] args) {

        //Create a table to store data. It matches the 'Datum' type from the previous
        // tutorial
        String st1 = "";
        String st2 = "";
        String st3 = "";
        String st4 = "";
        String st5 = "";

        Connection c = database.getConnect(st1, st2, st3, st4, st5);
        PreparedStatement stmt = null;

        System.out.println("Attempting to create tblData");
        String createStatement = "CREATE TABLE tblData (id INT(64) NOT NULL " + "AUTO_INCREMENT, val1 VARCHAR(200), val2 VARCHAR(200), " + "PRIMARY KEY(id))";
        try {
            stmt = c.prepareStatement(createStatement);
            stmt.execute();
            System.out.println("Table Created");
        } catch (SQLException e) {
            System.out.println("Table not created (it may already exist)");
        }


        App app = new App();
        // Set up static file service... where the data is stored in the web server
        staticFileLocation("/web");

        // GET '/' returns the index page
        get("/", (req, res) -> {
            res.redirect("/index.html");
            return "";
        });

        // GET '/data' returns a JSON string with all of the data in the
        // "database"
        get("/data", (req, res) -> {
            // NB: moving getAllData out of this lambda facilitates testing
            String result = app.getAllData();
            // Send a JSON object back, as a string. Tell the client that
            // everything is "OK" (status 200)
            database.showAll(c);
            res.status(200);
            res.type("application/json");
            return result;
        });

        //POST a new item into the database
        post("/data", (req, res) -> {
            // Try to create a Datum from the request object
            Datum d = app.gson.fromJson(req.body(), Datum.class);
            String result = app.insertDatum(d);
            res.status(200);
            res.type("application/json");
            return result;
        });

        put("/data/:id", (req, res) -> {
            int idx = Integer.parseInt(req.params("id"));
            String result = "";
            //Datum d = app.gson.fromJson(req.body(), Datum.class);
            //if (idx < app.datab.size() && app.datab.get(idx) != null) {
            //  app.datab.set(idx, app.insertDatum(d));
            // result = result = "{\"res\":\"ok\"}";
            //}
            //else {
            //  result = "{\"res\":\"error\"}";
            //}
            //return result;
        });

        // Route for DELETEing one record. The '.id' part of the route gets
        // turned into a "param" of the req object.
        delete("/data/:id", (req, res) -> {
            // Note: this code is not easy to test, because it is inlined into
            // the lambda
            String result = "";
            int idx = Integer.parseInt(req.params("id"));
            synchronized (app.datab) {
                // We can't actually remove from the database, or our indexing
                // will get messed up. INstead we will set the row's content
                // to NULL
                if (idx < app.datab.size() && app.datab.get(idx) != null) {
                    app.datab.set(idx, null);
                    result = "{\"res\":\"ok\"}";
                }
                else {
                    result = "{\"res\":\"error\"}";
                }
            }
            return result;

        });


    }
}