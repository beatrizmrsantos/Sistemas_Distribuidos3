package util;

public class Token {

    private static String token;
    private static long TIME = 10000;
    private static String DELIMITER = "@";

    public static String set(long time, String id, String name) {
        long totaltime = time + TIME;

        String value = totaltime + DELIMITER + id;

        String secret = GetSecret.get(name);

        return Hash.of(value + DELIMITER + secret) + DELIMITER + value;
    }

    public static boolean matches(long currentTime, String t, String name) {
        String[] s = t.split(DELIMITER);
        String hash = s[0];
        long totaltime = Long.parseLong(s[1]);
        String id = s[2];

        if (totaltime < currentTime) return false;


        String secret = GetSecret.get(name);

        String newHash = Hash.of(totaltime + DELIMITER + id + DELIMITER + secret);

        if(!hash.equals(newHash)) return false;

        return true;

    }
}
