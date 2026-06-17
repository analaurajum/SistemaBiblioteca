package controller;

import java.awt.*;
import javax.swing.*;
import view.*;

public class BibliotecaApp extends JFrame {

    public BibliotecaApp() {
        // Janela do Sistema
        super("Sistema de Biblioteca");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        // Repositorios compartilhados entre os paineis
        AlunoRepository alunoRepo = new AlunoRepository();
        FuncionarioRepository funcionarioRepo = new FuncionarioRepository();
        LivroRepository livroRepo = new LivroRepository();
        EmprestimoRepository emprestimoRepo = new EmprestimoRepository();
        ReservaRepository reservaRepo = new ReservaRepository();

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Abas
        tabs.addTab("Alunos", new AlunoPanel(alunoRepo));
        tabs.addTab("Funcionarios", new FuncionarioPanel(funcionarioRepo));
        tabs.addTab("Estoque (Livros)", new LivroPanel(livroRepo));
        tabs.addTab("Emprestimos", new EmprestimoPanel(emprestimoRepo, alunoRepo, livroRepo, funcionarioRepo, reservaRepo));
        tabs.addTab("Reservas", new ReservaPanel(reservaRepo, alunoRepo, livroRepo, emprestimoRepo));

        add(tabs, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new BibliotecaApp().setVisible(true));
    }
}
