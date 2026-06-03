package view;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;

public class MenuGUI {
    public static void main(String[] args) {
        // Listas
        ArrayList<Usuario> usuarios = new ArrayList<>();
        ArrayList<Funcionario> funcionarios = new ArrayList<>();
        ArrayList<Livro> livros = new ArrayList<>();
        ArrayList<Emprestimo> emprestimos = new ArrayList<>();
        ArrayList<Reserva> reservas = new ArrayList<>();
        ArrayList<Categoria> categorias = new ArrayList<>();

        // Janela principal
        JFrame janela = new JFrame("Sistema Biblioteca");
        janela.setSize(700, 500);
        janela.setLocationRelativeTo(null);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Abas
        JTabbedPane abas = new JTabbedPane();

        // Painel Usuário
        JPanel painelUsuario = new JPanel(new GridLayout(6, 2, 10, 10));
        JTextField txt_nomeU = new JTextField();
        JTextField txt_cpfU = new JTextField();
        JTextField txt_emailU = new JTextField();
        JTextField txt_matricula = new JTextField();
        JTextField txt_tipo = new JTextField();
        JButton btn_cadastrarUsuario = new JButton("Cadastrar Usuário");
        btn_cadastrarUsuario.setPreferredSize(new Dimension(250, 60));

        painelUsuario.add(new JLabel("Nome:"));
        painelUsuario.add(txt_nomeU);
        painelUsuario.add(new JLabel("CPF:"));
        painelUsuario.add(txt_cpfU);
        painelUsuario.add(new JLabel("Email:"));
        painelUsuario.add(txt_emailU);
        painelUsuario.add(new JLabel("Matrícula:"));
        painelUsuario.add(txt_matricula);
        painelUsuario.add(new JLabel("Tipo:"));
        painelUsuario.add(txt_tipo);
        painelUsuario.add(btn_cadastrarUsuario);
        painelUsuario.add(new JLabel(""));

        btn_cadastrarUsuario.addActionListener(e -> {
            String nome = txt_nomeU.getText().trim();
            String cpf = txt_cpfU.getText().trim();
            String email = txt_emailU.getText().trim();
            String matricula = txt_matricula.getText().trim();
            String tipo = txt_tipo.getText().trim();

            if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty() || matricula.isEmpty() || tipo.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos!");
                return;
            }
            if (!cpf.matches("\\d{11}")) {
                JOptionPane.showMessageDialog(null, "CPF inválido! Digite 11 números.");
                return;
            }
            if (!email.contains("@")) {
                JOptionPane.showMessageDialog(null, "Email inválido!");
                return;
            }

            Usuario u = new Usuario(usuarios.size() + 1, nome, cpf, email, matricula, tipo);
            usuarios.add(u);
            JOptionPane.showMessageDialog(null, "Usuário cadastrado!");
        });

        // Painel Funcionário
        JPanel painelFuncionario = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField txt_nomeF = new JTextField();
        JTextField txt_cpfF = new JTextField();
        JTextField txt_emailF = new JTextField();
        JTextField txt_cargo = new JTextField();
        JButton btn_cadastrarFuncionario = new JButton("Cadastrar Funcionário");
        btn_cadastrarFuncionario.setPreferredSize(new Dimension(250, 60));

        painelFuncionario.add(new JLabel("Nome:"));
        painelFuncionario.add(txt_nomeF);
        painelFuncionario.add(new JLabel("CPF:"));
        painelFuncionario.add(txt_cpfF);
        painelFuncionario.add(new JLabel("Email:"));
        painelFuncionario.add(txt_emailF);
        painelFuncionario.add(new JLabel("Cargo:"));
        painelFuncionario.add(txt_cargo);
        painelFuncionario.add(btn_cadastrarFuncionario);
        painelFuncionario.add(new JLabel(""));

        btn_cadastrarFuncionario.addActionListener(e -> {
            String nome = txt_nomeF.getText().trim();
            String cpf = txt_cpfF.getText().trim();
            String email = txt_emailF.getText().trim();
            String cargo = txt_cargo.getText().trim();

            if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty() || cargo.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos!");
                return;
            }
            if (!cpf.matches("\\d{11}")) {
                JOptionPane.showMessageDialog(null, "CPF inválido!");
                return;
            }

            Funcionario f = new Funcionario(funcionarios.size() + 1, nome, cpf, email, cargo);
            funcionarios.add(f);
            JOptionPane.showMessageDialog(null, "Funcionário cadastrado!");
        });

        // Painel Livro
        JPanel painelLivro = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField txt_titulo = new JTextField();
        JTextField txt_autor = new JTextField();
        JTextField txt_categoria = new JTextField();
        JButton btn_cadastrarLivro = new JButton("Cadastrar Livro");
        btn_cadastrarLivro.setPreferredSize(new Dimension(250, 60));

        painelLivro.add(new JLabel("Título:"));
        painelLivro.add(txt_titulo);
        painelLivro.add(new JLabel("Autor:"));
        painelLivro.add(txt_autor);
        painelLivro.add(new JLabel("Categoria:"));
        painelLivro.add(txt_categoria);
        painelLivro.add(btn_cadastrarLivro);
        painelLivro.add(new JLabel(""));

        btn_cadastrarLivro.addActionListener(e -> {
            String titulo = txt_titulo.getText().trim();
            String autor = txt_autor.getText().trim();
            String categoriaNome = txt_categoria.getText().trim();

            if (titulo.isEmpty() || autor.isEmpty() || categoriaNome.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos!");
                return;
            }

            Categoria c = new Categoria();
            c.setId(categorias.size() + 500);
            c.setNome(categoriaNome);
            categorias.add(c);

            Livro l = new Livro(livros.size() + 100, titulo, autor);
            l.alterarDisponibilidade(true);
            livros.add(l);
            JOptionPane.showMessageDialog(null, "Livro cadastrado!");
        });

        // Painel Reserva
        JPanel painelReserva = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btn_reservar = new JButton("Criar Reserva");
        JButton btn_encerrarReserva = new JButton("Encerrar Reserva");
        btn_reservar.setPreferredSize(new Dimension(250, 60));
        btn_encerrarReserva.setPreferredSize(new Dimension(250, 60));
        painelReserva.add(btn_reservar);
        painelReserva.add(btn_encerrarReserva);

        btn_reservar.addActionListener(e -> {
            if (usuarios.isEmpty() || livros.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Cadastre usuário e livro antes!");
                return;
            }
            Reserva r = new Reserva();
            r.setId(reservas.size() + 200);
            r.setStatus("Ativa");
            r.setDataReserva(new Date());
            r.setUsuario(usuarios.get(usuarios.size() - 1));
            r.setLivro(livros.get(livros.size() - 1));
            reservas.add(r);
            JOptionPane.showMessageDialog(null, "Reserva criada!");
        });

        btn_encerrarReserva.addActionListener(e -> {
            if (reservas.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Nenhuma reserva encontrada!");
                return;
            }
            Reserva ultima = reservas.get(reservas.size() - 1);
            ultima.setStatus("Encerrada");
            JOptionPane.showMessageDialog(null, "Reserva encerrada!");
        });

        // Painel Empréstimo
        JPanel painelEmprestimo = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btn_emprestimo = new JButton("Criar Empréstimo");
        btn_emprestimo.setPreferredSize(new Dimension(250, 60));
        painelEmprestimo.add(btn_emprestimo);

        btn_emprestimo.addActionListener(e -> {
            if (usuarios.isEmpty() || livros.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Cadastre usuário e livro antes!");
                return;
            }
            Emprestimo emp = new Emprestimo();
            emp.setId(emprestimos.size() + 300);
            emp.setStatus("Em andamento");
            emp.setDataEmprestimo(new Date());
            emprestimos.add(emp);
            JOptionPane.showMessageDialog(null, "Empréstimo criado!");
        });

        // Painel Visualização
        JPanel painelVisualizar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btn_visualizar = new JButton("Visualizar Dados");
        btn_visualizar.setPreferredSize(new Dimension(250, 60));
        painelVisualizar.add(btn_visualizar);

        btn_visualizar.addActionListener(e -> {
            StringBuilder dados = new StringBuilder("--- DADOS ---\n");
            for (Usuario u : usuarios) dados.append("Usuário: ").append(u.getNome()).append("\n");
            for (Funcionario f : funcionarios) dados.append("Funcionário: ").append(f.getNome()).append("\n");
            for (Livro l : livros) dados.append("Livro: ").append(l.getTitulo()).append("\n");
            for (Reserva r : reservas)
                dados.append("Reserva: ").append(r.getStatus())
                        .append(" | Usuário: ").append(r.getUsuario().getNome())
                        .append(" | Livro: ").append(r.getLivro().getTitulo()).append("\n");
            for (Emprestimo em : emprestimos) dados.append("Empréstimo: ").append(em.getStatus()).append("\n");
            JOptionPane.showMessageDialog(null, dados.toString());
        });

        // Adiciona abas
        abas.addTab("Usuário", painelUsuario);
        abas.addTab("Funcionário", painelFuncionario);
        abas.addTab("Livro", painelLivro);
        abas.addTab("Reserva", painelReserva);
        abas.addTab("Empréstimo", painelEmprestimo);
        abas.addTab("Visualizar", painelVisualizar);

        janela.add(abas);
        janela.setVisible(true);
    }
}