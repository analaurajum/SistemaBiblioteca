package view;

import model.Funcionario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Optional;

public class FuncionarioPanel extends JPanel {

    private final FuncionarioRepository repo;

    private JTextField txtNome;
    private JTextField txtCargo;
    private JTextField txtMatricula;
    private JTextField txtEmail;
    private JTextField txtTelefone;
    private JTextField txtBusca;

    private JTable tabela;
    private DefaultTableModel tableModel;

    private int idEmEdicao = 0;

    public FuncionarioPanel(FuncionarioRepository repo) {
        this.repo = repo;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarPainelFormulario(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);

        carregarTabela(repo.listarTodos());
    }

    // ------------------------------------------------------------
    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel();
        painel.setBorder(BorderFactory.createTitledBorder("Cadastro de model.Funcionario"));
        painel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNome = new JTextField(20);
        txtCargo = new JTextField(15);
        txtMatricula = new JTextField(12);
        txtEmail = new JTextField(18);
        txtTelefone = new JTextField(12);

        int linha = 0;

        adicionarCampo(painel, gbc, linha++, "Nome:", txtNome, "Cargo:", txtCargo);
        adicionarCampo(painel, gbc, linha++, "Matricula Funcional:", txtMatricula, "Telefone:", txtTelefone);
        adicionarCampo(painel, gbc, linha++, "Email:", txtEmail, null, null);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnNovo = new JButton("Novo");
        JButton btnRemover = new JButton("Remover Selecionado");

        btnSalvar.addActionListener(this::salvar);
        btnNovo.addActionListener(e -> limparFormulario());
        btnRemover.addActionListener(this::remover);

        botoes.add(btnSalvar);
        botoes.add(btnNovo);
        botoes.add(btnRemover);

        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 4;
        painel.add(botoes, gbc);

        return painel;
    }

    private void adicionarCampo(JPanel painel, GridBagConstraints gbc, int linha,
                                 String label1, JComponent campo1, String label2, JComponent campo2) {
        gbc.gridy = linha;
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.weightx = 0;
        painel.add(new JLabel(label1), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        painel.add(campo1, gbc);

        if (label2 != null) {
            gbc.gridx = 2;
            gbc.weightx = 0;
            painel.add(new JLabel(label2), gbc);

            gbc.gridx = 3;
            gbc.weightx = 1;
            painel.add(campo2, gbc);
        }
    }

    // ------------------------------------------------------------
    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Consulta de Funcionarios"));

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBusca = new JTextField(20);
        JButton btnBuscar = new JButton("Buscar");
        JButton btnListarTodos = new JButton("Listar Todos");

        btnBuscar.addActionListener(e -> {
            String termo = txtBusca.getText().trim();
            if (termo.isEmpty()) {
                carregarTabela(repo.listarTodos());
            } else {
                carregarTabela(repo.buscarPorNomeOuMatricula(termo));
            }
        });
        btnListarTodos.addActionListener(e -> {
            txtBusca.setText("");
            carregarTabela(repo.listarTodos());
        });

        painelBusca.add(new JLabel("Buscar (nome ou matricula):"));
        painelBusca.add(txtBusca);
        painelBusca.add(btnBuscar);
        painelBusca.add(btnListarTodos);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Nome", "Cargo", "Matricula Funcional", "Email", "Telefone"}, 0) {
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
    private void salvar(ActionEvent e) {
        String nome = txtNome.getText().trim();
        String cargo = txtCargo.getText().trim();
        String matricula = txtMatricula.getText().trim();
        String email = txtEmail.getText().trim();
        String telefone = txtTelefone.getText().trim();

        if (nome.isEmpty() || matricula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e Matricula Funcional sao obrigatorios.",
                    "Validacao", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (repo.existeMatricula(matricula, idEmEdicao)) {
            JOptionPane.showMessageDialog(this, "Ja existe um funcionario com essa matricula.",
                    "Validacao", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Funcionario funcionario = new Funcionario(idEmEdicao, nome, cargo, matricula, email, telefone);
        repo.salvar(funcionario);

        limparFormulario();
        carregarTabela(repo.listarTodos());
        JOptionPane.showMessageDialog(this, "model.Funcionario salvo com sucesso!");
    }

    private void remover(ActionEvent e) {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um funcionario na tabela.",
                    "Atencao", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(linha, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente remover este funcionario?", "Confirmacao", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            repo.remover(id);
            limparFormulario();
            carregarTabela(repo.listarTodos());
        }
    }

    private void carregarSelecaoNoFormulario() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) return;

        int id = (int) tableModel.getValueAt(linha, 0);
        Optional<Funcionario> opt = repo.buscarPorId(id);
        opt.ifPresent(f -> {
            idEmEdicao = f.getId();
            txtNome.setText(f.getNome());
            txtCargo.setText(f.getCargo());
            txtMatricula.setText(f.getMatriculaFuncional());
            txtEmail.setText(f.getEmail());
            txtTelefone.setText(f.getTelefone());
        });
    }

    private void limparFormulario() {
        idEmEdicao = 0;
        txtNome.setText("");
        txtCargo.setText("");
        txtMatricula.setText("");
        txtEmail.setText("");
        txtTelefone.setText("");
        tabela.clearSelection();
    }

    private void carregarTabela(List<Funcionario> funcionarios) {
        tableModel.setRowCount(0);
        for (Funcionario f : funcionarios) {
            tableModel.addRow(new Object[]{
                    f.getId(), f.getNome(), f.getCargo(), f.getMatriculaFuncional(), f.getEmail(), f.getTelefone()
            });
        }
    }
}
