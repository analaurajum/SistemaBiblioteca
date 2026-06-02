import java.util.ArrayList;
import java.util.Scanner;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);

        ArrayList<Usuario> usuarios = new ArrayList<>();
        ArrayList<Funcionario> funcionarios = new ArrayList<>();
        ArrayList<Livro> livros = new ArrayList<>();
        ArrayList<Emprestimo> emprestimos = new ArrayList<>();
        ArrayList<Reserva> reservas = new ArrayList<>();

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

            try {
                opcao = leitor.nextInt();
                leitor.nextLine();
            } catch (Exception e) {
                System.out.println("Entrada inválida! Digite um número.");
                leitor.nextLine();
                opcao = -1;
                continue;
            }
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

                    Usuario usuario = new Usuario(usuarios.size()+1, nomeU, cpfU, emailU, matricula, tipo);
                    usuarios.add(usuario);
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

                    Funcionario funcionario = new Funcionario(funcionarios.size()+1, nomeF, cpfF, emailF, cargo);
                    funcionarios.add(funcionario);
                    System.out.println("Funcionário cadastrado!");
                    break;

                case 3:
                    System.out.println("Título do livro:");
                    String titulo = leitor.nextLine();
                    System.out.println("Autor:");
                    String autor = leitor.nextLine();

                    Livro livro = new Livro(livros.size() + 100, titulo, autor);
                    livro.alterarDisponibilidade(true);
                    livros.add(livro);

                    System.out.println("Livro cadastrado!");
                    break;

                case 4:
                    if (usuarios.isEmpty() || livros.isEmpty()) {
                        System.out.println("É necessário cadastrar usuário e livro antes!");
                    } else {
                        Emprestimo emprestimo = new Emprestimo();
                        emprestimo.setId(emprestimos.size() +300);
                        emprestimo.setStatus("Em andamento");
                        emprestimo.setDataEmprestimo(new Date());
                        emprestimos.add(emprestimo);
                        System.out.println("Empréstimo criado com sucesso!");
                    }
                    break;

                case 5:
                    if (usuarios.isEmpty()|| livros.isEmpty()) {
                        System.out.println("É necessário cadastrar usuário e livro antes!");
                    } else {
                        Reserva reserva = new Reserva();
                        reserva.setId(reservas.size()+ 200);
                        reserva.setStatus("Ativa");
                        reserva.setDataReserva(new Date());
                        reservas.add(reserva);
                        System.out.println("Reserva criada com sucesso!");
                    }
                    break;

                case 6:
                    System.out.println("\n--- DADOS CADASTRADOS ---");
                    for (Usuario u : usuarios) u.exibirPessoa();
                    for (Funcionario f : funcionarios) f.exibirFuncionario();
                    for (Livro l : livros) l.exibirLivros();
                    for (Emprestimo e : emprestimos) System.out.println("Empréstimo Status: " + e.getStatus());
                    for (Reserva r : reservas) System.out.println("Reserva Status: " + r.getStatus());
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
