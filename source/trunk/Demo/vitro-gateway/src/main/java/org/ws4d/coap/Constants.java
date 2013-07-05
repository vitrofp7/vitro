

package org.ws4d.coap;

/**
 * @author Christian Lerche <christian.lerche@uni-rostock.de>
 */

public final class Constants {
    public final static int MESSAGE_ID_MIN = 0;
    public final static int MESSAGE_ID_MAX = 65535;
    public final static int COAP_MESSAGE_SIZE_MAX = 1152;
    public final static int COAP_DEFAULT_PORT = 5683;
    public final static int COAP_DEFAULT_MAX_AGE_S = 60;
    public final static int COAP_DEFAULT_MAX_AGE_MS = COAP_DEFAULT_MAX_AGE_S * 1000;
}
