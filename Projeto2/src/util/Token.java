package util;

public class Token {

    private static String token;
    private static long TIME = 10000;
    private static String DELIMITER = "@";

    public static void set(String t) {
        token = t;
    }

    public static String calculate(long time, String id, String name) {
        long totaltime = time + TIME;

        String value = totaltime + DELIMITER + id;

        return Hash.of(value + DELIMITER + GetSecret.get(name)) + DELIMITER + value;
    }


    public static boolean matches(String idGiven, long currentTime, String t, String name) {
        String[] s = t.split(DELIMITER);
        String hash = s[0];
        long totaltime = Long.parseLong(s[1]);
        String id = s[2];

        if (totaltime < currentTime) return false;

        if(!idGiven.equals(id)) return false;

        String newHash = Hash.of(totaltime + DELIMITER + id + DELIMITER + GetSecret.get(name));
        if(!hash.equals(newHash)) return false;

        return true;

    }
}
