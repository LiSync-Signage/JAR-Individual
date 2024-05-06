package dao;

import models.Televisao;
import org.LiSync.conexao.ConexaoMySQL;
import models.Processo;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessoDAO {
    public void salvarProcesso(Processo processo) {
        ConexaoMySQL conexao = new ConexaoMySQL();
        JdbcTemplate con = conexao.getconexaoMySqlLocal();

        String sql = "INSERT INTO Processo (pid, nome, dataHora, fkTelevisao) VALUES (?, ?, ?, ?)";

        try {
            con.update(sql, processo.getPid(), processo.getNome(), processo.getDataHora(), processo.getFkTelevisao());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.getDataSource().getConnection().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void salvarVariosProcessos(List<Processo> processos) {
        ConexaoMySQL conexao = new ConexaoMySQL();
        JdbcTemplate con = conexao.getconexaoMySqlLocal();

        String sql = "INSERT INTO Log (pid, nomeProcesso, dataHora, fkComponente, valor) VALUES (?, ?, ?, ?, ?)";

        try {
            for (Processo processo : processos) {
                con.update(sql, processo.getPid(), processo.getNome(), processo.getDataHora(), processo.getIdComponente(), processo.getValor());
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (con != null) {
                try {
                    con.getDataSource().getConnection().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    public Integer numeroDeProcessos() {
        ConexaoMySQL conexao = new ConexaoMySQL();
        JdbcTemplate con = conexao.getconexaoMySqlLocal();

        String sql = "SELECT COUNT(idProcesso) FROM Processo";

        try {


            return con.queryForObject(sql, Integer.class);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (con != null) {
                try {
                    con.getDataSource().getConnection().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public List<Processo> buscarProcessos(String  hostname) {
        ConexaoMySQL conexao = new ConexaoMySQL();
        JdbcTemplate con = conexao.getconexaoMySqlLocal();

        String sql = "Select * FROM log JOIN Componente ON log.fkComponente = Componente.idComponente JOIN Televisao ON Componente.fkTelevisao = Televisao.idTelevisao WHERE Televisao.hostName = ?;";

        try {
            List<Processo> listProcessos = con.query(sql, new BeanPropertyRowMapper<>(Processo.class),hostname);
            return listProcessos;
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (con != null) {
                try {
                    con.getDataSource().getConnection().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }



    public static Map<String, Integer> buscarProcessosNomeID(String hostname) {
        ConexaoMySQL conexao = new ConexaoMySQL();
        JdbcTemplate con = conexao.getconexaoMySqlLocal();

        String sql = "SELECT log.nomeProcesso AS nome, log.pid AS pid " +
                "FROM log " +
                "JOIN Componente ON log.fkComponente = Componente.idComponente " +
                "JOIN Televisao ON Componente.fkTelevisao = Televisao.idTelevisao " +
                "WHERE Televisao.hostName = ?";

        Map<String, Integer> processoMap = new HashMap<>();
        try {
            con.query(sql, (rs, rowNum) -> {
                String nome = rs.getString("nome");
                int pid = rs.getInt("pid");
                processoMap.put(nome, pid);
                return null;
            }, hostname);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.getDataSource().getConnection().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return processoMap;
    }
}
