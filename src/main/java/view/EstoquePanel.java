package view;

import controller.OpenLibraryService;
import model.Livro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Optional;

public class EstoquePanel extends JPanel {

    private final LivroRepository repo;
    private final OpenLibraryService openLibrary = new OpenLibraryService();

    // Formulario manual
    private JTextField txtIsbn;
    private JTextField txtTitulo;
    private JTextField txtAutor;
    private JTextField txtAno;
    private JSpinner spnQuantidade;

    // Busca Open Library
    private JTextField txtBuscaApi;
    private JComboBox<OpenLibraryService.LivroResultado> comboResultadosApi;
    private JLabel lblCapa;

    // Tabela de estoque
    private JTextField txtBuscaEstoque;
    private JTable tabela;
    private DefaultTableModel tableModel;

    private int idEmEdicao = 0;

    public EstoquePanel(LivroRepository repo) {
        this.repo = repo;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topo = new JPanel();
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.add(criarPainelBuscaApi());
        topo.add(criarPainelFormulario());

        add(topo, BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);

        carregarTabela(repo.listarTodos());
    }

    // ------------------------------------------------------------
    // Busca na Open Library API
    // ------------------------------------------------------------
    private JPanel criarPainelBuscaApi() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Buscar livro na Open Library API (openlibrary.org)"));

        JPanel linhaBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBuscaApi = new JTextField(25);
        JButton btnBuscarTitulo = new JButton("Buscar por Titulo/Autor");
        JButton btnBuscarIsbn = new JButton("Buscar por ISBN");

        btnBuscarTitulo.addActionListener(e -> buscarNaApi(false));
        btnBuscarIsbn.addActionListener(e -> buscarNaApi(true));

        linhaBusca.add(new JLabel("Termo / ISBN:"));
        linhaBusca.add(txtBuscaApi);
        linhaBusca.add(btnBuscarTitulo);
        linhaBusca.add(btnBuscarIsbn);

        JPanel linhaResultado = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboResultadosApi = new JComboBox<>();
        comboResultadosApi.setPreferredSize(new Dimension(420, 25));
        JButton btnPreencher = new JButton("Preencher Formulario");
        btnPreencher.addActionListener(e -> preencherComResultadoApi());

        lblCapa = new JLabel();
        lblCapa.setPreferredSize(new Dimension(60, 80));
        lblCapa.setHorizontalAlignment(JLabel.CENTER);
        lblCapa.setBorder(BorderFactory.createEtchedBorder());

        linhaResultado.add(new JLabel("Resultados:"));
        linhaResultado.add(comboResultadosApi);
        linhaResultado.add(btnPreencher);
        linhaResultado.add(lblCapa);

        comboResultadosApi.addActionListener(e -> atualizarCapaPreview());

        painel.add(linhaBusca, BorderLayout.NORTH);
        painel.add(linhaResultado, BorderLayout.SOUTH);
        return painel;
    }

    private void buscarNaApi(boolean porIsbn) {
        String termo = txtBuscaApi.getText().trim();
        if (termo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe um termo de busca ou ISBN.",
                    "Atencao", JOptionPane.WARNING_MESSAGE);
            return;
        }

        comboResultadosApi.removeAllItems();
        lblCapa.setIcon(null);
        lblCapa.setText("");

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<List<OpenLibraryService.LivroResultado>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<OpenLibraryService.LivroResultado> doInBackground() throws Exception {
                if (porIsbn) {
                    return openLibrary.buscarPorIsbn(termo);
                } else {
                    return openLibrary.buscarPorTermo(termo);
                }
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    List<OpenLibraryService.LivroResultado> resultados = get();
                    if (resultados.isEmpty()) {
                        JOptionPane.showMessageDialog(EstoquePanel.this,
                                "Nenhum resultado encontrado na Open Library para: " + termo,
                                "Busca", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        for (OpenLibraryService.LivroResultado lr : resultados) {
                            comboResultadosApi.addItem(lr);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(EstoquePanel.this,
                            "Erro ao consultar a Open Library API:\n" + ex.getMessage()
                                    + "\n\nVerifique sua conexao com a internet.",
                            "Erro de Conexao", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void preencherComResultadoApi() {
        OpenLibraryService.LivroResultado lr = (OpenLibraryService.LivroResultado) comboResultadosApi.getSelectedItem();
        if (lr == null) {
            JOptionPane.showMessageDialog(this, "Selecione um resultado da busca primeiro.",
                    "Atencao", JOptionPane.WARNING_MESSAGE);
            return;
        }
        txtTitulo.setText(lr.titulo != null ? lr.titulo : "");
        txtAutor.setText(lr.autor != null ? lr.autor : "");
        txtAno.setText(lr.anoPublicacao != null ? lr.anoPublicacao : "");
        txtIsbn.setText(lr.isbn != null ? lr.isbn : "");
    }

    private void atualizarCapaPreview() {
        OpenLibraryService.LivroResultado lr = (OpenLibraryService.LivroResultado) comboResultadosApi.getSelectedItem();
        if (lr == null || lr.capaUrl == null) {
            lblCapa.setIcon(null);
            lblCapa.setText("Sem capa");
            return;
        }
        carregarImagemAsync(lr.capaUrl, lblCapa);
    }

    /**
     * Carrega uma imagem de uma URL em background e exibe no JLabel informado.
     */
    private void carregarImagemAsync(String urlImagem, JLabel destino) {
        destino.setText("Carregando...");
        destino.setIcon(null);
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    java.net.URL url = new java.net.URL(urlImagem);
                    Image img = new ImageIcon(url).getImage().getScaledInstance(55, 75, Image.SCALE_SMOOTH);
                    return new ImageIcon(img);
                } catch (Exception ex) {
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        destino.setIcon(icon);
                        destino.setText("");
                    } else {
                        destino.setIcon(null);
                        destino.setText("Sem capa");
                    }
                } catch (Exception ex) {
                    destino.setIcon(null);
                    destino.setText("Sem capa");
                }
            }
        };
        worker.execute();
    }

    // ------------------------------------------------------------
    // Formulario de cadastro manual / edicao
    // ------------------------------------------------------------
    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel();
        painel.setBorder(BorderFactory.createTitledBorder("Cadastro de model.Livro no Estoque"));
        painel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtIsbn = new JTextField(15);
        txtTitulo = new JTextField(25);
        txtAutor = new JTextField(20);
        txtAno = new JTextField(6);
        spnQuantidade = new JSpinner(new SpinnerNumberModel(1, 0, 9999, 1));

        int linha = 0;

        gbc.gridy = linha;
        gbc.gridx = 0; gbc.weightx = 0; painel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; painel.add(txtIsbn, gbc);
        gbc.gridx = 2; gbc.weightx = 0; painel.add(new JLabel("Ano Publicacao:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1; painel.add(txtAno, gbc);
        linha++;

        gbc.gridy = linha;
        gbc.gridx = 0; gbc.weightx = 0; painel.add(new JLabel("Titulo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3; painel.add(txtTitulo, gbc);
        gbc.gridwidth = 1;
        linha++;

        gbc.gridy = linha;
        gbc.gridx = 0; gbc.weightx = 0; painel.add(new JLabel("Autor:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3; painel.add(txtAutor, gbc);
        gbc.gridwidth = 1;
        linha++;

        gbc.gridy = linha;
        gbc.gridx = 0; gbc.weightx = 0; painel.add(new JLabel("Quantidade em Estoque:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0; painel.add(spnQuantidade, gbc);
        linha++;

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnSalvar = new JButton("Salvar no Estoque");
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

    // ------------------------------------------------------------
    // Tabela de estoque
    // ------------------------------------------------------------
    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Consulta de Estoque"));

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBuscaEstoque = new JTextField(20);
        JButton btnBuscar = new JButton("Buscar");
        JButton btnListarTodos = new JButton("Listar Todos");

        btnBuscar.addActionListener(e -> {
            String termo = txtBuscaEstoque.getText().trim();
            if (termo.isEmpty()) {
                carregarTabela(repo.listarTodos());
            } else {
                carregarTabela(repo.buscarPorTituloOuAutorOuIsbn(termo));
            }
        });
        btnListarTodos.addActionListener(e -> {
            txtBuscaEstoque.setText("");
            carregarTabela(repo.listarTodos());
        });

        painelBusca.add(new JLabel("Buscar (titulo, autor ou ISBN):"));
        painelBusca.add(txtBuscaEstoque);
        painelBusca.add(btnBuscar);
        painelBusca.add(btnListarTodos);

        tableModel = new DefaultTableModel(new Object[]{
                "ID", "ISBN", "Titulo", "Autor", "Ano", "Qtd Total", "Qtd Disponivel"
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
    // Acoes CRUD
    // ------------------------------------------------------------
    private void salvar(ActionEvent e) {
        String isbn = txtIsbn.getText().trim();
        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        String ano = txtAno.getText().trim();
        int qtdTotal = (int) spnQuantidade.getValue();

        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Titulo e obrigatorio.",
                    "Validacao", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (idEmEdicao == 0) {
            // Novo registro
            if (!isbn.isEmpty()) {
                Optional<Livro> existente = repo.buscarPorIsbn(isbn);
                if (existente.isPresent()) {
                    JOptionPane.showMessageDialog(this,
                            "Ja existe um livro com este ISBN no estoque. Selecione-o na tabela para editar a quantidade.",
                            "Validacao", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            String capaUrl = OpenLibraryService.urlCapaPorIsbn(isbn);
            Livro livro = new Livro(0, isbn, titulo, autor, ano, capaUrl, qtdTotal, qtdTotal);
            repo.salvar(livro);
        } else {
            // Edicao: ajusta disponibilidade proporcionalmente a alteracao de total
            Optional<Livro> opt = repo.buscarPorId(idEmEdicao);
            if (opt.isPresent()) {
                Livro livro = opt.get();
                int totalAnterior = livro.getQuantidadeTotal();
                int disponivelAnterior = livro.getQuantidadeDisponivel();
                int emprestados = totalAnterior - disponivelAnterior;

                int novoDisponivel = qtdTotal - emprestados;
                if (novoDisponivel < 0) novoDisponivel = 0;

                livro.setIsbn(isbn);
                livro.setTitulo(titulo);
                livro.setAutor(autor);
                livro.setAnoPublicacao(ano);
                livro.setCapaUrl(OpenLibraryService.urlCapaPorIsbn(isbn));
                livro.setQuantidadeTotal(qtdTotal);
                livro.setQuantidadeDisponivel(novoDisponivel);
                repo.salvar(livro);
            }
        }

        limparFormulario();
        carregarTabela(repo.listarTodos());
        JOptionPane.showMessageDialog(this, "model.Livro salvo no estoque com sucesso!");
    }

    private void remover(ActionEvent e) {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um livro na tabela.",
                    "Atencao", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(linha, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente remover este livro do estoque?", "Confirmacao", JOptionPane.YES_NO_OPTION);
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
        Optional<Livro> opt = repo.buscarPorId(id);
        opt.ifPresent(l -> {
            idEmEdicao = l.getId();
            txtIsbn.setText(l.getIsbn());
            txtTitulo.setText(l.getTitulo());
            txtAutor.setText(l.getAutor());
            txtAno.setText(l.getAnoPublicacao());
            spnQuantidade.setValue(l.getQuantidadeTotal());
        });
    }

    private void limparFormulario() {
        idEmEdicao = 0;
        txtIsbn.setText("");
        txtTitulo.setText("");
        txtAutor.setText("");
        txtAno.setText("");
        spnQuantidade.setValue(1);
        tabela.clearSelection();
    }

    private void carregarTabela(List<Livro> livros) {
        tableModel.setRowCount(0);
        for (Livro l : livros) {
            tableModel.addRow(new Object[]{
                    l.getId(), l.getIsbn(), l.getTitulo(), l.getAutor(),
                    l.getAnoPublicacao(), l.getQuantidadeTotal(), l.getQuantidadeDisponivel()
            });
        }
    }
}
