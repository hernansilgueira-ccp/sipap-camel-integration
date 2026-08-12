package py.ucom.sipap.parser;

import java.util.LinkedHashMap;
import java.util.Map;

public class QrTlvParser {

    public Map<String, String> parse(String tlv) {

        Map<String, String> campos = new LinkedHashMap<>();

        int posicion = 0;

        while (posicion < tlv.length()) {

            if (posicion + 4 > tlv.length()) {
                throw new IllegalArgumentException(
                    "Estructura TLV incompleta en la posicion " + posicion
                );
            }

            String tag = tlv.substring(posicion, posicion + 2);
            posicion += 2;

            String longitudTexto = tlv.substring(posicion, posicion + 2);
            posicion += 2;

            int longitud;

            try {
                longitud = Integer.parseInt(longitudTexto);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    "Longitud invalida para el tag " + tag
                );
            }

            if (posicion + longitud > tlv.length()) {
                throw new IllegalArgumentException(
                    "Longitud declarada invalida para el tag " + tag
                );
            }

            String valor = tlv.substring(posicion, posicion + longitud);
            posicion += longitud;

            campos.put(tag, valor);
        }

        return campos;
    }
}