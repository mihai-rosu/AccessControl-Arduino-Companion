package com.example.mihai.accesscontrol;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

/**
 * Created by Mihai on 12.05.2017.
 */

public class LoggedUser {
    private User loggedUser;
    private Context context;

    public LoggedUser(Context context) {
        this.context = context;
    }

    public void logOut() throws FileNotFoundException {
        try {
            File file = new File(context.getFilesDir(), "logged.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            String line = "null";
            writer.write(line);

            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException thrown");
        } catch (IOException ex) {
            System.out.println("IOException thrown");
        }
    }

    public User getLoggedUser() throws FileNotFoundException {
        try {
            File file = new File(context.getFilesDir(), "logged.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            line = reader.readLine();
            if (line != "null") {
                StringTokenizer st = new StringTokenizer(line, ";");
                User user = new User(st.nextToken(), st.nextToken());
                this.loggedUser = user;
                reader.close();
                return user;
            }
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException thrown");
        } catch (IOException e) {
            System.out.println("IOException thrown");
        } catch (Exception e) {
            System.out.println("Exception thrown");
        }
        return null;
    }

    public void setLoggedUser(User user) throws FileNotFoundException {
        try {
            File file = new File(context.getFilesDir(), "logged.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            String line = user.getUsername() + ";" + user.getPassword();
            writer.write(line);

            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException thrown");
        } catch (IOException ex) {
            System.out.println("IOException thrown");
        }
    }
}
