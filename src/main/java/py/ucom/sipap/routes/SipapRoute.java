package py.ucom.sipap.routes;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import py.ucom.sipap.generator.QrGenerator;
import py.ucom.sipap.model.ResultadoTransferencia;
import py.ucom.sipap.model.Transferencia;
import py.ucom.sipap.parser.TransferenciaMapper;
import py.ucom.sipap.validator.TransferenciaValidator;

public class SipapRoute extends RouteBuilder {

    private final TransferenciaMapper mapper = new TransferenciaMapper();
    private final TransferenciaValidator validator = new TransferenciaValidator();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure() {

        // ==========================================
        // MANEJO CENTRALIZADO DE ERRORES
        // ==========================================
        onException(Exception.class)
            .handled(true)
            .process(exchange -> {

                Exception exception =
                    exchange.getProperty(
                        Exchange.EXCEPTION_CAUGHT,
                        Exception.class
                    );

                String idTransaccion =
                    exchange.getMessage()
                        .getHeader(
                            "idTransaccion",
                            String.class
                        );

                ResultadoTransferencia resultado =
                    new ResultadoTransferencia(
                        idTransaccion,
                        "RECHAZADA",
                        "Error de parsing o procesamiento: "
                            + exception.getMessage()
                    );

                exchange.getMessage().setBody(resultado);
            })
            .process(exchange ->
                imprimirResultado(exchange)
            );


        // ==========================================
        // PRODUCTOR ITAU
        // ==========================================
        from("timer:productor-itau?period=5000")
            .routeId("productor-itau")

            .setHeader(
                "idTransaccion",
                simple("TX${date:now:yyyyMMddHHmmssSSS}")
            )

            .setBody(
                constant(QrGenerator.generarQrItau())
            )

            .log(
                "Productor ITAU genero [${header.idTransaccion}]: ${body}"
            )

            .to("direct:sipap-in");


        // ==========================================
        // PRODUCTOR ATLAS
        // ==========================================
        from("timer:productor-atlas?period=7000")
            .routeId("productor-atlas")

            .setHeader(
                "idTransaccion",
                simple("TX${date:now:yyyyMMddHHmmssSSS}")
            )

            .setBody(
                constant(QrGenerator.generarQrAtlas())
            )

            .log(
                "Productor ATLAS genero [${header.idTransaccion}]: ${body}"
            )

            .to("direct:sipap-in");


        // ==========================================
        // PRODUCTOR FAMILIAR
        // ==========================================
        from("timer:productor-familiar?period=9000")
            .routeId("productor-familiar")

            .setHeader(
                "idTransaccion",
                simple("TX${date:now:yyyyMMddHHmmssSSS}")
            )

            .setBody(
                constant(QrGenerator.generarQrFamiliar())
            )

            .log(
                "Productor FAMILIAR genero [${header.idTransaccion}]: ${body}"
            )

            .to("direct:sipap-in");


        // ==========================================
        // CASOS INVALIDOS
        // ==========================================
        from("timer:prueba-banco-desconocido?repeatCount=1&delay=12000")
            .routeId("prueba-banco-desconocido")

            .setHeader(
                "idTransaccion",
                simple("TX-INVALID-BANCO-${date:now:HHmmssSSS}")
            )

            .setBody(
                constant(
                    QrGenerator.generarQrBancoDesconocido()
                )
            )

            .to("direct:sipap-in");


        from("timer:prueba-monto-invalido?repeatCount=1&delay=14000")
            .routeId("prueba-monto-invalido")

            .setHeader(
                "idTransaccion",
                simple("TX-INVALID-MONTO-${date:now:HHmmssSSS}")
            )

            .setBody(
                constant(
                    QrGenerator.generarQrMontoInvalido()
                )
            )

            .to("direct:sipap-in");


        from("timer:prueba-crc-invalido?repeatCount=1&delay=16000")
            .routeId("prueba-crc-invalido")

            .setHeader(
                "idTransaccion",
                simple("TX-INVALID-CRC-${date:now:HHmmssSSS}")
            )

            .setBody(
                constant(
                    QrGenerator.generarQrChecksumInvalido()
                )
            )

            .to("direct:sipap-in");


        from("timer:prueba-tlv-invalido?repeatCount=1&delay=18000")
            .routeId("prueba-tlv-invalido")

            .setHeader(
                "idTransaccion",
                simple("TX-INVALID-TLV-${date:now:HHmmssSSS}")
            )

            .setBody(
                constant(
                    QrGenerator.generarQrTlvInvalido()
                )
            )

            .to("direct:sipap-in");


        // ==========================================
        // MEDIADOR SIPAP
        // ==========================================
        from("direct:sipap-in")
            .routeId("mediador-sipap")

            .log(
                "Transferencia recibida: ${header.idTransaccion}"
            )

            // MESSAGE TRANSLATOR
            .process(exchange -> {

                String qr =
                    exchange.getMessage()
                        .getBody(String.class);

                String idTransaccion =
                    exchange.getMessage()
                        .getHeader(
                            "idTransaccion",
                            String.class
                        );

                Transferencia transferencia =
                    mapper.convertir(
                        qr,
                        idTransaccion
                    );

                exchange.getMessage()
                    .setBody(transferencia);
            })

            // VALIDACION
            .process(exchange -> {

                Transferencia transferencia =
                    exchange.getMessage()
                        .getBody(Transferencia.class);

                try {

                    validator.validar(transferencia);

                    exchange.getMessage()
                        .setHeader("valida", true);

                } catch (IllegalArgumentException e) {

                    exchange.getMessage()
                        .setHeader("valida", false);

                    exchange.getMessage()
                        .setHeader(
                            "motivoRechazo",
                            e.getMessage()
                        );
                }
            })

            // MESSAGE FILTER + CONTENT BASED ROUTER
            .choice()

                .when(
                    header("valida")
                        .isEqualTo(false)
                )

                    .to("direct:rechazados")

                .otherwise()

                    .choice()

                        .when(
                            simple(
                                "${body.merchantAccountInformation.codigoEntidad} == '0015'"
                            )
                        )
                            .to("direct:itau")

                        .when(
                            simple(
                                "${body.merchantAccountInformation.codigoEntidad} == '0007'"
                            )
                        )
                            .to("direct:atlas")

                        .when(
                            simple(
                                "${body.merchantAccountInformation.codigoEntidad} == '0020'"
                            )
                        )
                            .to("direct:familiar")

                        .otherwise()

                            .setHeader(
                                "motivoRechazo",
                                constant(
                                    "Banco destino no soportado"
                                )
                            )

                            .to("direct:rechazados")

                    .endChoice()

            .end();


        // ==========================================
        // CONSUMIDOR ITAU
        // ==========================================
        from("direct:itau")
            .routeId("consumidor-itau")

            .process(exchange ->
                procesarBanco(
                    exchange,
                    "ITAU"
                )
            );


        // ==========================================
        // CONSUMIDOR ATLAS
        // ==========================================
        from("direct:atlas")
            .routeId("consumidor-atlas")

            .process(exchange ->
                procesarBanco(
                    exchange,
                    "ATLAS"
                )
            );


        // ==========================================
        // CONSUMIDOR FAMILIAR
        // ==========================================
        from("direct:familiar")
            .routeId("consumidor-familiar")

            .process(exchange ->
                procesarBanco(
                    exchange,
                    "FAMILIAR"
                )
            );


        // ==========================================
        // RECHAZADOS
        // ==========================================
        from("direct:rechazados")
            .routeId("rechazados")

            .process(exchange -> {

                Transferencia transferencia =
                    exchange.getMessage()
                        .getBody(Transferencia.class);

                String motivo =
                    exchange.getMessage()
                        .getHeader(
                            "motivoRechazo",
                            String.class
                        );

                ResultadoTransferencia resultado =
                    new ResultadoTransferencia(
                        transferencia.getIdTransaccion(),
                        "RECHAZADA",
                        motivo
                    );

                exchange.getMessage()
                    .setBody(resultado);
            })

            .process(exchange ->
                imprimirResultado(exchange)
            );
    }


    private void procesarBanco(
            Exchange exchange,
            String banco) throws Exception {

        Transferencia transferencia =
            exchange.getMessage()
                .getBody(Transferencia.class);

        String json =
            objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(
                    transferencia
                );

        System.out.println();
        System.out.println(
            "===== CONSUMIDOR " + banco + " ====="
        );
        System.out.println(json);

        ResultadoTransferencia resultado =
            new ResultadoTransferencia(
                transferencia.getIdTransaccion(),
                "PROCESADA",
                "Transferencia procesada exitosamente por "
                    + banco
            );

        exchange.getMessage()
            .setBody(resultado);

        imprimirResultado(exchange);
    }


    private void imprimirResultado(
            Exchange exchange) throws Exception {

        ResultadoTransferencia resultado =
            exchange.getMessage()
                .getBody(
                    ResultadoTransferencia.class
                );

        String json =
            objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(
                    resultado
                );

        System.out.println();
        System.out.println(
            "===== RESULTADO ====="
        );
        System.out.println(json);
        System.out.println(
            "====================="
        );
    }
}