package py.ucom.sipap.validator;

import java.util.Set;

import py.ucom.sipap.model.Transferencia;

public class TransferenciaValidator {

    private static final Set<String> BANCOS_VALIDOS =
        Set.of("0015", "0007", "0020");

    public void validar(Transferencia t) {

        if (t == null) {
            throw new IllegalArgumentException("Transferencia vacia");
        }

        if (!"01".equals(t.getPayloadFormatIndicator())) {
            throw new IllegalArgumentException(
                "Payload Format Indicator invalido"
            );
        }

        if (!"11".equals(t.getPointOfInitiationMethod())
                && !"12".equals(t.getPointOfInitiationMethod())) {

            throw new IllegalArgumentException(
                "Point of Initiation Method invalido"
            );
        }

        if (t.getMerchantAccountInformation() == null) {
            throw new IllegalArgumentException(
                "Merchant Account Information ausente"
            );
        }

        if (!"py.gov.bcp.sip".equals(
                t.getMerchantAccountInformation()
                    .getGloballyUniqueIdentifier())) {

            throw new IllegalArgumentException(
                "Globally Unique Identifier invalido"
            );
        }

        String codigoEntidad =
            t.getMerchantAccountInformation().getCodigoEntidad();

        if (codigoEntidad == null || codigoEntidad.isBlank()) {
            throw new IllegalArgumentException(
                "Codigo de entidad ausente"
            );
        }

        if (!BANCOS_VALIDOS.contains(codigoEntidad)) {
            throw new IllegalArgumentException(
                "Banco destino desconocido: " + codigoEntidad
            );
        }

        String cuenta =
            t.getMerchantAccountInformation().getNumeroCuenta();

        if (cuenta == null || cuenta.isBlank()) {
            throw new IllegalArgumentException(
                "Numero de cuenta ausente"
            );
        }

        if (!"600".equals(t.getTransactionCurrency())) {
            throw new IllegalArgumentException(
                "Moneda invalida. Se esperaba PYG (600)"
            );
        }

        // QR dinamico
        if ("12".equals(t.getPointOfInitiationMethod())) {

            if (t.getTransactionAmount() == null) {
                throw new IllegalArgumentException(
                    "Monto obligatorio para QR dinamico"
                );
            }

            if (t.getTransactionAmount() <= 0) {
                throw new IllegalArgumentException(
                    "El monto debe ser mayor a cero"
                );
            }
        }

        if (t.getTransactionAmount() != null
                && t.getTransactionAmount() >= 10_000_000) {

            throw new IllegalArgumentException(
                "El monto supera o iguala el maximo permitido"
            );
        }

        if (!"PY".equals(t.getCountryCode())) {
            throw new IllegalArgumentException(
                "Codigo de pais invalido"
            );
        }

        if (!"A1B2".equals(t.getCrc())) {
            throw new IllegalArgumentException(
                "Checksum invalido"
            );
        }
    }
}