package utils;

import models.UserModel;

import java.util.Random;
import java.util.UUID;

public class UserGenerator {

    private static final Random random = new Random();

    public static UserModel generateRandomUser() {
        String random = UUID.randomUUID().toString().substring(0, 8);
        return new UserModel("User" + random, "Password#" + random, "USER");
    }
}
