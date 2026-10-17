package com.loja.crud;

import com.loja.crud.model.Produto;
import com.loja.crud.service.ProdutoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

// Menu do sistema
public class Main {

    public static void main(String[] args) {
        HashMap<String, Object> dados = new HashMap<>();
        ArrayList<String> campos = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        ProdutoService service = new ProdutoService();
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("Escolha uma opcao");
            System.out.println("1 - Criar Produto");
            System.out.println("2 - Listar Produtos");
            System.out.println("3 - Atualizar um Produto");
            System.out.println("4 - Deletar um produto");
            System.out.println("0 - Sair");

            try {
                System.out.print("Opcao: ");
                opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                        System.out.println("Opcao " + opcao);
                        dados.clear();
                        campos.clear();
                        campos.add("nome");
                        campos.add("preco");
                        campos.add("quantidade");

                        for (String campo : campos) {
                            System.out.println("Defina o valor de " + campo);
                            String linha = scanner.nextLine();

                            if (campo.equals("preco")) {
                                dados.put(campo, Double.parseDouble(linha.replace(",", ".")));
                            } else if (campo.equals("quantidade")) {
                                dados.put(campo, Integer.parseInt(linha));
                            } else {
                                dados.put(campo, linha);
                            }
                        }

                        if (service.cadastrar((String) dados.get("nome"), (Double) dados.get("preco"), (Integer) dados.get("quantidade"))) {
                            System.out.println("Produto cadastrado!");
                        } else {
                            System.out.println("Dados invalidos.");
                        }
                        break;
                    case 2:
                        System.out.println("Opcao " + opcao);
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
                        break;
                    case 3:
                        System.out.println("Opcao " + opcao);
                        System.out.print("Id do produto: ");
                        int id = scanner.nextInt();
                        scanner.nextLine();
                        dados.clear();
                        campos.clear();
                        campos.add("nome");
                        campos.add("preco");
                        campos.add("quantidade");

                        for (String campo : campos) {
                            System.out.println("Defina o valor de " + campo);
                            String linha = scanner.nextLine();

                            if (campo.equals("preco")) {
                                dados.put(campo, Double.parseDouble(linha.replace(",", ".")));
                            } else if (campo.equals("quantidade")) {
                                dados.put(campo, Integer.parseInt(linha));
                            } else {
                                dados.put(campo, linha);
                            }
                        }

                        if (service.atualizar(id, (String) dados.get("nome"), (Double) dados.get("preco"), (Integer) dados.get("quantidade"))) {
                            System.out.println("Produto atualizado!");
                        } else {
                            System.out.println("Dados invalidos.");
                        }
                        break;
                    case 4:
                        System.out.println("Opcao " + opcao);
                        System.out.print("Id do produto: ");
                        int idDeletar = scanner.nextInt();
                        scanner.nextLine();
                        service.deletar(idDeletar);
                        System.out.println("Produto deletado!");
                        break;
                    case 0:
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Digite um valor valido");
                        break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                if (e instanceof InputMismatchException) {
                    scanner.nextLine();
                }
            }
        }

        scanner.close();
    }
}
