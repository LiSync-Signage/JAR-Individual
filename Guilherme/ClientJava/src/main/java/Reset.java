import java.util.List;
import java.util.Scanner;
import com.github.britooo.looca.api.group.janelas.Janela;
import com.github.britooo.looca.api.core.Looca;
import services.Autenticacao;
import services.ServicosLisync;

import dao.TelevisaoDAO;
import dao.UsuarioDAO;
import models.Componente;
import models.Televisao;
import models.Usuario;
import services.Autenticacao;
import services.ServicosLisync;

import dao.ComponenteDAO;

public class Reset {




    public static void main(String[] args) {


        Scanner input = new Scanner(System.in);
        Scanner inputNext = new Scanner(System.in);
        ServicosLisync servicosLisync = new ServicosLisync();
        Looca looca = new Looca();
        Autenticacao autenticacao = new Autenticacao();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        TelevisaoDAO televisaoDAO = new TelevisaoDAO();
        ComponenteDAO componenteDAO = new ComponenteDAO();




        String prosseguir;
        Boolean cadastroValido = false;
        Usuario usuarioAutenticado = null;

        do {
            System.out.println("""
                    \n|----------- LOGIN -----------|
                    Insira suas informações
                    Digite 0 para voltar ao MENU
                    """);

            System.out.println("Digite seu e-mail: ");
            String email = input.next();
            if (email.equals("0")) break;

            System.out.println("Digite sua senha: ");
            String senha = input.next();
            if (senha.equals("0")) break;

            cadastroValido = autenticacao.validacaoLogin(email, senha);
            if (cadastroValido) {
                usuarioAutenticado = usuarioDAO.buscarCreedenciasUsuario(email, senha);
                System.out.println("Bem vindo %s!".formatted(usuarioAutenticado.getNome()));
                System.out.println("\nDados cliente: \n" + usuarioAutenticado.toString());
            } else {
                System.out.println("Email ou senha incorretos!");
                System.out.println("Realizar nova tentativa? (Digite 'N' Para cancelar ou 'S' para prosseguir) ");
                prosseguir = input.next();
                if (prosseguir.equalsIgnoreCase("N")) {
                    System.out.println("Login encerrado");
                    System.exit(0);
                }
            }
        } while (!cadastroValido);

        servicosLisync.atualizarUsuario(usuarioAutenticado);
        servicosLisync.atualizarEmpresaDoUsuario(usuarioAutenticado.getFkEmpresa());
        List<Janela> janelas = looca.getGrupoDeJanelas().getJanelas();

        long pidJanela;

        for (Janela janela : janelas) {
            System.out.println(janela);
        }
            Scanner scanner = new Scanner(System.in);
            System.out.print("Insira o PID da janela que deseja fechar: ");
            pidJanela = scanner.nextLong();
            scanner.close();

        fecharJanelaPorPID(pidJanela);
    }

    public static void fecharJanelaPorPID(long pid) {
        Looca looca = new Looca();
        List<Janela> janelas = looca.getGrupoDeJanelas().getJanelas();

        for (Janela janela : janelas) {
            if (janela.getPid() == pid) {
                encerrarProcesso(janela.getPid());
                return;
            }
        }

        System.out.println("Não foi encontrada uma janela com o PID especificado.");
    }

    private static void encerrarProcesso(long janelaId) {
        try {
            Runtime.getRuntime().exec("kill -9 " + janelaId);
            // Runtime.getRuntime().exec("kill " + janelaId);
            System.out.println("Janela fechada com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao fechar a janela: " + e.getMessage());
        }
    }
}
