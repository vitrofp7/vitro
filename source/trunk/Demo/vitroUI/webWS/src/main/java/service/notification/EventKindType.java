
package service.notification;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EventKindType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EventKindType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Register"/>
 *     &lt;enumeration value="Observation"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EventKindType", namespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/notification/v1/types")
@XmlEnum
public enum EventKindType {

    @XmlEnumValue("Register")
    REGISTER("Register"),
    @XmlEnumValue("Observation")
    OBSERVATION("Observation");
    private final String value;

    EventKindType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EventKindType fromValue(String v) {
        for (EventKindType c: EventKindType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
