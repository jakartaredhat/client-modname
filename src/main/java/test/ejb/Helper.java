package test.ejb;

public class Helper {
    public static void assertEquals(final String messagePrefix,
                                    final Object expected, final Object actual, final StringBuilder sb)
            throws RuntimeException {
        sb.append('\n');
        if (messagePrefix != null) {
            sb.append('\n').append(messagePrefix).append(" ");
        }
        if (equalsOrNot(expected, actual)) {
            sb.append("Got the expected result:").append(actual).append("\t");
        } else {
            sb.append("Expecting ").append(expected).append(", but actual ")
                    .append(actual);
            throw new RuntimeException(sb.toString());
        }
    }
    public static void assertNotEquals(final String messagePrefix,
                                       final Object expected, final Object actual, final StringBuilder sb)
            throws RuntimeException {
        sb.append('\n');
        if (messagePrefix != null) {
            sb.append(messagePrefix).append(" ");
        }
        if (!equalsOrNot(expected, actual)) {
            sb.append("Got the expected NotEquals result. compareTo:")
                    .append(expected).append(", actual:").append(actual).append("\t");
        } else {
            sb.append("Expecting NotEquals, but got equals. compareTo:")
                    .append(expected).append(", and actual: ").append(actual);
            throw new RuntimeException(sb.toString());
        }
    }
    private static boolean equalsOrNot(final Object expected,
                                       final Object actual) {
        boolean sameOrNot = false;
        if (expected == null) {
            if (actual == null) {
                sameOrNot = true;
            }
        } else {
            sameOrNot = expected.equals(actual);
        }
        return sameOrNot;
    }
}
