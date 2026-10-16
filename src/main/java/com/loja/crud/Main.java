package com.loja.crud;

import com.loja.crud.model.Produto;
import com.loja.crud.service.ProdutoService;

import java.util.List;
import java.util.Scanner;

// Menu do sistema
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ProdutoService service = new ProdutoService();
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n=== LOJA ===");
            System.out.println("1 - Cadastrar produto");
            System.out.println("2 - Listar produtos");
            System.out.println("3 - Atualizar produto");
            System.out.println("4 - Deletar produto");
            System.out.println("0 - Sair");
            System.out.print("Opcao: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            if (opcao == 1) {
                System.out.print("Nome: ");
                String nome = scanner.nextLine();
                System.out.print("Preco: ");
                double preco = Double.parseDouble(scanner.nextLine().replace(",", "."));
                System.out.print("Quantidade: ");
                int quantidade = scanner.nextInt();
                scanner.nextLine();

                if (service.cadastrar(nome, preco, quantidade)) {
                    System.out.println("Produto cadastrado!");
                } else {
                    System.out.println("Dados invalidos.");
                }

            } else if (opcao == 2) {
                List<Produto> produtos = service.listar();
                if (produtos.isEmpty()) {
                    System.out.println("Nenhum produto cadastrado.");
                } else {
                    for (Produto p : produtos) {
                        System.out.println(p.getId() + " - " + p.getNome()
                                + " - R$ " + p.getPreco()
                                + " - " + p.getQuantidade() + " un");
                    }
                }

            } else if (opcao == 3) {
                System.out.print("Id do produto: ");
                int id = scanner.nextInt();
                scanner.nextLine();
                System.out.print("Novo nome: ");
                String nome = scanner.nextLine();
                System.out.print("Novo preco: ");
                double preco = Double.parseDouble(scanner.nextLine().replace(",", "."));
                System.out.print("Nova quantidade: ");
                int quantidade = scanner.nextInt();
                scanner.nextLine();

                if (service.atualizar(id, nome, preco, quantidade)) {
                    System.out.println("Produto atualizado!");
                } else {
                    System.out.println("Dados invalidos.");
                }

            } else if (opcao == 4) {
                System.out.print("Id do produto: ");
                int id = scanner.nextInt();
                scanner.nextLine();
                service.deletar(id);
                System.out.println("Produto deletado!");

            } else if (opcao == 0) {
                System.out.println("Saindo...");
            } else {
                System.out.println("Opcao invalida.");
            }
        }

        scanner.close();
    }
}
