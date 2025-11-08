package files.custom;

import java.sql.SQLException;
import javax.sql.DataSource;
import java.sql.Connection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
public class UsersController {

    private DataSource dataSource;

    @GetMapping("")
    public String main() {
        return "Users Microservice";
    }

    @PostMapping("lol")
    public String createUser(){
        return "s";
    }





    @GetMapping("/check-db")
    public String checkDb() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            return "Connected to: " + conn.getMetaData().getURL();
        }
    }
}