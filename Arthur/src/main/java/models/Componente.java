package models;

import java.util.ArrayList;
import java.util.List;

public abstract class Componente {
    private Integer idComponente;
    private String modelo;
    private String identificador;
    private Integer fkTelevisao;
    private List<Monitoramento> monitoramentos;

    public Componente() {}

    public Componente(String modelo, String identificador, Integer fkTelevisao) {
        this.modelo = modelo;
        this.identificador = identificador;
        this.fkTelevisao = fkTelevisao;
        this.monitoramentos = new ArrayList<>();
    }

    public Integer getIdComponente() {
        return idComponente;
    }

    public void setIdComponente(Integer idComponente) {
        this.idComponente = idComponente;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }



    public Integer getFkTelevisao() {
        return fkTelevisao;
    }

    public void setFkTelevisao(Integer fkTelevisao) {
        this.fkTelevisao = fkTelevisao;
    }

    public List<Monitoramento> getMonitoramentos() {
        return monitoramentos;
    }

    public void setMonitoramentos(List<Monitoramento> monitoramentos) {
        this.monitoramentos = monitoramentos;
    }

    @Override
    public String toString() {
        return "Componente{" +
                "idComponente=" + idComponente +
                ", modelo='" + modelo + '\'' +
                ", identificador='" + identificador + '\'' +
                ", fkTelevisao=" + fkTelevisao +
                ", monitoramentos=" + monitoramentos +
                '}';
    }
}
