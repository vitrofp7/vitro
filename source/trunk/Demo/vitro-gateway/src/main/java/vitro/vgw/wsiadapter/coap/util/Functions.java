/*
 * #--------------------------------------------------------------------------
 * # Copyright (c) 2013 VITRO FP7 Consortium.
 * # All rights reserved. This program and the accompanying materials
 * # are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * # http://www.gnu.org/licenses/lgpl-3.0.html
 * #
 * # Contributors:
 * #     Antoniou Thanasis (Research Academic Computer Technology Institute)
 * #     Paolo Medagliani (Thales Communications & Security)
 * #     D. Davide Lamanna (WLAB SRL)
 * #     Alessandro Leoni (WLAB SRL)
 * #     Francesco Ficarola (WLAB SRL)
 * #     Stefano Puglia (WLAB SRL)
 * #     Panos Trakadas (Technological Educational Institute of Chalkida)
 * #     Panagiotis Karkazis (Technological Educational Institute of Chalkida)
 * #     Andrea Kropp (Selex ES)
 * #     Kiriakos Georgouleas (Hellenic Aerospace Industry)
 * #     David Ferrer Figueroa (Telefonica Investigación y Desarrollo S.A.)
 * #
 * #--------------------------------------------------------------------------
 */
package vitro.vgw.wsiadapter.coap.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.Map.Entry;

public class Functions {
    private static Logger logger = LoggerFactory.getLogger(Functions.class);
	
	public static short byteArraytoShort(byte[] data) {
		return (short) (data[0] & 0xFF | data[1] << 8); // moves the 2nd byte as the high order byte of a zero padded word and ORs with a word with 0000 and the low Order Byte.
	}

    public static short byteArraytoSecShort(byte[] data) {

        short[] shorts = new short[data.length/2];
// to turn bytes to shorts as either big endian or little endian.
        ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
        return shorts[0];
    }

    public static int byteArraytoSecInteger(byte[] data) {
        //byte [] dataExt = new byte[] { 127, 1 ,0 ,0}; //{data[0], data[1], 0, 0};
        //logger.debug("tag 1");
        int[] ints = new int[data.length/4];
// to turn bytes to shorts as either big endian or little endian.
        //logger.debug("tag 2");
        ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(ints);
        //logger.debug("tag 3");
        return ints[0];
    }

	public static byte[] shortToByteArray(short data) {
		return new byte[] { (byte) data, (byte) (data >> 8) };
	}
	
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (value.equals(entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
}
