//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.janelas.Janela;
import dao.ComponenteDAO;
import dao.TelevisaoDAO;
import dao.UsuarioDAO;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import models.Usuario;
import services.Autenticacao;
import services.ServicosLisync;

public class Reset {
    public Reset() {
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        new Scanner(System.in);
        ServicosLisync servicosLisync = new ServicosLisync();
        Looca looca = new Looca();
        Autenticacao autenticacao = new Autenticacao();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        new TelevisaoDAO();
        new ComponenteDAO();
        Boolean cadastroValido = false;
        Usuario usuarioAutenticado = null;

        do {
            System.out.println("\n|----------- LOGIN -----------|\nInsira suas informacoes\nDigite 0 para voltar ao MENU\n");
            System.out.println("Digite seu e-mail: ");
            String email = input.next();
            if (email.equals("0")) {
                break;
            }

            System.out.println("Digite sua senha: ");
            String senha = input.next();
            if (senha.equals("0")) {
                break;
            }

            cadastroValido = autenticacao.validacaoLogin(email, senha);
            if (cadastroValido) {
                usuarioAutenticado = usuarioDAO.buscarCreedenciasUsuario(email, senha);
                System.out.println("Bem vindo %s!".formatted(usuarioAutenticado.getNome()));
                System.out.println("\nDados cliente: \n" + usuarioAutenticado.toString());
            } else {
                System.out.println("Email ou senha incorretos!");
                System.out.println("Realizar nova tentativa? (Digite 'N' Para cancelar ou 'S' para prosseguir) ");
                String prosseguir = input.next();
                if (prosseguir.equalsIgnoreCase("N")) {
                    System.out.println("Login encerrado");
                    System.exit(0);
                }
            }
        } while(!cadastroValido);

        servicosLisync.atualizarUsuario(usuarioAutenticado);
        servicosLisync.atualizarEmpresaDoUsuario(usuarioAutenticado.getFkEmpresa());
        List<Janela> janelas = looca.getGrupoDeJanelas().getJanelas();
        Iterator var15 = janelas.iterator();

        while(var15.hasNext()) {
            Janela janela = (Janela)var15.next();
            if (janela.getTitulo() != null && !janela.getTitulo().isEmpty()) {
                System.out.println(janela);
            }
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Insira o PID da janela que deseja fechar: ");
        long pidJanela = scanner.nextLong();
        scanner.close();
        fecharJanelaPorPID(pidJanela);
    }

    public static void fecharJanelaPorPID(long pid) {
        Looca looca = new Looca();
        List<Janela> janelas = looca.getGrupoDeJanelas().getJanelas();
        Iterator var4 = janelas.iterator();

        Janela janela;
        do {
            if (!var4.hasNext()) {
                System.out.println("Não foi encontrada uma janela com o PID especificado.");
                return;
            }

            janela = (Janela)var4.next();
        } while(janela.getPid() != pid);

        encerrarProcesso(janela.getPid());
    }

    private static void encerrarProcesso(long janelaId) {
        try {
            Runtime.getRuntime().exec("taskkill /IM " + janelaId + " /F");
            System.out.println("Janela fechada com sucesso.");
        } catch (Exception var3) {
            System.out.println("Erro ao fechar a janela: " + var3.getMessage());
        }

    }
}
