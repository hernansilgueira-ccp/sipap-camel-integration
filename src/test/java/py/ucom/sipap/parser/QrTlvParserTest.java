package py.ucom.sipap.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class QrTlvParserTest {

    @Test
    void debeParsearTlvValido() {

        QrTlvParser parser = new QrTlvParser();

        Map<String, String> resultado =
            parser.parse("000201010212");

        assertEquals("01", resultado.get("00"));
        assertEquals("12", resultado.get("01"));
    }

    @Test
    void debeRechazarLongitudIncorrecta() {

        QrTlvParser parser = new QrTlvParser();

        IllegalArgumentException exception =
            assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse("5910ERROR")
            );

        assertTrue(
            exception.getMessage()
                .contains("Longitud declarada invalida")
        );
    }

    @Test
    void debeRechazarEstructuraIncompleta() {

        QrTlvParser parser = new QrTlvParser();

        assertThrows(
            IllegalArgumentException.class,
            () -> parser.parse("00")
        );
    }
}