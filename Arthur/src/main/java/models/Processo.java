package models;

import java.time.LocalDateTime;

public class Processo {
    private Integer idProcesso;
    private Integer pid;
    private String nomeProcesso;
    private Integer fkTelevisao;
    private LocalDateTime dataHora;
    private Integer idComponente;
    private Double valor;

    public Processo() {}



    public Processo(Integer pid, String nomeProcesso, Integer idComponente, Double valor) {
        this.pid = pid;
        this.nomeProcesso = nomeProcesso;
        this.idComponente = idComponente;
        this.dataHora = LocalDateTime.now();
        this.valor = valor;
    }

    public Integer getIdComponente() {
        return idComponente;
    }

    public void setIdComponente(Integer idComponente) {
        this.idComponente = idComponente;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Integer getIdProcesso() {
        return idProcesso;
    }

    public void setIdProcesso(Integer idProcesso) {
        this.idProcesso = idProcesso;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getNome() {
        return nomeProcesso;
    }

    public void setNome(String nome) {
        this.nomeProcesso = nome;
    }

    public Integer getFkTelevisao() {
        return fkTelevisao;
    }

    public void setFkTelevisao(Integer fkTelevisao) {
        this.fkTelevisao = fkTelevisao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    @Override
    public String toString() {
        return "Processo{" +
                "idProcesso=" + idProcesso +
                ", pid=" + pid +
                ", nome='" + nomeProcesso + '\'' +
                ", fkTelevisao=" + fkTelevisao +
                ", dataHora=" + dataHora +
                ", idComponente=" + idComponente +
                ", valor=" + valor +
                '}';
    }
}
