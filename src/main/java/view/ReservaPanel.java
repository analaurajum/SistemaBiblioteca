package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Aluno;
import model.Livro;
import model.Reserva;

public class ReservaPanel extends JPanel {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int DIAS_VALIDADE_PADRAO = 3;

    private final ReservaRepository repo;
    private final AlunoRepository alunoRepo;
    private final LivroRepository livroRepo;
    private final EmprestimoRepository emprestimoRepo;

    private JComboBox<ComboItem> comboAluno;
    private JComboBox<ComboItem> comboLivro;
    private JSpinner spnDiasValidade;

    private JTextField txtBusca;
    private JTable tabela;
    private DefaultTableModel tableModel;

    public ReservaPanel(ReservaRepository repo, AlunoRepository alunoRepo, LivroRepository livroRepo,
                         EmprestimoRepository emprestimoRepo) {
        this.repo = repo;
        this.alunoRepo = alunoRepo;
        this.livroRepo = livroRepo;
        this.emprestimoRepo = emprestimoRepo;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarPainelFormulario(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);

        carregarTabela(repo.listarTodos());
    }

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

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel();
        painel.setBorder(BorderFactory.createTitledBorder("Registrar Reserva"));
        painel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboAluno = new JComboBox<>();
        comboLivro = new JComboBox<>();
        spnDiasValidade = new JSpinner(new SpinnerNumberModel(DIAS_VALIDADE_PADRAO, 1, 60, 1));

        atualizarCombos();

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
        gbc.gridx = 0; gbc.weightx = 0; painel.add(new JLabel("Validade (dias a partir de hoje):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0; painel.add(spnDiasValidade, gbc);
        linha++;

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnReservar = new JButton("Registrar Reserva");
        JButton btnCancelar = new JButton("Cancelar Reserva Selecionada");
        JButton btnAtualizarListas = new JButton("Atualizar Listas");
        JButton btnRemover = new JButton("Remover Selecionado");

        btnReservar.addActionListener(this::registrarReserva);
        btnCancelar.addActionListener(this::cancelarReserva);
        btnAtualizarListas.addActionListener(e -> atualizarCombos());
        btnRemover.addActionListener(this::remover);

        botoes.add(btnReservar);
        botoes.add(btnCancelar);
        botoes.add(btnAtualizarListas);
        botoes.add(btnRemover);

        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 4;
        painel.add(botoes, gbc);

        JLabel info = new JLabel("Obs: a reserva nao reduz o estoque disponivel; ela apenas registra interesse e prioridade.");
        info.setFont(info.getFont().deriveFont(Font.ITALIC, 11f));
        gbc.gridy = ++linha;
        painel.add(info, gbc);
        gbc.gridwidth = 1;

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
    }

    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Consulta de Reservas"));

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
            List<Reserva> todos = repo.listarTodos();
            todos.removeIf(r -> alunosEncontrados.stream().noneMatch(a -> a.getId() == r.getAlunoId()));
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
                "ID", "Aluno", "Livro", "Data Reserva", "Validade", "Status"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        painel.add(painelBusca, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        return painel;
    }

    private void registrarReserva(ActionEvent e) {
        ComboItem aluno = (ComboItem) comboAluno.getSelectedItem();
        ComboItem livroItem = (ComboItem) comboLivro.getSelectedItem();
        int dias = (int) spnDiasValidade.getValue();

        if (aluno == null || livroItem == null) {
            JOptionPane.showMessageDialog(this,
                    "Cadastre ao menos um Aluno e um Livro no estoque antes de registrar a reserva.",
                    "Validacao", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Evita reserva duplicada do mesmo aluno para o mesmo livro
        for (Reserva r : repo.buscarAtivasPorLivro(livroItem.id)) {
            if (r.getAlunoId() == aluno.id) {
                JOptionPane.showMessageDialog(this,
                        "Este aluno ja possui uma reserva ativa para este livro.",
                        "Validacao", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        Optional<Livro> optLivro = livroRepo.buscarPorId(livroItem.id);
        if (optLivro.isPresent() && optLivro.get().getQuantidadeDisponivel() > 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Este livro possui exemplar(es) disponivel(is) agora.\n"
                            + "Ainda assim deseja registrar uma reserva (em vez de um emprestimo direto)?",
                    "Confirmacao", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
        }

        LocalDate hoje = LocalDate.now();
        Reserva reserva = new Reserva(0, aluno.id, livroItem.id, hoje, hoje.plusDays(dias), "ATIVA");
        repo.salvar(reserva);

        carregarTabela(repo.listarTodos());
        JOptionPane.showMessageDialog(this, "Reserva registrada com sucesso! Valida até " + reserva.getDataValidade().format(FMT) + ".");
    }

    private void cancelarReserva(ActionEvent e) {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva na tabela.",
                    "Atencao", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(linha, 0);
        Optional<Reserva> opt = repo.buscarPorId(id);
        if (opt.isEmpty()) return;

        Reserva r = opt.get();
        if (!"ATIVA".equals(r.getStatus())) {
            JOptionPane.showMessageDialog(this, "Apenas reservas ATIVAS podem ser canceladas.",
                    "Atencao", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        r.setStatus("CANCELADA");
        repo.salvar(r);
        carregarTabela(repo.listarTodos());
    }

    private void remover(ActionEvent e) {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva na tabela.",
                    "Atencao", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(linha, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente remover este registro de reserva?", "Confirmacao", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            repo.remover(id);
            carregarTabela(repo.listarTodos());
        }
    }

    private void carregarTabela(List<Reserva> reservas) {
        tableModel.setRowCount(0);
        for (Reserva r : reservas) {
            String nomeAluno = alunoRepo.buscarPorId(r.getAlunoId()).map(Aluno::getNome).orElse("(removido)");
            String tituloLivro = livroRepo.buscarPorId(r.getLivroId()).map(Livro::getTitulo).orElse("(removido)");

            tableModel.addRow(new Object[]{
                    r.getId(), nomeAluno, tituloLivro,
                    r.getDataReserva().format(FMT),
                    r.getDataValidade().format(FMT),
                    r.getStatus()
            });
        }
    }
}
