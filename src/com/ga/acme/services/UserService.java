package com.ga.acme.services;

import com.ga.acme.enums.FilePath;
import com.ga.acme.exceptions.RecordNotFoundException;
import com.ga.acme.models.User;

import java.util.HashMap;
import java.util.Map;

import static com.ga.acme.util.FileHandler.getDataFromFile;

public class UserService {

    public static User getUserById(String id) {
        User user = null;
        HashMap<String, Map<String, String>> users = getDataFromFile(FilePath.USERS.getPath());
        try {
            if (users.containsKey(id)) {
                Map<String, String> values = users.get(id);
                user = User.mapToUserObject(values);
            } else {
                throw new RecordNotFoundException("User with this id " + id + " does not exist");
            }
        } catch (RecordNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return user;
    }
}
