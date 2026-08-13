package py.ucom.sipap.routes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import py.ucom.sipap.generator.QrGenerator;

public class SipapRouteTest {

    private CamelContext context;
    private ProducerTemplate producer;

    @BeforeEach
    void preparar() throws Exception {

        context = new DefaultCamelContext();

        context.addRoutes(new SipapRoute());

        // ==========================================
        // DESACTIVAMOS LOS TIMER EN LOS TESTS
        // Reemplazandolos por endpoints direct:
        // ==========================================

        AdviceWith.adviceWith(
            context,
            "productor-itau",
            builder ->
                builder.replaceFromWith(
                    "direct:test-productor-itau"
                )
        );

        AdviceWith.adviceWith(
            context,
            "productor-atlas",
            builder ->
                builder.replaceFromWith(
                    "direct:test-productor-atlas"
                )
        );

        AdviceWith.adviceWith(
            context,
            "productor-familiar",
            builder ->
                builder.replaceFromWith(
                    "direct:test-productor-familiar"
                )
        );

        AdviceWith.adviceWith(
            context,
            "prueba-banco-desconocido",
            builder ->
                builder.replaceFromWith(
                    "direct:test-banco-desconocido"
                )
        );

        AdviceWith.adviceWith(
            context,
            "prueba-monto-invalido",
            builder ->
                builder.replaceFromWith(
                    "direct:test-monto-invalido"
                )
        );

        AdviceWith.adviceWith(
            context,
            "prueba-crc-invalido",
            builder ->
                builder.replaceFromWith(
                    "direct:test-crc-invalido"
                )
        );

        AdviceWith.adviceWith(
            context,
            "prueba-tlv-invalido",
            builder ->
                builder.replaceFromWith(
                    "direct:test-tlv-invalido"
                )
        );


        // ==========================================
        // INTERCEPTAMOS LOS CONSUMIDORES
        // ==========================================

        AdviceWith.adviceWith(
            context,
            "consumidor-itau",
            builder ->
                builder.weaveAddFirst()
                    .to("mock:itau")
        );

        AdviceWith.adviceWith(
            context,
            "consumidor-atlas",
            builder ->
                builder.weaveAddFirst()
                    .to("mock:atlas")
        );

        AdviceWith.adviceWith(
            context,
            "consumidor-familiar",
            builder ->
                builder.weaveAddFirst()
                    .to("mock:familiar")
        );

        AdviceWith.adviceWith(
            context,
            "rechazados",
            builder ->
                builder.weaveAddFirst()
                    .to("mock:rechazados")
        );


        // ==========================================
        // INICIAMOS CAMEL
        // ==========================================

        context.start();

        producer = context.createProducerTemplate();
    }


    @AfterEach
    void finalizar() throws Exception {

        if (producer != null) {
            producer.stop();
        }

        if (context != null) {
            context.stop();
        }
    }


    // ==========================================
    // TEST 1 - ITAU
    // ==========================================

    @Test
    void transferenciaValidaDebeLlegarAItau()
            throws Exception {

        MockEndpoint itau =
            context.getEndpoint(
                "mock:itau",
                MockEndpoint.class
            );

        MockEndpoint atlas =
            context.getEndpoint(
                "mock:atlas",
                MockEndpoint.class
            );

        MockEndpoint familiar =
            context.getEndpoint(
                "mock:familiar",
                MockEndpoint.class
            );

        MockEndpoint rechazados =
            context.getEndpoint(
                "mock:rechazados",
                MockEndpoint.class
            );

        itau.expectedMessageCount(1);
        atlas.expectedMessageCount(0);
        familiar.expectedMessageCount(0);
        rechazados.expectedMessageCount(0);

        producer.sendBodyAndHeader(
            "direct:sipap-in",
            QrGenerator.generarQrItau(),
            "idTransaccion",
            "TEST-ITAU"
        );

        MockEndpoint.assertIsSatisfied(context);
    }


    // ==========================================
    // TEST 2 - ATLAS
    // ==========================================

    @Test
    void transferenciaValidaDebeLlegarAAtlas()
            throws Exception {

        MockEndpoint itau =
            context.getEndpoint(
                "mock:itau",
                MockEndpoint.class
            );

        MockEndpoint atlas =
            context.getEndpoint(
                "mock:atlas",
                MockEndpoint.class
            );

        MockEndpoint familiar =
            context.getEndpoint(
                "mock:familiar",
                MockEndpoint.class
            );

        MockEndpoint rechazados =
            context.getEndpoint(
                "mock:rechazados",
                MockEndpoint.class
            );

        itau.expectedMessageCount(0);
        atlas.expectedMessageCount(1);
        familiar.expectedMessageCount(0);
        rechazados.expectedMessageCount(0);

        producer.sendBodyAndHeader(
            "direct:sipap-in",
            QrGenerator.generarQrAtlas(),
            "idTransaccion",
            "TEST-ATLAS"
        );

        MockEndpoint.assertIsSatisfied(context);
    }


    // ==========================================
    // TEST 3 - FAMILIAR
    // ==========================================

    @Test
    void transferenciaValidaDebeLlegarAFamiliar()
            throws Exception {

        MockEndpoint itau =
            context.getEndpoint(
                "mock:itau",
                MockEndpoint.class
            );

        MockEndpoint atlas =
            context.getEndpoint(
                "mock:atlas",
                MockEndpoint.class
            );

        MockEndpoint familiar =
            context.getEndpoint(
                "mock:familiar",
                MockEndpoint.class
            );

        MockEndpoint rechazados =
            context.getEndpoint(
                "mock:rechazados",
                MockEndpoint.class
            );

        itau.expectedMessageCount(0);
        atlas.expectedMessageCount(0);
        familiar.expectedMessageCount(1);
        rechazados.expectedMessageCount(0);

        producer.sendBodyAndHeader(
            "direct:sipap-in",
            QrGenerator.generarQrFamiliar(),
            "idTransaccion",
            "TEST-FAMILIAR"
        );

        MockEndpoint.assertIsSatisfied(context);
    }


    // ==========================================
    // TEST 4 - BANCO DESCONOCIDO
    // ==========================================

    @Test
    void bancoDesconocidoDebeSerRechazado()
            throws Exception {

        MockEndpoint itau =
            context.getEndpoint(
                "mock:itau",
                MockEndpoint.class
            );

        MockEndpoint atlas =
            context.getEndpoint(
                "mock:atlas",
                MockEndpoint.class
            );

        MockEndpoint familiar =
            context.getEndpoint(
                "mock:familiar",
                MockEndpoint.class
            );

        MockEndpoint rechazados =
            context.getEndpoint(
                "mock:rechazados",
                MockEndpoint.class
            );

        itau.expectedMessageCount(0);
        atlas.expectedMessageCount(0);
        familiar.expectedMessageCount(0);
        rechazados.expectedMessageCount(1);

        producer.sendBodyAndHeader(
            "direct:sipap-in",
            QrGenerator.generarQrBancoDesconocido(),
            "idTransaccion",
            "TEST-BANCO-INVALIDO"
        );

        MockEndpoint.assertIsSatisfied(context);

        assertEquals(
            "Banco destino desconocido: 9999",
            rechazados.getExchanges()
                .get(0)
                .getMessage()
                .getHeader(
                    "motivoRechazo",
                    String.class
                )
        );
    }


    // ==========================================
    // TEST 5 - MONTO INVALIDO
    // ==========================================

    @Test
    void montoInvalidoDebeSerRechazado()
            throws Exception {

        MockEndpoint itau =
            context.getEndpoint(
                "mock:itau",
                MockEndpoint.class
            );

        MockEndpoint atlas =
            context.getEndpoint(
                "mock:atlas",
                MockEndpoint.class
            );

        MockEndpoint familiar =
            context.getEndpoint(
                "mock:familiar",
                MockEndpoint.class
            );

        MockEndpoint rechazados =
            context.getEndpoint(
                "mock:rechazados",
                MockEndpoint.class
            );

        itau.expectedMessageCount(0);
        atlas.expectedMessageCount(0);
        familiar.expectedMessageCount(0);
        rechazados.expectedMessageCount(1);

        producer.sendBodyAndHeader(
            "direct:sipap-in",
            QrGenerator.generarQrMontoInvalido(),
            "idTransaccion",
            "TEST-MONTO-INVALIDO"
        );

        MockEndpoint.assertIsSatisfied(context);

        assertEquals(
            "El monto supera o iguala el maximo permitido",
            rechazados.getExchanges()
                .get(0)
                .getMessage()
                .getHeader(
                    "motivoRechazo",
                    String.class
                )
        );
    }


    // ==========================================
    // TEST 6 - CRC INVALIDO
    // ==========================================

    @Test
    void crcInvalidoDebeSerRechazado()
            throws Exception {

        MockEndpoint itau =
            context.getEndpoint(
                "mock:itau",
                MockEndpoint.class
            );

        MockEndpoint atlas =
            context.getEndpoint(
                "mock:atlas",
                MockEndpoint.class
            );

        MockEndpoint familiar =
            context.getEndpoint(
                "mock:familiar",
                MockEndpoint.class
            );

        MockEndpoint rechazados =
            context.getEndpoint(
                "mock:rechazados",
                MockEndpoint.class
            );

        itau.expectedMessageCount(0);
        atlas.expectedMessageCount(0);
        familiar.expectedMessageCount(0);
        rechazados.expectedMessageCount(1);

        producer.sendBodyAndHeader(
            "direct:sipap-in",
            QrGenerator.generarQrChecksumInvalido(),
            "idTransaccion",
            "TEST-CRC-INVALIDO"
        );

        MockEndpoint.assertIsSatisfied(context);

        assertEquals(
            "Checksum invalido",
            rechazados.getExchanges()
                .get(0)
                .getMessage()
                .getHeader(
                    "motivoRechazo",
                    String.class
                )
        );
    }
    // ==========================================
// TEST 7 - TLV INVALIDO
// ==========================================

@Test
void tlvInvalidoDebeSerRechazado()
        throws Exception {

    MockEndpoint itau =
        context.getEndpoint(
            "mock:itau",
            MockEndpoint.class
        );

    MockEndpoint atlas =
        context.getEndpoint(
            "mock:atlas",
            MockEndpoint.class
        );

    MockEndpoint familiar =
        context.getEndpoint(
            "mock:familiar",
            MockEndpoint.class
        );

    itau.expectedMessageCount(0);
    atlas.expectedMessageCount(0);
    familiar.expectedMessageCount(0);

    producer.sendBodyAndHeader(
        "direct:sipap-in",
        QrGenerator.generarQrTlvInvalido(),
        "idTransaccion",
        "TEST-TLV-INVALIDO"
    );

    MockEndpoint.assertIsSatisfied(context);
    }
}