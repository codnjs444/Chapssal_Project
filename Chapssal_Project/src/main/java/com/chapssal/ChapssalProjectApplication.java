package com.chapssal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// 스케줄링 작업을 활성화 하기 위해 (특정 시간에 user 테이블의 vote 칼럼 0으로 초기화 작업 및 selectedtopic 테이블 비우기)
// @EnableScheduling 어노테이션 추가
@SpringBootApplication
@EnableScheduling
public class ChapssalProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChapssalProjectApplication.class, args);
	}

}
