package com.loja.crud;

import com.loja.crud.controller.EnderecoController;
import com.loja.crud.controller.ProdutoController;
import com.loja.crud.controller.UsuarioController;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws IOException {
        int porta = porta();
        HttpServer server = HttpServer.create(new InetSocketAddress(porta), 0);

        ProdutoController.registrar(server);
        UsuarioController.registrar(server);
        EnderecoController.registrar(server);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();

        System.out.println("API disponivel em http://localhost:" + porta + "/api");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(0)));
    }

    private static int porta() {
        String valor = System.getProperty("PORT");

        if (valor == null || valor.isBlank()) {
            valor = System.getenv("PORT");
        }

        return valor == null || valor.isBlank() ? 8080 : Integer.parseInt(valor);
    }
}
