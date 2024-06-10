package services;

import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.memoria.Memoria;
import com.github.britooo.looca.api.group.processos.Processo;
import com.github.britooo.looca.api.group.janelas.Janela;
import com.github.britooo.looca.api.util.Conversor;
import conexao.ConexaoSlack;
import dao.*;
import models.*;
import plano.Plano;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServicosLisync {
    Looca looca = new Looca();
    UsuarioDAO usuarioDAO = new UsuarioDAO();
    EmpresaDAO empresaDAO = new EmpresaDAO();
    TelevisaoDAO televisaoDAO = new TelevisaoDAO();
    ComponenteDAO componenteDAO = new ComponenteDAO();
    ProcessoDAO processoDAO = new ProcessoDAO();
    JanelaDAO janelaDAO = new JanelaDAO();

    LogComponenteDAO logComponenteDAO = new LogComponenteDAO();
//
    public Boolean televisaoNova(String hostname) {
        Integer televisaoExiste = televisaoDAO.contarPorEndereco(hostname);
        return televisaoExiste == 0;
    }
//
    public Integer qtdTelevisoesDisponiveis(Integer fkEmpresa){
        Plano plano = empresaDAO.buscarPorPlano(fkEmpresa).getPlano();
        return plano.getQtdTvs();
    }
//
    public Integer contarTvsPorFkEmpresa(Integer fkEmpresa) {
        Integer qtdTvs = empresaDAO.contarPorEmpresa(fkEmpresa);
        return qtdTvs;
    }
//
    public void atualizarEmpresaDoUsuario(Integer fkEmpresa) {
        Empresa empresaUsuario = empresaDAO.buscarEmpresa(fkEmpresa);
        try {
            empresaDAO.atualizarEmpresaLocalSQLServer(empresaUsuario);
            empresaDAO.atualizarEmpresaLocal(empresaUsuario);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("erro na Atualização de usuário");
        }
    }
//
    public void atualizarUsuario(Usuario usuarioLogado) {
        try {
            usuarioDAO.atualizarUsuarioLocalSQLServer(usuarioLogado);
            usuarioDAO.atualizarUsuarioLocal(usuarioLogado);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("erro na Atualização de usuário");
        }


    }
//
    public void cadastrarAmbiente(String setor, String andar, Integer fkEmpresa) {
        models.Ambiente ambiente= new models.Ambiente(setor, andar, fkEmpresa);
        registrarAmbiente(ambiente);
    }
    public void registrarAmbiente (Ambiente ambiente) {
        try {
            AmbienteDAO.insertAmbienteSQLServer(ambiente);
            AmbienteDAO.insertAmbiente(ambiente);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("erro ao cadastrar Ambiente");
        }
    }
//
    public void cadastrarNovaTelevisao( String nome, Integer fkAmbiente, Integer taxaAtualizacao
                            ) {

        Televisao televisao = new Televisao(
                nome,
                fkAmbiente,
                taxaAtualizacao,
                looca.getRede().getParametros().getHostName()
        );

        System.out.println(televisao.getFkAmbiente());

        try {
            televisaoDAO.registrarSQLServer(televisao);
            televisaoDAO.registrar(televisao);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("erro ao cadastrar televisão");
        }

        System.out.println("Nova televisão adicionada! \n");
    }
//
    public void cadastrarComponentes(Televisao televisao) {
        televisao.setComponentes(new ArrayList<>());

        ComponenteTv cpu = new ComponenteTv(looca.getProcessador().getNome(),
                looca.getProcessador().getId(), "CPU", televisao.getIdTelevisao());
        televisao.registarComponenteTv(cpu);

        try {
            componenteDAO.registarComponenteSQLServer(cpu);
            componenteDAO.registarComponente(cpu);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("erro ao cadastrar CPU");
        }


        Disco instanciaDisco = looca.getGrupoDeDiscos().getDiscos().get(0);
        ComponenteTv disco = new ComponenteTv(instanciaDisco.getModelo(), instanciaDisco.getSerial(),
                "Disco", televisao.getIdTelevisao());
        televisao.registarComponenteTv(disco);

        try {
            componenteDAO.registarComponenteSQLServer(disco);
            componenteDAO.registarComponente(disco);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("erro ao cadastrar Disco");
        }

        Long memoriaTotal = looca.getMemoria().getTotal();
        ComponenteTv memoriaRam = new ComponenteTv(String.format("Memória RAM %s", Conversor.formatarBytes(memoriaTotal)),
                "Não existe", "RAM", televisao.getIdTelevisao());
        televisao.registarComponenteTv(memoriaRam);

        try {
            componenteDAO.registarComponenteSQLServer(memoriaRam);
            componenteDAO.registarComponente(memoriaRam);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("erro ao cadastrar Memoria ram");
        }

    }

//
    public String monitoramentoComponentes(Componente componente, Televisao televisao) throws IOException {
        Double valor = 0.0;
        ConexaoSlack conexaoSlack = new ConexaoSlack();

        if (componente instanceof  Cpu) {
                valor = looca.getProcessador().getUso();
                LogComponente logComponente = monitoramentoLogComponente(componenteDAO.buscarTipoComponentePorIdTv("CPU", televisao.getIdTelevisao()).get(0).getIdComponente(), valor);
                logComponenteDAO.salvarLogComponenteIndividual(logComponente);
                conexaoSlack.alertMessageCPU(valor);

        }else if (componente instanceof models.Disco) {

                List<Disco> discos = looca.getGrupoDeDiscos().getDiscos();
                Disco discoPrincipal = discos.get(0);

                for (Disco discoAtual : discos) {
                    discoPrincipal = discoAtual;
                }

                valor = (discoPrincipal.getBytesDeEscritas().doubleValue()
                        / discoPrincipal.getTamanho().doubleValue()) * 100.;
                LogComponente logComponente2 = monitoramentoLogComponente(componenteDAO.buscarTipoComponentePorIdTv("Disco", televisao.getIdTelevisao()).get(0).getIdComponente(), valor);
                logComponenteDAO.salvarLogComponenteIndividual(logComponente2);
                conexaoSlack.alertMessageDisco(valor);

        }else {
                valor = (looca.getMemoria().getEmUso().doubleValue() / (looca.getMemoria().getTotal().doubleValue())) * 100.;

                LogComponente logComponente1 = monitoramentoLogComponente(componenteDAO.buscarTipoComponentePorIdTv("RAM", televisao.getIdTelevisao()).get(0).getIdComponente(), valor);

                logComponenteDAO.salvarLogComponenteIndividual(logComponente1);
                conexaoSlack.alertMessageRAM(valor);
        }
        return "";
    }

//
    public void registrarProcessos(List<models.Processo> listaProcessos) {
        try {
            processoDAO.salvarVariosProcessosSQLServer(listaProcessos);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao salvar LogComponente no servidor principal");
        } finally {
            try{
                processoDAO.salvarVariosProcessos(listaProcessos);
            }catch (Exception c) {
                c.printStackTrace();
                System.out.println("Erro ao salvar LogComponente no servidor local");
            }
        }

    }
    public void registrarLogComponente(List<models.LogComponente> listaLogComponente) {

        try {
            logComponenteDAO.salvarLogComponenteSQLServer(listaLogComponente);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao salvar LogComponente no servidor principal");
        } finally {
            try{
                logComponenteDAO.salvarLogComponente(listaLogComponente);
            }catch (Exception c) {
                c.printStackTrace();
                System.out.println("Erro ao salvar LogComponente no servidor local");
            }
        }
    }


//

    public models.LogComponente monitoramentoLogComponente(Integer fkComponente, Double valor  ) {
        models.LogComponente logComponente = new models.LogComponente(fkComponente, valor);
        return logComponente;
    }
//
    public models.Processo monitoramentoProcesso(Processo processoMonitorado, Integer idComponente, Double valor) {
        models.Processo processo = new models.Processo(processoMonitorado.getPid(),
                processoMonitorado.getNome(), idComponente, valor);
        return processo;
    }
//
    public models.Janela monitoramentoJanela(Janela janelaMonitorada, Integer idTelevisao) {
        Integer visivel = (janelaMonitorada.isVisivel() ? 1 : 0);
        models.Janela janela = new models.Janela(janelaMonitorada.getPid().intValue(), janelaMonitorada.getComando(),
                janelaMonitorada.getTitulo(), visivel,
                idTelevisao);
        return  janela;
    }
//
    public void salvarJanelas(List<models.Janela> janelas) {
        janelaDAO.salvarVariasJanelas(janelas);

        try {
            janelaDAO.salvarVariasJanelasSQLServer(janelas);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao salvar janelas no servidor principal");
        } finally {
            try{
                janelaDAO.salvarVariasJanelas(janelas);
            }catch (Exception c) {
                c.printStackTrace();
                System.out.println("Erro ao salvar Janelas no servidor local");
            }
        }

    }



    public void atualizarComando(Integer idComando, String comando, String resposta, Integer fkTelevisao) {
        models.Comando comandoObj= new models.Comando(idComando ,comando,resposta , fkTelevisao);

        try {
            ComandoDAO.updateComandoSQLServer(comandoObj);
            ComandoDAO.updateComando(comandoObj);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("erro ao retornar resposta do comando");
        }
    }

    public void inserirComandos(Comando comando){
        ComandoDAO comandoDAO = new ComandoDAO();

        try{
            comandoDAO.insertComandoSQLServer(comando);
        }catch (Exception e){
            e.printStackTrace();

        }finally {
            try {
                comandoDAO.insertComando(comando);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }



}
