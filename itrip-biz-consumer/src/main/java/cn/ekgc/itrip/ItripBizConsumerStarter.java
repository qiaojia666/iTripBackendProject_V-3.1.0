package cn.ekgc.itrip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * <b>主功能子项目Consumer启动类</b>
 * @author Qiaojia
 * @version 3.1.0 2019-12-12
 * @since 3.1.0
 */
@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
public class ItripBizConsumerStarter {
    public static void main( String[] args ) {
        SpringApplication.run(ItripBizConsumerStarter.class, args);
    }
}