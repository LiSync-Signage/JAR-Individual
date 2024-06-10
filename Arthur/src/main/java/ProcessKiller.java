import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.processos.Processo;
import dao.ComandoDAO;
import models.Comando;
import models.Usuario;
import services.ServicosLisync;

import java.util.*;

public class ProcessKiller {
    public void killProcess(Integer fktelevisao) {
        String opcao = "";
        String processoEscolhido = "";
        String comandoExecutado = "";

        do {
            Looca looca = new Looca();
            List<Processo> processos = looca.getGrupoDeProcessos().getProcessos();

            List<Processo> maioresProcessosCPU = new ArrayList<>();
            List<Processo> maioresProcessosRam = new ArrayList<>();


            Collections.sort(processos, Comparator.comparingDouble(Processo::getUsoMemoria).reversed());

            for (int i = 0; i < Math.min(processos.size(), 8); i++) {
                maioresProcessosRam.add(processos.get(i));
            }

            Collections.sort(processos, Comparator.comparingDouble(Processo::getUsoCpu).reversed());

            for (int i = 0; i < Math.min(processos.size(), 8); i++) {
                maioresProcessosCPU.add(processos.get(i));
            }

            System.out.println("Maiores processos em uso de MemoriaRAM%");
            for (int i = 0; i < maioresProcessosRam.size(); i++) {
                Processo processoAtual = maioresProcessosRam.get(i);

                System.out.println("""
                        |----------- Processo %d -----------|
                        Nome: %s
                        Pid: %d
                        CPU: %.2f
                        Memória: %.2f
                         """.formatted(i, processoAtual.getNome(), processoAtual.getPid(),
                        processoAtual.getUsoCpu(), processoAtual.getUsoMemoria()));


            }


            System.out.println("Maiores processos em uso de CPU%");
            for (int i = 0; i < maioresProcessosCPU.size(); i++) {
                Processo processoAtual = maioresProcessosCPU.get(i);

                System.out.println("""
                        |----------- Processo %d -----------|
                        Nome: %s
                        Pid: %d
                        CPU: %.2f
                        Memória: %.2f
                         """.formatted(i, processoAtual.getNome(), processoAtual.getPid(),
                        processoAtual.getUsoCpu(), processoAtual.getUsoMemoria()));

            }
//        for (Processo processo : processos) {
//            System.out.println(processo);
//        }

            Scanner input = new Scanner(System.in);

            try {

                input = new Scanner(System.in);
                System.out.println("Insira o PID do processo que deseja finalizar:\n");
                processoEscolhido = input.nextLine();
                String[] comandoLinux = {"/bin/sh", "-c", "kill -9 " + processoEscolhido};
                String[] comandoWindows = {"cmd.exe", "/c", "taskkill", "/F", "/PID", processoEscolhido};
                try {
                    Process killProcess = new ProcessBuilder(comandoLinux).start();
                    killProcess.waitFor();
                    comandoExecutado = "kill - 9 " + processoEscolhido;
                    System.out.println("Processo com PID " + processoEscolhido + " encerrado.");
                    Comando comando = new Comando(comandoExecutado ,"Processo com PID " + processoEscolhido + " encerrado.", fktelevisao);
                    ServicosLisync servicosLisync =  new ServicosLisync();
                    servicosLisync.inserirComandos(comando);

                } catch (Exception c) {
                    try {
                        Process killProcessWindows = new ProcessBuilder(comandoWindows).start();
                        killProcessWindows.waitFor();
                        comandoExecutado = "taskkill /F /PID " + processoEscolhido;
                        System.out.println("Processo com PID " + processoEscolhido + " encerrado.");
                        Comando comando = new Comando(comandoExecutado ,"Processo com PID " + processoEscolhido + " encerrado.", fktelevisao);
                        ServicosLisync servicosLisync =  new ServicosLisync();
                        servicosLisync.inserirComandos(comando);
                    } catch (Exception c1) {
                        c.printStackTrace();
                        c1.printStackTrace();
                        System.out.println("Falha ao encerrar processo");
                    }

                }



            } catch (Exception e) {
                e.printStackTrace();
            }


            System.out.println("deseja encerrar mais processos?\n ('s' para sim e 'n' para não) ");
            opcao = input.next();

        } while (!opcao.equalsIgnoreCase("n"));

        System.out.println("programa encerrado");

    }
}
