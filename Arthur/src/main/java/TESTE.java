import com.github.britooo.looca.api.core.Looca;
import dao.ComponenteDAO;
import dao.ProcessoDAO;
import dao.TelevisaoDAO;
import models.Componente;
import models.Processo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TESTE {
    public static void main(String[] args) {
        Componente componente = new Componente();
        ComponenteDAO componenteDAO = new ComponenteDAO();
        TelevisaoDAO televisaoDAO = new TelevisaoDAO();
        ProcessoDAO processoDAO = new ProcessoDAO();
        Looca looca = new Looca();
        String hostName = looca.getRede().getParametros().getHostName();

        System.out.println(componenteDAO.buscarTipoComponentePorIdTv("CPU",1).get(0).getIdComponente());


        Map<String, Integer> processoMap = ProcessoDAO.buscarProcessosNomeID(hostName);

        for (Map.Entry<String, Integer> entry : processoMap.entrySet()) {
            String nomeDoProcesso = entry.getKey();
            Integer pidDoProcesso = entry.getValue();

            System.out.println("Nome do processo: " + nomeDoProcesso + ", PID: " + pidDoProcesso);
        }

    }
}
