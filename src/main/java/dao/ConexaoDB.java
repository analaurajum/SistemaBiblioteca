package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConexaoDB {

    private static final String url = "jdbc:mysql://localhost:3306/sistema_biblioteca";
    private static final String usuario = "root";
    private static final String senha = "root";

    public static Connection conectar() {
        try {
            return DriverManager.getConnection(url, usuario, senha);
        } catch (Exception e) {
            System.out.println("Erro na conexão: " + e.getMessage());
            return null;
        }
    }
}