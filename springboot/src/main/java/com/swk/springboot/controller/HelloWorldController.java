package com.swk.springboot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @RestController Controller里的方法都以json格式输出
 * @author fuyuw
 * 2018年9月12日 下午10:13:36
 */
@RestController
public class HelloWorldController {

	@RequestMapping("/hello")
	public String index(){
		return "hello world";
	}
}
