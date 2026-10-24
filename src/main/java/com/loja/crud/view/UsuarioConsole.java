package com.loja.crud.view;

import com.loja.crud.exception.DataAccessException;
import com.loja.crud.entity.Usuario;
import com.loja.crud.entity.Usuario.Cpf;
import com.loja.crud.entity.Usuario.Email;
import com.loja.crud.entity.Usuario.Endereco;
import com.loja.crud.entity.Usuario.Telefone;
import com.loja.crud.service.UsuarioService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class UsuarioConsole {

    private static final DateTimeFormatter FORMATO_EXIBICAO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_ENTRADA_DATA = DateTimeFormatter.ofPattern("ddMMuuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private final Scanner scanner;
    private final UsuarioService service = new UsuarioService();

    public UsuarioConsole(Scanner scanner) {
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
        Usuario usuario = lerUsuario();

        if (service.cadastrar(usuario)) {
            System.out.println("Usuario cadastrado!");
        } else {
            System.out.println("Nao foi possivel cadastrar o usuario.");
        }
    }

    private void listar() {
        List<Usuario> usuarios = service.listar();

        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuario cadastrado.");
            return;
        }

        for (Usuario usuario : usuarios) {
            imprimirUsuario(usuario);
        }
    }

    private void atualizar() {
        int id = lerId();
        Usuario usuario = lerUsuario(id);

        if (service.atualizar(usuario)) {
            System.out.println("Usuario atualizado!");
        } else {
            System.out.println("Nao foi possivel atualizar o usuario.");
        }
    }

    private void deletar() {
        int id = lerId();

        System.out.print("Confirma a exclusao? (S/N): ");
        String resposta = scanner.nextLine().trim();

        if (!resposta.equalsIgnoreCase("S")) {
            System.out.println("Operacao cancelada.");
            return;
        }

        if (service.deletar(id)) {
            System.out.println("Usuario deletado!");
        } else {
            System.out.println("Nao foi possivel deletar o usuario.");
        }
    }

    private Usuario lerUsuario() {
        return lerUsuario(0);
    }

    private Usuario lerUsuario(int id) {
        String nome = lerNome();
        Cpf cpf = lerCpf();
        Email email = lerEmail();
        Telefone telefone = lerTelefone();
        LocalDate dataNascimento = lerDataNascimento();
        Endereco endereco = lerEndereco();

        return new Usuario(id, nome, cpf, email, telefone, dataNascimento, endereco);
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

    private Cpf lerCpf() {
        while (true) {
            System.out.println("Defina o CPF:");

            try {
                return new Cpf(scanner.nextLine());
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + ". Tente novamente.");
            }
        }
    }

    private Email lerEmail() {
        while (true) {
            System.out.println("Defina o email:");

            try {
                return new Email(scanner.nextLine());
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + ". Tente novamente.");
            }
        }
    }

    private Telefone lerTelefone() {
        while (true) {
            System.out.println("Defina o telefone:");

            try {
                return new Telefone(scanner.nextLine());
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + ". Tente novamente.");
            }
        }
    }

    private LocalDate lerDataNascimento() {
        while (true) {
            System.out.println("Defina a data de nascimento (ddmmaaaa):");
            String valor = scanner.nextLine().replaceAll("\\D", "");

            try {
                LocalDate dataNascimento = LocalDate.parse(valor, FORMATO_ENTRADA_DATA);

                if (dataNascimento.isAfter(LocalDate.now())) {
                    System.out.println("Data de nascimento invalida. Tente novamente.");
                    continue;
                }

                return dataNascimento;
            } catch (DateTimeParseException e) {
                System.out.println("Data invalida. Informe a data no formato ddmmaaaa.");
            }
        }
    }

    private Endereco lerEndereco() {
        String rua = lerTextoObrigatorio("Defina a rua:");
        String bairro = lerTextoObrigatorio("Defina o bairro:");
        String numero = lerTextoObrigatorio("Defina o numero:");

        while (true) {
            System.out.println("Defina o CEP:");

            try {
                return new Endereco(rua, bairro, numero, scanner.nextLine());
            } catch (IllegalArgumentException e) {
                System.out.println("CEP invalido. Tente novamente.");
            }
        }
    }

    private String lerTextoObrigatorio(String mensagem) {
        while (true) {
            System.out.println(mensagem);
            String texto = scanner.nextLine().trim();

            if (!texto.isEmpty()) {
                return texto;
            }

            System.out.println("Valor invalido. Tente novamente.");
        }
    }

    private int lerId() {
        while (true) {
            System.out.print("Id do usuario: ");

            try {
                int id = scanner.nextInt();
                scanner.nextLine();

                if (id > 0) {
                    return id;
                }
            } catch (InputMismatchException e) {
                scanner.nextLine();
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
        System.out.println("=== GERENCIAR USUARIOS ===");
        System.out.println("1 - Criar Usuario");
        System.out.println("2 - Listar Usuarios");
        System.out.println("3 - Atualizar Usuario");
        System.out.println("4 - Deletar Usuario");
        System.out.println("0 - Voltar");
    }

    private void imprimirUsuario(Usuario usuario) {
        Cpf cpf = usuario.getCpf();
        Email email = usuario.getEmail();
        Telefone telefone = usuario.getTelefone();
        Endereco endereco = usuario.getEndereco();
        LocalDate dataNascimento = usuario.getDataNascimento();

        System.out.println(usuario.getId() + " - " + usuario.getNome()
                + " - CPF: " + cpf.getDigitos()
                + " - Email: " + email.getValor()
                + " - Telefone: " + telefone.getDigitos()
                + " - Nascimento: " + dataNascimento.format(FORMATO_EXIBICAO_DATA)
                + " - Endereco: " + endereco.getRua() + ", " + endereco.getNumero()
                + " - " + endereco.getBairro()
                + " - CEP: " + endereco.getCEP());
    }
}
