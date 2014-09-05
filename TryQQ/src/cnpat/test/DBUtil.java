package cnpat.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static String driver = "com.mysql.jdbc.Driver";

//     private static String username = "root";
//    
//     private static String password = "root";
//    
//     private static String url = "jdbc:mysql://localhost:3306/test";

    // private static String ip = System.getenv("JAE_MYSQL_IP");
    //
    // private static String port = System.getenv("JAE_MYSQL_PORT");
    //
    // private static String dbName = System.getenv("JAE_MYSQL_DBNAME");
    //
    // private static String username = System.getenv("JAE_MYSQL_USERNAME");
    //
    // private static String password = System.getenv("JAE_MYSQL_PASSWORD");
    //
    // private static String encoding = System.getenv("JAE_MYSQL_ENCODING");
    // private static String encoding = System.getenv("JAE_MYSQL_ENCODING");
    //BAE2.0
//    private static String ip = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_ADDR_SQL_IP);
//
//    private static String port = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_ADDR_SQL_PORT);
//
//    private static String dbName = "dTpQCpjoewXcqsfogOon";
//
//    private static String username = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_AK);
//
//    private static String password = BaeEnv.getBaeHeader(BaeEnv.BAE_ENV_SK);
    //BAE1.0
    private static String ip = "127.0.0.1";
    private static String port = "3306";
    private static String dbName = "pmp";
    private static String username = "root";
    private static String password = "root";
    

    private static String url = "jdbc:mysql://" + ip + ":" + port + "/" + dbName
            + "?useUnicode=true&characterEncoding=UTF-8";

    // 得到数据库连接
    public static Connection getConnection() throws InstantiationException, IllegalAccessException,
            ClassNotFoundException, SQLException {
        Class.forName(driver).newInstance();
        return DriverManager.getConnection(url, username, password);
    }
    
    
}
