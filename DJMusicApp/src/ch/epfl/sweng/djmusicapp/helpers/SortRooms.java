/**
 * 
 */
package ch.epfl.sweng.djmusicapp.helpers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ch.epfl.sweng.djmusicapp.Room;

/**
 * @author Tristan Marchal
 * 
 */
public class SortRooms {

    public static void sortByUsers(List<Room> rooms) {
        Collections.sort(rooms, new Comparator<Room>() {
            @Override
            public int compare(Room r1, Room r2) {
                if (r1.getUsers().size() > r2.getUsers().size()) {
                    return -1;
                } else if (r1.getUsers().size() == r2.getUsers().size()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
    }

    public static void sortByName(List<Room> rooms) {
        Collections.sort(rooms, new Comparator<Room>() {
            @Override
            public int compare(Room r1, Room r2) {
                return r1.getName().compareToIgnoreCase(r2.getName());
            }
        });
    }

    // Unused
    public static void sortByStatusPublicFirst(List<Room> rooms) {
        Collections.sort(rooms, new Comparator<Room>() {
            @Override
            public int compare(Room r1, Room r2) {
                if (r1.getStatusToString().equals(Room.PUBLIC_STRING)
                        && r2.getStatusToString().equals(Room.PRIVATE_STRING)) {
                    return -1;
                } else if ((r1.getStatusToString().equals(Room.PUBLIC_STRING) && r2.getStatusToString().equals(
                        Room.PUBLIC_STRING))
                        || (r1.getStatusToString().equals(Room.PRIVATE_STRING) && r2.getStatusToString().equals(
                                Room.PRIVATE_STRING))) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
    }

    // Unused
    public static void sortByStatusPrivateFirst(List<Room> rooms) {
        Collections.sort(rooms, new Comparator<Room>() {
            @Override
            public int compare(Room r1, Room r2) {
                if (r1.getStatusToString().equals(Room.PRIVATE_STRING)
                        && r2.getStatusToString().equals(Room.PUBLIC_STRING)) {
                    return -1;
                } else if ((r1.getStatusToString().equals(Room.PUBLIC_STRING) && r2.getStatusToString().equals(
                        Room.PUBLIC_STRING))
                        || (r1.getStatusToString().equals(Room.PRIVATE_STRING) && r2.getStatusToString().equals(
                                Room.PRIVATE_STRING))) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });
    }
}

/**
 * Tristan
 */
