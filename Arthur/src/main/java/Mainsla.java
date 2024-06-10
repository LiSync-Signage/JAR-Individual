import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.github.britooo.looca.api.group.processos.Processo;
import models.*;
import services.Autenticacao;
import dao.UsuarioDAO;


import services.ServicosLisync;

import com.github.britooo.looca.api.core.Looca;
import dao.*;
import dao.TelevisaoDAO;
import models.Usuario;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;


public class Mainsla {
    private static JFrame frame;
    private static Usuario usuarioAutenticado;




    public static void main(String[] args) {
        frame = new JFrame("Login");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panelLogin = new JPanel(new GridBagLayout());
        frame.add(panelLogin);
        placeComponents(panelLogin);
        frame.setVisible(true);
        frame.setResizable(false);
        centerFrameOnScreen(frame);

    }


    //Janela Login
    private static void placeComponents(JPanel panelLogin) {


        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(10, 10, 10, 10);

        JLabel emailLabel = new JLabel("Email:");
        panelLogin.add(emailLabel, constraints);

        JTextField emailText = new JTextField(20);
        constraints.gridy = 1;
        panelLogin.add(emailText, constraints);

        JLabel senhaLabel = new JLabel("Senha:");
        constraints.gridy = 2;
        panelLogin.add(senhaLabel, constraints);

        JPasswordField senhaText = new JPasswordField(20);
        constraints.gridy = 3;
        panelLogin.add(senhaText, constraints);

        JButton loginButton = new JButton("Login");
        constraints.gridy = 4;
        panelLogin.add(loginButton, constraints);


        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = emailText.getText();
                String senha = new String(senhaText.getPassword());
                Autenticacao autenticacao = new Autenticacao();
                ServicosLisync servicosLisync = new ServicosLisync();
                boolean autenticado = autenticacao.validacaoLogin(email, senha);


                if (autenticado) {
                    UsuarioDAO usuarioDAO = new UsuarioDAO();
                    usuarioAutenticado = usuarioDAO.buscarCreedenciasUsuario(email, senha);
                    JOptionPane.showMessageDialog(null, "Bem-vindo, " + usuarioAutenticado.getNomeUsuario()+ "!");
                    servicosLisync.atualizarEmpresaDoUsuario(usuarioAutenticado.getFkEmpresa());
                    servicosLisync.atualizarUsuario(usuarioAutenticado);
                    frame.dispose();
                    Looca looca = new Looca();
                    String hostName = looca.getRede().getParametros().getHostName();
                    usuarioAutenticado = usuarioDAO.buscarCreedenciasUsuario(email, senha);


                    ///Verificação de existência de televisões na empresa
                    if(!servicosLisync.televisaoNova(hostName)){

                        JFrame homePanel = new JFrame("Tela Inicial");
                        placeComponentsHome(homePanel);
                        homePanel.setSize(300, 400);
                        homePanel.setVisible(true);
                        centerFrameOnScreen(homePanel);
                        homePanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                    }else {
                        JFrame panelCadastroTvs = new JFrame("CadastroTvs");
                        placeComponentsCadastro(panelCadastroTvs);
                        panelCadastroTvs.setSize(300, 300);
                        panelCadastroTvs.setVisible(true);
                        centerFrameOnScreen(panelCadastroTvs);
                        panelCadastroTvs.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                    }



                } else {
                    JOptionPane.showMessageDialog(null, "Email ou senha incorretos!");
                }
            }
        });


    }

    private static void centerFrameOnScreen(JFrame frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) ((screenSize.getWidth() - frame.getWidth()) / 2);
        int centerY = (int) ((screenSize.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(centerX, centerY);
    }


    private static void placeComponentsHome(JFrame homePanel) {
        frame.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton buttonMonitoramento = new JButton("Encerrar Processo");

        Dimension tamanhoBotão = new Dimension(100, 50);



        buttonMonitoramento.setPreferredSize(tamanhoBotão);
        buttonMonitoramento.setMaximumSize(tamanhoBotão);
        buttonMonitoramento.setMinimumSize(tamanhoBotão);

        buttonPanel.add(Box.createVerticalGlue()); // espaço  antes dos botões
        buttonPanel.add(Box.createRigidArea(new Dimension(90,5))); // Gap botões
        buttonPanel.add(buttonMonitoramento);
        buttonPanel.add(Box.createRigidArea(new Dimension(90,5))); // Gap botões
        buttonPanel.add(Box.createVerticalGlue()); //  espaço depois dos botões

        homePanel.add(buttonPanel, BorderLayout.CENTER);


        buttonMonitoramento.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                        JFrame panelTvs = new JFrame("Janela de Encerramento de Processo");
                        placeComponentsPanelTvs(panelTvs);
                        panelTvs.setSize(600, 300);
                        panelTvs.setVisible(true);
                        centerFrameOnScreen(panelTvs);
                        panelTvs.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            }
        });
    }



    ///Janela de Tvs
    private static void placeComponentsPanelTvs(JFrame panelTvs) {
        panelTvs.setLayout(new BorderLayout());

        Looca looca = new Looca();
        List<com.github.britooo.looca.api.group.processos.Processo> processos = looca.getGrupoDeProcessos().getProcessos();
        Map<String, Integer> processoMap = new HashMap<>();

        for (com.github.britooo.looca.api.group.processos.Processo processo : processos) {
            processoMap.put(processo.getNome(), processo.getPid());
        }

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<String> comboBox = new JComboBox<>(processoMap.keySet().toArray(new String[0]));
        Dimension comboBoxSize = new Dimension(180, comboBox.getPreferredSize().height);
        comboBox.setPreferredSize(comboBoxSize);
        if (!processoMap.isEmpty()) {
            comboBox.setSelectedIndex(0);
        }

        JButton cadTvButton = new JButton("Cadastrar Televisão");
        Dimension buttonSize = new Dimension(180, cadTvButton.getPreferredSize().height);
        cadTvButton.setPreferredSize(buttonSize);

        contentPanel.add(comboBox);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(cadTvButton);

        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        cadTvButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelTvs.add(contentPanel, BorderLayout.CENTER);

        comboBox.addActionListener(e -> {
            String processoSelecionado = (String) comboBox.getSelectedItem();
            int pid = processoMap.get(processoSelecionado);
            System.out.println("Processo selecionado: " + processoSelecionado + " (PID: " + pid + ")");
        });

        cadTvButton.addActionListener(e -> {
            String processoSelecionado = (String) comboBox.getSelectedItem();
            int pid = processoMap.get(processoSelecionado);

            try {
                String[] comando = {"taskkill", "/PID", Integer.toString(pid), "/F"};
                Process processo = new ProcessBuilder(comando).start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(processo.getInputStream()));
                String linha;
                while ((linha = reader.readLine()) != null) {
                    System.out.println(linha);
                }
                processo.waitFor();
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        });
    }





    ///Janela de Cadastro de Tvs
    private static void placeComponentsCadastro(JFrame panelCadastroTvs) {
        panelCadastroTvs.setLayout(null);

        JLabel andarLabel = new JLabel("Andar:");
        andarLabel.setBounds(10, 30, 60, 25);
        panelCadastroTvs.add(andarLabel);

        JTextField andarText = new JTextField(20);
        andarText.setBounds(80, 30, 190, 25);
        panelCadastroTvs.add(andarText);

        JLabel setorLabel = new JLabel("Setor:");
        setorLabel.setBounds(10, 60, 60, 25);
        panelCadastroTvs.add(setorLabel);

        JTextField setorText = new JTextField(20);
        setorText.setBounds(80, 60, 190, 25);
        panelCadastroTvs.add(setorText);

        JLabel nomeLabel = new JLabel("Nome personalizado:");
        nomeLabel.setBounds(10, 90, 140, 25);
        panelCadastroTvs.add(nomeLabel);

        JTextField nomeText = new JTextField(20);
        nomeText.setBounds(150, 90, 120, 25);
        panelCadastroTvs.add(nomeText);

        JLabel txAtualiLabel = new JLabel("Taxa de atualização:");
        txAtualiLabel.setBounds(10, 120, 140, 25);
        panelCadastroTvs.add(txAtualiLabel);

        JTextField txAtualiText = new JTextField(20);
        txAtualiText.setBounds(150, 120, 120, 25);
        panelCadastroTvs.add(txAtualiText);

        JButton cadTvButton = new JButton("Cadastrar Televisão");
        cadTvButton.setBounds(70, 160, 180, 25);
        panelCadastroTvs.add(cadTvButton);

        TelevisaoDAO televisaoDAO = new TelevisaoDAO();
        ServicosLisync servicosLisync = new ServicosLisync();
        Ambiente ambiente = new Ambiente();
        Looca looca = new Looca();
        AmbienteDAO ambienteDAO = new AmbienteDAO();
        cadTvButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String andar = andarText.getText();
                String setor = setorText.getText();
                String nome = nomeText.getText();
                Integer taxaAtualizacao = Integer.parseInt(txAtualiText.getText());

                String hostName = looca.getRede().getParametros().getHostName();

                servicosLisync.cadastrarAmbiente(setor, andar, usuarioAutenticado.getFkEmpresa());
                servicosLisync.cadastrarNovaTelevisao(nome, ambienteDAO.getIdpAndarSetor(andar, setor), taxaAtualizacao);
                System.out.println(andar);
                Televisao televisao = televisaoDAO.buscarTvPeloEndereco(hostName);
                servicosLisync.cadastrarComponentes(televisao);

                JFrame panelTvs = new JFrame("Janela de Encerrar processo");
                panelTvs.setSize(800, 300);
                panelTvs.setVisible(true);
                centerFrameOnScreen(panelTvs);
                panelTvs.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                panelCadastroTvs.dispose();

                placeComponentsPanelTvs(panelTvs);
            }
        });
    }



}