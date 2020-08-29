package com.example.roomie.util;

/**
 * A utility class to validate form input entered by the users.
 */
public class FormValidator {

    public static boolean isValidHouseName(String houseName) {
        // TODO add regex check for invalid chars?
        if (houseName == null) {
            return false;
        }

        if (houseName.length() < 3 || houseName.length() > 50) {
            return false;
        }

        return true;
    }

    public static boolean isValidHouseAddress(String houseAddress) {
        // TODO add regex check for invalid chars?
        if (houseAddress == null) {
            return false;
        }

        if (houseAddress.length() < 3 || houseAddress.length() > 100) {
            return false;
        }

        return true;
    }

    public static boolean isValidHouseDesc(String houseDesc) {
        // TODO add regex check for invalid chars?

        return true;
    }

    public static boolean isValidUsername(String username) {
        if (username.isEmpty()) {
            return false;
        }

        if (username.length() < 3 || username.length() > 30) {
            return false;
        }

        return true;
    }

}
