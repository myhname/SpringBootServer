package server.mine.servertest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class SpringBootServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootServerApplication.class, args);

    }

}
