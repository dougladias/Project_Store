package com.loja.crud;

import com.loja.crud.view.ProdutoConsole;
import com.loja.crud.view.UsuarioConsole;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ProdutoConsole produtoConsole = new ProdutoConsole(scanner);
        UsuarioConsole usuarioConsole = new UsuarioConsole(scanner);

        int opcao = -1;

        while (opcao != 0) {
            imprimirMenu();
            opcao = lerOpcao(scanner);

            switch (opcao) {
                case 1:
                    produtoConsole.abrir();
                    break;
                case 2:
                    usuarioConsole.abrir();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Digite um valor valido");
                    break;
            }
        }

        scanner.close();
    }

    private static void imprimirMenu() {
        System.out.println("=== SISTEMA DE LOJA ===");
        System.out.println("1 - Gerenciar Produtos");
        System.out.println("2 - Gerenciar Usuarios");
        System.out.println("0 - Sair");
    }

    private static int lerOpcao(Scanner scanner) {
        try {
            System.out.print("Opcao: ");
            int opcao = scanner.nextInt();
            scanner.nextLine();
            return opcao;
        } catch (InputMismatchException e) {
            scanner.nextLine();
            return -1;
        }
    }
}
