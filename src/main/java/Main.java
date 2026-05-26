import java.util.Scanner;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);

        Usuario usuario = null;
        Funcionario funcionario = null;
        Livro livro = null;
        Emprestimo emprestimo = null;
        Reserva reserva = null;

        int opcao;

        do {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1 - Cadastrar Usuário");
            System.out.println("2 - Cadastrar Funcionário");
            System.out.println("3 - Cadastrar Livro");
            System.out.println("4 - Criar Empréstimo");
            System.out.println("5 - Criar Reserva");
            System.out.println("6 - Visualizar Dados");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            opcao = leitor.nextInt();
            leitor.nextLine(); // consumir quebra de linha

            switch (opcao) {
                case 1:
                    System.out.println("Nome do usuário:");
                    String nomeU = leitor.nextLine();
                    System.out.println("CPF:");
                    String cpfU = leitor.nextLine();
                    System.out.println("Email:");
                    String emailU = leitor.nextLine();
                    System.out.println("Matrícula:");
                    String matricula = leitor.nextLine();
                    System.out.println("Tipo (Aluno/Professor):");
                    String tipo = leitor.nextLine();

                    usuario = new Usuario(1, nomeU, cpfU, emailU, matricula, tipo);
                    System.out.println("Usuário cadastrado!");
                    break;

                case 2:
                    System.out.println("Nome do funcionário:");
                    String nomeF = leitor.nextLine();
                    System.out.println("CPF:");
                    String cpfF = leitor.nextLine();
                    System.out.println("Email:");
                    String emailF = leitor.nextLine();
                    System.out.println("Cargo:");
                    String cargo = leitor.nextLine();

                    funcionario = new Funcionario(2, nomeF, cpfF, emailF, cargo);
                    System.out.println("Funcionário cadastrado!");
                    break;

                case 3:
                    System.out.println("Título do livro:");
                    String titulo = leitor.nextLine();
                    System.out.println("Autor:");
                    String autor = leitor.nextLine();

                    livro = new Livro(101, titulo, autor);
                    livro.alterarDisponibilidade(true);

                    System.out.println("Livro cadastrado!");
                    break;

                case 4:
                    if (usuario == null || livro == null) {
                        System.out.println("É necessário cadastrar usuário e livro antes!");
                    } else {
                        emprestimo = new Emprestimo();
                        emprestimo.setId(301);
                        emprestimo.setStatus("Em andamento");
                        emprestimo.setDataEmprestimo(new Date());
                        System.out.println("Empréstimo criado com sucesso!");
                    }
                    break;

                case 5:
                    if (usuario == null || livro == null) {
                        System.out.println("É necessário cadastrar usuário e livro antes!");
                    } else {
                        reserva = new Reserva();
                        reserva.setId(201);
                        reserva.setStatus("Ativa");
                        reserva.setDataReserva(new Date());
                        System.out.println("Reserva criada com sucesso!");
                    }
                    break;

                case 6:
                    System.out.println("\n--- DADOS CADASTRADOS ---");
                    if (usuario != null) usuario.exibirPessoa();
                    if (funcionario != null) funcionario.exibirFuncionario();
                    if (livro != null) livro.exibirLivros();
                    if (emprestimo != null) System.out.println("Empréstimo Status: " + emprestimo.getStatus());
                    if (reserva != null) System.out.println("Reserva Status: " + reserva.getStatus());
                    break;

                case 0:
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção inválida!");
                    break;
            }

        } while (opcao != 0);

        leitor.close();
    }
}
