package org.javaup.ai;

import org.dromara.easyes.spring.annotation.EsMapperScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * @program: 潮声-ai智能服务项目。 添加 阿星不是程序员 微信，添加时备注 ai 来获取项目的完整资料 
 * @description: 潮声-ai智能服务项目启动类
 * @author: 阿星不是程序员
 **/
@EsMapperScan("org.javaup.ai.es.mapper")
@MapperScan("org.javaup.ai.mapper")
@SpringBootApplication
public class TidesAiCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(TidesAiCoreApplication.class, args);
    }

}
