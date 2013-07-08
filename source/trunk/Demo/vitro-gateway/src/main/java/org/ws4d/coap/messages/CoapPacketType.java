

package org.ws4d.coap.messages;

/**
 * Type-safe class for CoapPacketTypes
 * 
 * @author Nico Laum <nico.laum@uni-rostock.de>
 * @author Sebastian Unger <sebastian.unger@uni-rostock.de>
 * @author Christian Lerche <christian.lerche@uni-rostock.de>
 */
public enum CoapPacketType {
    CON(0x00),
    NON(0x01),
    ACK(0x02),
    RST(0x03);

    private int packetType;

    CoapPacketType(int packetType) {
    	if (packetType >= 0x00 && packetType <= 0x03){
    		this.packetType = packetType;
    	} else {
    		throw new IllegalStateException("Unknown CoAP Packet Type");
		}
    	
    }

    public static CoapPacketType getPacketType(int packetType) {
        if (packetType == 0x00)
            return CON;
        else if (packetType == 0x01)
            return NON;
        else if (packetType == 0x02)
            return ACK;
        else if (packetType == 0x03)
            return RST;
        else
        	throw new IllegalStateException("Unknown CoAP Packet Type");
    }
    
    public int getValue() {
        return packetType;
    }
}
