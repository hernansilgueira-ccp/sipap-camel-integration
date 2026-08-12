package py.ucom.sipap;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import py.ucom.sipap.routes.SipapRoute;

public class App {

    public static void main(String[] args) throws Exception {

        CamelContext context =
            new DefaultCamelContext();

        context.addRoutes(
            new SipapRoute()
        );

        context.start();

        System.out.println(
            "=========================================="
        );

        System.out.println(
            " Mediador SIPAP Apache Camel iniciado"
        );

        System.out.println(
            " Presiona CTRL+C para finalizar"
        );

        System.out.println(
            "=========================================="
        );

        Thread.currentThread().join();
    }
}