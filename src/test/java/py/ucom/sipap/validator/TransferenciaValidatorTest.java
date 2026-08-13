package py.ucom.sipap.validator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import py.ucom.sipap.model.MerchantAccountInformation;
import py.ucom.sipap.model.Transferencia;

public class TransferenciaValidatorTest {

    private TransferenciaValidator validator;
    private Transferencia transferencia;

    @BeforeEach
    void preparar() {

        validator = new TransferenciaValidator();

        MerchantAccountInformation merchant =
            new MerchantAccountInformation();

        merchant.setGloballyUniqueIdentifier(
            "py.gov.bcp.sip"
        );

        merchant.setCodigoEntidad("0015");
        merchant.setNumeroCuenta("1234567890");

        transferencia = new Transferencia();

        transferencia.setIdTransaccion("TX-TEST-001");
        transferencia.setPayloadFormatIndicator("01");
        transferencia.setPointOfInitiationMethod("12");

        transferencia.setMerchantAccountInformation(
            merchant
        );

        transferencia.setMerchantCategoryCode("5731");
        transferencia.setTransactionCurrency("600");
        transferencia.setTransactionAmount(15000L);
        transferencia.setCountryCode("PY");
        transferencia.setMerchantName("TIENDA TEST");
        transferencia.setMerchantCity("ASUNCION");
        transferencia.setCrc("A1B2");
    }

    @Test
    void transferenciaValidaDebeSerAceptada() {

        assertDoesNotThrow(
            () -> validator.validar(transferencia)
        );
    }

    @Test
    void bancoDesconocidoDebeSerRechazado() {

        transferencia
            .getMerchantAccountInformation()
            .setCodigoEntidad("9999");

        IllegalArgumentException exception =
            assertThrows(
                IllegalArgumentException.class,
                () -> validator.validar(transferencia)
            );

        assertTrue(
            exception.getMessage()
                .contains("Banco destino desconocido")
        );
    }

    @Test
    void montoIgualADiezMillonesDebeSerRechazado() {

        transferencia.setTransactionAmount(
            10_000_000L
        );

        IllegalArgumentException exception =
            assertThrows(
                IllegalArgumentException.class,
                () -> validator.validar(transferencia)
            );

        assertTrue(
            exception.getMessage()
                .contains("maximo permitido")
        );
    }

    @Test
    void montoSuperiorADiezMillonesDebeSerRechazado() {

        transferencia.setTransactionAmount(
            15_000_000L
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> validator.validar(transferencia)
        );
    }

    @Test
    void crcIncorrectoDebeSerRechazado() {

        transferencia.setCrc("FFFF");

        IllegalArgumentException exception =
            assertThrows(
                IllegalArgumentException.class,
                () -> validator.validar(transferencia)
            );

        assertEquals(
            "Checksum invalido",
            exception.getMessage()
        );
    }

    @Test
    void monedaIncorrectaDebeSerRechazada() {

        transferencia.setTransactionCurrency("840");

        assertThrows(
            IllegalArgumentException.class,
            () -> validator.validar(transferencia)
        );
    }

    @Test
    void montoEsObligatorioParaQrDinamico() {

        transferencia.setTransactionAmount(null);

        assertThrows(
            IllegalArgumentException.class,
            () -> validator.validar(transferencia)
        );
    }

    @Test
    void montoDebeSerMayorACero() {

        transferencia.setTransactionAmount(0L);

        assertThrows(
            IllegalArgumentException.class,
            () -> validator.validar(transferencia)
        );
    }

    @Test
    void globallyUniqueIdentifierDebeSerCorrecto() {

        transferencia
            .getMerchantAccountInformation()
            .setGloballyUniqueIdentifier(
                "identificador.incorrecto"
            );

        assertThrows(
            IllegalArgumentException.class,
            () -> validator.validar(transferencia)
        );
    }
}