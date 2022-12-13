package io.springboot.log.web.controller;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.Channel;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class LinuxController {

    Session session = null;
    Channel channel = null;
    private static final Logger logger = LoggerFactory.getLogger(LinuxController.class);
    @RequestMapping("/1")
    @ResponseBody
    public String hello() {
        getConnect();
        getDisconnect();
        return "shellwe is programing!";
    }

    public void getConnect() {
        String username = "fps";
        String password = "fps123456";
        String host = "172.168.199.75";
        int port = 1221;
//        String username = "shellwe_ubuntu";
//        String password = "shellwe";
//        String host = "172.19.45.211";
//        int port = 22;



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

//            执行命令
            executeCommand("tail -5  /www/FPS_SERVER/fps-apihk/logs/log_all.log");
            // 获取连接结果
            result = session.isConnected();

//            System.out.println(result);


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

    /**
     *   主动断开链接
     */
    public void getDisconnect() {
        session.disconnect();
        logger.info("[SSH连接]主动断开目标Linux连接");
    }

    /**
     * 执行Linux命令
     */
    public String executeCommand(String command) {
        ChannelExec channelExec = null;
        BufferedReader inputStreamReader = null;
        BufferedReader errInputStreamReader = null;
        StringBuilder infoLog = new StringBuilder("");
        StringBuilder errorLog = new StringBuilder("");

        try {
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(command);
            // 执行命令
            channelExec.connect();
            // 获取标准输入流
            inputStreamReader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));
            // 获取标准错误输入流
            errInputStreamReader = new BufferedReader(new InputStreamReader(channelExec.getErrStream()));

            // 记录命令执行 log
            String line = null;
            while ((line = inputStreamReader.readLine()) != null) {
                String looking = "API_HK.{105,300} : ";
                Pattern pattern = Pattern.compile(looking);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {

                    System.out.println(matcher.toString());
                } else {
                    System.out.println("no!");
                }

                infoLog.append(line).append("\n");
            }


            // 记录命令执行错误 log
            String errorLine = null;
            while ((errorLine = errInputStreamReader.readLine()) != null) {
                errorLog.append(errorLine).append("\n");
            }

            // 输出 shell 命令执行日志
            System.out.println("exitStatus="
                    + channelExec.getExitStatus()
                    + ", openChannel.isClosed="
                    + channelExec.isClosed());
            System.out.println("命令执行完成，执行日志如下:先行隐藏");
//            System.out.println(infoLog.toString());
            System.out.println("命令执行完成，执行错误日志如下:先行隐藏");
//            System.out.println(errorLog.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (errInputStreamReader != null) {
                    errInputStreamReader.close();
                }

                if (channelExec != null) {
                    channelExec.disconnect();
                }
//                注释原因：完成执行命令后断开session
//                if (session != null) {
//                    session.disconnect();
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }




        return infoLog.toString();
    }
}
