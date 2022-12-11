package io.springboot.log.web.controller;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.channels.Channel;
import java.util.Properties;

@Controller
public class LinuxController {

    Session session = null;
    Channel channel = null;
    private static final Logger logger = LoggerFactory.getLogger(LinuxController.class);
    @RequestMapping("/1")
    @ResponseBody
    public String hello() {
        System.out.println("hh");
        getConnect();

        return "shellwe is programing!";
    }

    public void getConnect() {
        String username = "fps";
        String password = "fps123456";
        String host = "172.168.199.75";
        int port = 1221;


        // 创建JSch对象
        JSch jSch = new JSch();

        boolean result = false;

        try {

            // 根据主机账号、ip、端口获取一个Session对象
            session = jSch.getSession(username, host, port);

            // 存放主机密码
            session.setPassword(password);

            Properties config = new Properties();

            // 去掉首次连接确认
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);

            // 超时连接时间为3秒
            session.setTimeout(3000);

            // 进行连接
            session.connect();

            // 获取连接结果
            result = session.isConnected();
            System.out.println(result);


        } catch (JSchException e) {
            logger.warn(e.getMessage());
        } finally {
            // 关闭jschSession流
            if (session != null && session.isConnected()) {
                session.disconnect();
                logger.info("[SSH连接]Linux连接重置");
            }
        }

        if (result) {
            logger.info("[SSH连接]目标Linux连接成功");
        } else {
            logger.error("[SSH连接]目标Linux连接失败");
        }

    }

    public void getDisconnect() {
        session.disconnect();
        logger.info("[SSH连接]主动断开目标Linux连接");
    }
}
