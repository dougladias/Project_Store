package com.loja.crud.view;

import com.loja.crud.model.Produto;
import com.loja.crud.service.ProdutoService;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ProdutoConsole {

    private final Scanner scanner;
    private final ProdutoService service = new ProdutoService();

    public ProdutoConsole(Scanner scanner) {
        this.scanner = scanner;
    }

    public void abrir() {
        int opcao = -1;

        while (opcao != 0) {
            imprimirMenu();
            opcao = lerOpcao();

            switch (opcao) {
                case 1:
                    criar();
                    break;
                case 2:
                    listar();
                    break;
                case 3:
                    atualizar();
                    break;
                case 4:
                    deletar();
                    break;
                case 0:
                    System.out.println("Voltando...");
                    break;
                default:
                    System.out.println("Digite um valor valido");
                    break;
            }
        }
    }

    private void criar() {
        DadosProduto dados = lerDadosProduto();

        if (service.cadastrar(dados.nome, dados.preco, dados.quantidade)) {
            System.out.println("Produto cadastrado!");
        } else {
            System.out.println("Dados invalidos.");
        }
    }

    private void listar() {
        List<Produto> produtos = service.listar();

        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }

        for (Produto produto : produtos) {
            System.out.println(produto.getId() + " - " + produto.getNome()
                    + " - R$ " + produto.getPreco()
                    + " - " + produto.getQuantidade() + " un");
        }
    }

    private void atualizar() {
        int id = lerId();
        DadosProduto dados = lerDadosProduto();

        if (service.atualizar(id, dados.nome, dados.preco, dados.quantidade)) {
            System.out.println("Produto atualizado!");
        } else {
            System.out.println("Dados invalidos.");
        }
    }

    private void deletar() {
        int id = lerId();
        service.deletar(id);
        System.out.println("Produto deletado!");
    }

    private DadosProduto lerDadosProduto() {
        String nome = lerNome();
        double preco = lerPreco();
        int quantidade = lerQuantidade();

        return new DadosProduto(nome, preco, quantidade);
    }

    private String lerNome() {
        while (true) {
            System.out.println("Defina o nome:");
            String nome = scanner.nextLine().trim();

            if (!nome.isEmpty()) {
                return nome;
            }

            System.out.println("Nome invalido. Tente novamente.");
        }
    }

    private double lerPreco() {
        while (true) {
            double preco = lerDecimal("Defina o preco:");

            if (Double.isFinite(preco) && preco > 0) {
                return preco;
            }

            System.out.println("Preco invalido. Tente novamente.");
        }
    }

    private int lerQuantidade() {
        while (true) {
            int quantidade = lerInteiro("Defina a quantidade:");

            if (quantidade >= 0) {
                return quantidade;
            }

            System.out.println("Quantidade invalida. Tente novamente.");
        }
    }

    private double lerDecimal(String mensagem) {
        while (true) {
            System.out.println(mensagem);

            try {
                return Double.parseDouble(scanner.nextLine().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido. Tente novamente.");
            }
        }
    }

    private int lerInteiro(String mensagem) {
        while (true) {
            System.out.println(mensagem);

            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Valor invalido. Tente novamente.");
            }
        }
    }

    private int lerId() {
        while (true) {
            int id = lerInteiro("Id do produto:");

            if (id > 0) {
                return id;
            }

            System.out.println("Id invalido. Tente novamente.");
        }
    }

    private int lerOpcao() {
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

    private void imprimirMenu() {
        System.out.println("=== GERENCIAR PRODUTOS ===");
        System.out.println("1 - Criar Produto");
        System.out.println("2 - Listar Produtos");
        System.out.println("3 - Atualizar Produto");
        System.out.println("4 - Deletar Produto");
        System.out.println("0 - Voltar");
    }

    private static class DadosProduto {

        private final String nome;
        private final double preco;
        private final int quantidade;

        private DadosProduto(String nome, double preco, int quantidade) {
            this.nome = nome;
            this.preco = preco;
            this.quantidade = quantidade;
        }
    }
}
