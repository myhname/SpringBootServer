package server.mine.servertest.mysql.bean;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class DocMapBeanConf {

    @Bean(name = "docMap")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Map<Integer, DocMapBean> getDocBean(){
        return new ConcurrentHashMap<>();
    }
}
