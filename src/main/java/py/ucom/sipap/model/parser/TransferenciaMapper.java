package py.ucom.sipap.parser;

import java.util.Map;

import py.ucom.sipap.model.MerchantAccountInformation;
import py.ucom.sipap.model.Transferencia;

public class TransferenciaMapper {

    private final QrTlvParser parser = new QrTlvParser();

    public Transferencia convertir(String qr, String idTransaccion) {

        Map<String, String> campos = parser.parse(qr);

        Transferencia transferencia = new Transferencia();

        transferencia.setIdTransaccion(idTransaccion);

        transferencia.setPayloadFormatIndicator(
            campos.get("00")
        );

        transferencia.setPointOfInitiationMethod(
            campos.get("01")
        );

        transferencia.setMerchantCategoryCode(
            campos.get("52")
        );

        transferencia.setTransactionCurrency(
            campos.get("53")
        );

        if (campos.containsKey("54")) {
            transferencia.setTransactionAmount(
                Long.parseLong(campos.get("54"))
            );
        }

        transferencia.setCountryCode(
            campos.get("58")
        );

        transferencia.setMerchantName(
            campos.get("59")
        );

        transferencia.setMerchantCity(
            campos.get("60")
        );

        transferencia.setCrc(
            campos.get("63")
        );

        String merchantAccount = campos.get("32");

        if (merchantAccount != null) {

            Map<String, String> subCampos =
                parser.parse(merchantAccount);

            MerchantAccountInformation info =
                new MerchantAccountInformation();

            info.setGloballyUniqueIdentifier(
                subCampos.get("00")
            );

            info.setCodigoEntidad(
                subCampos.get("01")
            );

            info.setNumeroCuenta(
                subCampos.get("02")
            );

            transferencia.setMerchantAccountInformation(info);
        }

        return transferencia;
    }
}