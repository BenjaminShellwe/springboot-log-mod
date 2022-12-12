package io.springboot.log.web.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@RequestMapping
@RestController
public class LogController {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogController.class);

	@GetMapping(value = { "/", "/index"})
	public ModelAndView index () {
		return new ModelAndView("index/index");
	}

	@GetMapping("/log")
	@CrossOrigin(origins = {"http://localhost","http://192.168.40.254:8080"})
	public Object log () {

//		返回以下信息数据到简易控制台

		LOGGER.info("日志消息：查询成功");
		LOGGER.warn("日志消息：数据有误");
		LOGGER.error("日志消息：查询失败");


//		返回以下信息数据到日志内容
		Map<String, Object> retVal = new HashMap<>();
		retVal.put("success", Boolean.TRUE);

		return ResponseEntity.ok(retVal);
	}

	@Scheduled(cron="0/180 * *  * * ? ")
	public void JqcaseSearch() {
		try {
			LOGGER.info("连接链路检查 3分/次");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}




