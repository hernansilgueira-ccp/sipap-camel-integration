package py.ucom.sipap.generator;

public class QrGenerator {

    private QrGenerator() {
    }

    private static String tlv(String tag, String valor) {
        return tag
                + String.format("%02d", valor.length())
                + valor;
    }

    private static String generarQr(
            String codigoBanco,
            String cuenta,
            String comercio,
            long monto) {

        String merchantAccount =
                tlv("00", "py.gov.bcp.sip")
                + tlv("01", codigoBanco)
                + tlv("02", cuenta);

        return tlv("00", "01")
                + tlv("01", "12")
                + tlv("32", merchantAccount)
                + tlv("52", "5731")
                + tlv("53", "600")
                + tlv("54", String.valueOf(monto))
                + tlv("58", "PY")
                + tlv("59", comercio)
                + tlv("60", "ASUNCION")
                + tlv("63", "A1B2");
    }

    public static String generarQrItau() {
        return generarQr(
            "0015",
            "1234567890",
            "TIENDA ITAU",
            15000
        );
    }

    public static String generarQrAtlas() {
        return generarQr(
            "0007",
            "9876543210",
            "TIENDA ATLAS",
            25000
        );
    }

    public static String generarQrFamiliar() {
        return generarQr(
            "0020",
            "1122334455",
            "TIENDA FAMILIAR",
            35000
        );
    }
    public static String generarQrBancoDesconocido() {
    return generarQr(
        "9999",
        "5555555555",
        "TIENDA DESCONOCIDA",
        45000
    );
}

    public static String generarQrMontoInvalido() {
            return generarQr(
                "0015",
                "1234567890",
                "TIENDA MONTO ALTO",
                10_000_000
            );
        }

    public static String generarQrChecksumInvalido() {

            String merchantAccount =
                    tlv("00", "py.gov.bcp.sip")
                    + tlv("01", "0015")
                    + tlv("02", "1234567890");

            return tlv("00", "01")
                    + tlv("01", "12")
                    + tlv("32", merchantAccount)
                    + tlv("52", "5731")
                    + tlv("53", "600")
                    + tlv("54", "15000")
                    + tlv("58", "PY")
                    + tlv("59", "TIENDA CRC ERROR")
                    + tlv("60", "ASUNCION")
                    + tlv("63", "FFFF");
        }

    public static String generarQrTlvInvalido() {

            // Tag 59 declara longitud 20,
            // pero el valor real es mucho mas corto.
            return "000201"
                    + "010212"
                    + "5910ERROR";
        }
    }