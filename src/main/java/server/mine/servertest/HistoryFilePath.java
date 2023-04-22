package server.mine.servertest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class HistoryFilePath implements CommandLineRunner {

    @Value("${historyFile.path}")
    private String historyFilePath;

    @Override
    public void run(String... args) throws Exception {
        File file = new File(historyFilePath);
        boolean flag = file.isDirectory();
        if(!flag){
            if(!file.mkdirs()){
                throw new IOException("历史记录文件夹不存在，且创建失败");
            }
        } else if(!file.canRead() || !file.canWrite()){
            throw new IOException("您没有历史记录文件夹的读写权限");
        }
    }
}
