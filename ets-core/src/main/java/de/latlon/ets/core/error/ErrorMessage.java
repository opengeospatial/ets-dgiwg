package de.latlon.ets.core.error;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Utility class for retrieving and formatting localized error messages that describe failed assertions.
 * 
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 */
public final class ErrorMessage {

    private static final String BASE_NAME = "de.latlon.ets.core.error.MessageBundle";

    private static ResourceBundle msgResources = ResourceBundle.getBundle( BASE_NAME );

    private ErrorMessage() {
    }

    /**
     * Produces a formatted error message using the supplied substitution arguments and the current locale. The
     * arguments should reflect the order of the placeholders in the message template.
     * 
     * @param msgKey
     *            The {@link ErrorMessageKey} identifying the message template.
     * @param args
     *            An array of arguments to be formatted and substituted in the content of the message.
     * @return A String containing the message content. If no message is found for the given key, a
     *         {@link java.util.MissingResourceException} is thrown.
     */
    public static String format( ErrorMessageKey msgKey, Object... args ) {
        return MessageFormat.format( msgResources.getString( msgKey.getKey() ), args );
    }

    /**
     * Retrieves a simple message according to the current locale.
     * 
     * @param msgKey
     *            The {@link ErrorMessageKey} identifying the message.
     * @return A String containing the message content. If no message is found for the given key, a
     *         {@link java.util.MissingResourceException} is thrown.
     */
    public static String get( ErrorMessageKey msgKey ) {
        return msgResources.getString( msgKey.getKey() );
    }

}