import java.util.List;
import java.util.Scanner;
import com.github.britooo.looca.api.group.janelas.Janela;
import com.github.britooo.looca.api.core.Looca;

public class Reset {
    public static void main(String[] args) {
        Looca looca = new Looca();
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
            Runtime.getRuntime().exec("taskkill /F /T /PID " + janelaId);
            // Runtime.getRuntime().exec("kill " + janelaId);
            System.out.println("Janela fechada com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro ao fechar a janela: " + e.getMessage());
        }
    }
}
