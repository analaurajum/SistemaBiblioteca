package view;

import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class EmprestimoPanel extends JPanel {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final EmprestimoRepository repo;
    private final AlunoRepository alunoRepo;
    private final LivroRepository livroRepo;
    private final FuncionarioRepository funcionarioRepo;
    private final ReservaRepository reservaRepo;

    private JComboBox<ComboItem> comboAluno;
    private JComboBox<ComboItem> comboLivro;
    private JComboBox<ComboItem> comboFuncionario;
    private JTextField txtDataEmprestimo;
    private JTextField txtDataPrevista;

    private JTextField txtBusca;
    private JTable tabela;
    private DefaultTableModel tableModel;

    private int idEmEdicao = 0;

    public EmprestimoPanel(EmprestimoRepository repo, AlunoRepository alunoRepo, LivroRepository livroRepo,
                           FuncionarioRepository funcionarioRepo, ReservaRepository reservaRepo) {
        this.repo = repo;
        this.alunoRepo = alunoRepo;
        this.livroRepo = livroRepo;
        this.funcionarioRepo = funcionarioRepo;
        this.reservaRepo = reservaRepo;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarPainelFormulario(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);

        carregarTabela(repo.listarTodos());
    }

    // ------------------------------------------------------------
    // Item generico para combos (id + texto exibido)
    // ------------------------------------------------------------
    private static class ComboItem {
        int id;
        String texto;

        ComboItem(int id, String texto) {
            this.id = id;
            this.texto = texto;
        }

        @Override
        public String toString() {
            return texto;
        }
    }

    // ------------------------------------------------------------
    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel();
        painel.setBorder(BorderFactory.createTitledBorder("Registrar Empréstimo"));
        painel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboAluno = new JComboBox<>();
        comboLivro = new JComboBox<>();
        comboFuncionario = new JComboBox<>();
        txtDataEmprestimo = new JTextField(10);
        txtDataPrevista = new JTextField(10);

        atualizarCombos();

        txtDataEmprestimo.setText(LocalDate.now().format(FMT));
        txtDataPrevista.setText(LocalDate.now().plusDays(7).format(FMT));

        int linha = 0;

        gbc.gridy = linha;
        gbc.gridx = 0; gbc.weightx = 0; painel.add(new JLabel("Aluno:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3; painel.add(comboAluno, gbc);
        gbc.gridwidth = 1;
        linha++;

        gbc.gridy = linha;
        gbc.gridx = 0; gbc.weightx = 0; painel.add(new JLabel("Livro:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3; painel.add(comboLivro, gbc);
        gbc.gridwidth = 1;
        linha++;

        gbc.gridy = linha;
        gbc.gridx = 0; gbc.weightx = 0; painel.add(new JLabel("Funcionário Responsável:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3; painel.add(comboFuncionario, gbc);
        gbc.gridwidth = 1;
        linha++;

        gbc.gridy = linha;
        gbc.gridx = 0; gbc.weightx = 0; painel.add(new JLabel("Data Empréstimo (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; painel.add(txtDataEmprestimo, gbc);
        gbc.gridx = 2; gbc.weightx = 0; painel.add(new JLabel("Devolução Prevista:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1; painel.add(txtDataPrevista, gbc);
        linha++;

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSalvar = new JButton("Registrar Empréstimo");
        JButton btnDevolver = new JButton("Marcar como Devolvido");
        JButton btnNovo = new JButton("Novo");
        JButton btnAtualizarListas = new JButton("Atualizar Listas");
        JButton btnRemover = new JButton("Remover Selecionado");

        btnSalvar.addActionListener(this::registrarEmprestimo);
        btnDevolver.addActionListener(this::marcarDevolvido);
        btnNovo.addActionListener(e -> limparFormulario());
        btnAtualizarListas.addActionListener(e -> atualizarCombos());
        btnRemover.addActionListener(this::remover);

        botoes.add(btnSalvar);
        botoes.add(btnDevolver);
        botoes.add(btnNovo);
        botoes.add(btnAtualizarListas);
        botoes.add(btnRemover);

        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 4;
        painel.add(botoes, gbc);

        return painel;
    }

    private void atualizarCombos() {
        comboAluno.removeAllItems();
        for (Aluno a : alunoRepo.listarTodos()) {
            comboAluno.addItem(new ComboItem(a.getId(), a.getNome() + " (" + a.getMatricula() + ")"));
        }

        comboLivro.removeAllItems();
        for (Livro l : livroRepo.listarTodos()) {
            String disponibilidade = " [Disp: " + l.getQuantidadeDisponivel() + "/" + l.getQuantidadeTotal() + "]";
            comboLivro.addItem(new ComboItem(l.getId(), l.getTitulo() + disponibilidade));
        }

        comboFuncionario.removeAllItems();
        for (Funcionario f : funcionarioRepo.listarTodos()) {
            comboFuncionario.addItem(new ComboItem(f.getId(), f.getNome() + " (" + f.getCargo() + ")"));
        }
    }

    // ------------------------------------------------------------
    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Consulta de Empréstimos"));

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBusca = new JTextField(20);
        JButton btnBuscarAluno = new JButton("Filtrar por Nome do Aluno");
        JButton btnListarTodos = new JButton("Listar Todos");

        btnBuscarAluno.addActionListener(e -> {
            String termo = txtBusca.getText().trim();
            if (termo.isEmpty()) {
                carregarTabela(repo.listarTodos());
                return;
            }
            List<Aluno> alunosEncontrados = alunoRepo.buscarPorNomeOuMatricula(termo);
            List<Emprestimo> todos = repo.listarTodos();
            todos.removeIf(emp -> alunosEncontrados.stream().noneMatch(a -> a.getId() == emp.getAlunoId()));
            carregarTabela(todos);
        });
        btnListarTodos.addActionListener(e -> {
            txtBusca.setText("");
            carregarTabela(repo.listarTodos());
        });

        painelBusca.add(new JLabel("Buscar (nome/matricula do aluno):"));
        painelBusca.add(txtBusca);
        painelBusca.add(btnBuscarAluno);
        painelBusca.add(btnListarTodos);

        tableModel = new DefaultTableModel(new Object[]{
                "ID", "Aluno", "Livro", "Funcionario", "Data Emprestimo", "Devolução Prevista", "Data Devolução", "Status"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) carregarSelecaoNoFormulario();
        });

        painel.add(painelBusca, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        return painel;
    }

    // ------------------------------------------------------------
    // Acoes
    // ------------------------------------------------------------
    private void registrarEmprestimo(ActionEvent e) {
        ComboItem aluno = (ComboItem) comboAluno.getSelectedItem();
        ComboItem livroItem = (ComboItem) comboLivro.getSelectedItem();
        ComboItem funcionario = (ComboItem) comboFuncionario.getSelectedItem();

        if (aluno == null || livroItem == null || funcionario == null) {
            JOptionPane.showMessageDialog(this,
                    "Cadastre ao menos um Aluno, um Livro no estoque e um Funcionário antes de registrar o empréstimo.",
                    "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate dataEmprestimo;
        LocalDate dataPrevista;
        try {
            dataEmprestimo = LocalDate.parse(txtDataEmprestimo.getText().trim(), FMT);
            dataPrevista = LocalDate.parse(txtDataPrevista.getText().trim(), FMT);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Datas inválidas. Use o formato dd/MM/yyyy.",
                    "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (dataPrevista.isBefore(dataEmprestimo)) {
            JOptionPane.showMessageDialog(this,
                    "A data prevista de devolução não pode ser anterior a data do empréstimo.",
                    "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<Livro> optLivro = livroRepo.buscarPorId(livroItem.id);
        if (optLivro.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Livro não encontrado no estoque.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Livro livro = optLivro.get();

        if (idEmEdicao == 0) {
            // Novo emprestimo: precisa haver exemplar disponivel
            if (livro.getQuantidadeDisponivel() <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Não há exemplares disponíveis deste livro no estoque.",
                        "Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Emprestimo emprestimo = new Emprestimo(0, aluno.id, livroItem.id, funcionario.id,
                    dataEmprestimo, dataPrevista, null, "ATIVO");
            repo.salvar(emprestimo);

            // Atualiza estoque
            livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() - 1);
            livroRepo.salvar(livro);

            // Caso haja reserva ativa deste aluno para este livro, converte para "CONVERTIDA"
            for (Reserva r : reservaRepo.buscarAtivasPorLivro(livroItem.id)) {
                if (r.getAlunoId() == aluno.id) {
                    r.setStatus("CONVERTIDA");
                    reservaRepo.salvar(r);
                    break;
                }
            }
        } else {
            // Edicao de emprestimo existente (sem alterar estoque diretamente)
            Optional<Emprestimo> optEmp = repo.buscarPorId(idEmEdicao);
            if (optEmp.isPresent()) {
                Emprestimo emp = optEmp.get();
                emp.setAlunoId(aluno.id);
                emp.setLivroId(livroItem.id);
                emp.setFuncionarioId(funcionario.id);
                emp.setDataEmprestimo(dataEmprestimo);
                emp.setDataPrevistaDevolucao(dataPrevista);
                if (emp.getDataDevolucao() == null && emp.getDataPrevistaDevolucao().isBefore(LocalDate.now())) {
                    emp.setStatus("ATRASADO");
                } else if (emp.getDataDevolucao() == null) {
                    emp.setStatus("ATIVO");
                }
                repo.salvar(emp);
            }
        }

        limparFormulario();
        atualizarCombos();
        carregarTabela(repo.listarTodos());
        JOptionPane.showMessageDialog(this, "Empréstimo salvo com sucesso!");
    }

    private void marcarDevolvido(ActionEvent e) {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um empréstimo na tabela.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(linha, 0);
        Optional<Emprestimo> opt = repo.buscarPorId(id);
        if (opt.isEmpty()) return;

        Emprestimo emp = opt.get();
        if (emp.getDataDevolucao() != null) {
            JOptionPane.showMessageDialog(this, "Este empréstimo já foi devolvido.",
                    "Atenção", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        emp.setDataDevolucao(LocalDate.now());
        emp.setStatus("DEVOLVIDO");
        repo.salvar(emp);

        // Devolve exemplar ao estoque
        Optional<Livro> optLivro = livroRepo.buscarPorId(emp.getLivroId());
        if (optLivro.isPresent()) {
            Livro livro = optLivro.get();
            int novaDisp = livro.getQuantidadeDisponivel() + 1;
            if (novaDisp > livro.getQuantidadeTotal()) novaDisp = livro.getQuantidadeTotal();
            livro.setQuantidadeDisponivel(novaDisp);
            livroRepo.salvar(livro);
        }

        limparFormulario();
        atualizarCombos();
        carregarTabela(repo.listarTodos());
        JOptionPane.showMessageDialog(this, "Devolução registrada com sucesso! Estoque atualizado.");
    }

    private void remover(ActionEvent e) {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um empréstimo na tabela.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(linha, 0);

        Optional<Emprestimo> opt = repo.buscarPorId(id);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente remover este registro de empréstimo?\n"
                        + "Atenção: se o empréstimo estiver ATIVO/ATRASADO, o exemplar voltará ao estoque.",
                "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (opt.isPresent()) {
            Emprestimo emp = opt.get();
            if (emp.getDataDevolucao() == null) {
                Optional<Livro> optLivro = livroRepo.buscarPorId(emp.getLivroId());
                if (optLivro.isPresent()) {
                    Livro livro = optLivro.get();
                    int novaDisp = livro.getQuantidadeDisponivel() + 1;
                    if (novaDisp > livro.getQuantidadeTotal()) novaDisp = livro.getQuantidadeTotal();
                    livro.setQuantidadeDisponivel(novaDisp);
                    livroRepo.salvar(livro);
                }
            }
        }

        repo.remover(id);
        limparFormulario();
        atualizarCombos();
        carregarTabela(repo.listarTodos());
    }

    private void carregarSelecaoNoFormulario() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) return;

        int id = (int) tableModel.getValueAt(linha, 0);
        Optional<Emprestimo> opt = repo.buscarPorId(id);
        opt.ifPresent(emp -> {
            idEmEdicao = emp.getId();
            selecionarComboPorId(comboAluno, emp.getAlunoId());
            selecionarComboPorId(comboLivro, emp.getLivroId());
            selecionarComboPorId(comboFuncionario, emp.getFuncionarioId());
            txtDataEmprestimo.setText(emp.getDataEmprestimo().format(FMT));
            txtDataPrevista.setText(emp.getDataPrevistaDevolucao().format(FMT));
        });
    }

    private void selecionarComboPorId(JComboBox<ComboItem> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).id == id) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void limparFormulario() {
        idEmEdicao = 0;
        txtDataEmprestimo.setText(LocalDate.now().format(FMT));
        txtDataPrevista.setText(LocalDate.now().plusDays(7).format(FMT));
        if (comboAluno.getItemCount() > 0) comboAluno.setSelectedIndex(0);
        if (comboLivro.getItemCount() > 0) comboLivro.setSelectedIndex(0);
        if (comboFuncionario.getItemCount() > 0) comboFuncionario.setSelectedIndex(0);
        tabela.clearSelection();
    }

    private void carregarTabela(List<Emprestimo> emprestimos) {
        tableModel.setRowCount(0);
        for (Emprestimo emp : emprestimos) {
            String nomeAluno = alunoRepo.buscarPorId(emp.getAlunoId()).map(Aluno::getNome).orElse("(removido)");
            String tituloLivro = livroRepo.buscarPorId(emp.getLivroId()).map(Livro::getTitulo).orElse("(removido)");
            String nomeFunc = funcionarioRepo.buscarPorId(emp.getFuncionarioId()).map(Funcionario::getNome).orElse("(removido)");
            String dataDevolucao = emp.getDataDevolucao() != null ? emp.getDataDevolucao().format(FMT) : "-";

            tableModel.addRow(new Object[]{
                    emp.getId(), nomeAluno, tituloLivro, nomeFunc,
                    emp.getDataEmprestimo().format(FMT),
                    emp.getDataPrevistaDevolucao().format(FMT),
                    dataDevolucao,
                    emp.getStatus()
            });
        }
    }
}
