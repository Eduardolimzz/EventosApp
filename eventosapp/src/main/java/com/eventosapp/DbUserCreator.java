import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class DbUserCreator implements CommandLineRunner {

    private final DataSource dataSource;

    public DbUserCreator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE USER 'edu'@'%' IDENTIFIED BY 'senha123'");
            stmt.execute("GRANT ALL PRIVILEGES ON railway.* TO 'edu'@'%'");
            stmt.execute("FLUSH PRIVILEGES");

            System.out.println("Usuário 'edu' criado com sucesso.");
        }
    }
}
