package com.loja.crud.view;

import com.loja.crud.exception.DataAccessException;
import com.loja.crud.entity.Produto;
import com.loja.crud.service.ProdutoService;

import java.math.BigDecimal;
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

            try {
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
            } catch (DataAccessException e) {
                System.out.println("Erro ao acessar o banco de dados.");
            }
        }
    }

    private void criar() {
        DadosProduto dados = lerDadosProduto();

        if (service.cadastrar(dados.nome, dados.preco, dados.quantidade)) {
            System.out.println("Produto cadastrado!");
        } else {
            System.out.println("Nao foi possivel cadastrar o produto.");
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
                    + " - R$ " + String.format("%.2f", produto.getPreco())
                    + " - " + produto.getQuantidade() + " un");
        }
    }

    private void atualizar() {
        int id = lerId();
        DadosProduto dados = lerDadosProduto();

        if (service.atualizar(id, dados.nome, dados.preco, dados.quantidade)) {
            System.out.println("Produto atualizado!");
        } else {
            System.out.println("Nao foi possivel atualizar o produto.");
        }
    }

    private void deletar() {
        int id = lerId();

        if (service.deletar(id)) {
            System.out.println("Produto deletado!");
        } else {
            System.out.println("Nao foi possivel deletar o produto.");
        }
    }

    private DadosProduto lerDadosProduto() {
        String nome = lerNome();
        BigDecimal preco = lerPreco();
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

    private BigDecimal lerPreco() {
        while (true) {
            BigDecimal preco = lerDecimal("Defina o preco:");

            if (preco.compareTo(BigDecimal.ZERO) > 0) {
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

    private BigDecimal lerDecimal(String mensagem) {
        while (true) {
            System.out.println(mensagem);

            try {
                return new BigDecimal(scanner.nextLine().trim().replace(",", "."));
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
        private final BigDecimal preco;
        private final int quantidade;

        private DadosProduto(String nome, BigDecimal preco, int quantidade) {
            this.nome = nome;
            this.preco = preco;
            this.quantidade = quantidade;
        }
    }
}
