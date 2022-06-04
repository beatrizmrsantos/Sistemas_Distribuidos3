package util;

public class Token {

    private static String token;
    private static long TIME = 10000;
    private static String DELIMITER = "@";

    public static String set(long time, String fileId, String name) {
        long totaltime = time + TIME;

        String value = totaltime + DELIMITER + fileId;

        String secret = GetSecret.get(name);

        return Hash.of(value + DELIMITER + secret) + DELIMITER + value;
    }

    public static boolean matches(String t, String name) {
        String[] s = String.format(t).split(DELIMITER);
        String hash = s[0];
        long totaltime = Long.parseLong(s[1]);
        String id = s[2];

        if (totaltime > System.currentTimeMillis()) {
            return false;
        }

        String secret = GetSecret.get(name);

        String newHash = Hash.of(totaltime + DELIMITER + id + DELIMITER + secret);

        if(hash.equals(newHash)){
            return true;
        }
        return false;

    }
}
