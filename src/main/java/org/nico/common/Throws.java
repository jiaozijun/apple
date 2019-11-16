package org.nico.common;

public class Throws {
    private static class RateLimitException extends RuntimeException {
        public RateLimitException(String message) {
            super(message);
        }
        public static final RateLimitException RATE_LIMIT_EXCEPTION = new RateLimitException("rate limit");

        public static boolean is(Throwable e) {
            return e == RATE_LIMIT_EXCEPTION || e instanceof RateLimitException;
        }


    }

    public static void throwRateLimit(boolean isThrow) throws RateLimitException {
        if (isThrow) throw RateLimitException.RATE_LIMIT_EXCEPTION;
    }

    public static boolean isRateLimit(Throwable e) {
        return RateLimitException.is(e);
    }

}
