package conexao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class ConexaoSQLServer {
    private JdbcTemplate conexaoSqlServerLocal;

    public ConexaoSQLServer() {
        BasicDataSource dataSource = new BasicDataSource();

        // Configuração da conexão com SQL Server usando autenticação do SQL Server
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setUrl("jdbc:sqlserver://localhost:1433;databaseName=LysincDB;");
        dataSource.setUsername("root"); // Substitua pelo seu nome de usuário
        dataSource.setPassword("urubu100");   // Substitua pela sua senha

        conexaoSqlServerLocal = new JdbcTemplate(dataSource);
    }

    public JdbcTemplate getConexaoSqlServerLocal() {
        return conexaoSqlServerLocal;
    }

}