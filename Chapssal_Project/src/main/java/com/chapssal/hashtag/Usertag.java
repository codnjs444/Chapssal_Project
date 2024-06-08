package com.chapssal.hashtag;

import com.chapssal.user.User;
import com.chapssal.video.Video;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usertag")
@Getter
@Setter
public class Usertag {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "UsertagNum")
	private int usertagNum;
	
	@ManyToOne
	@JoinColumn(name = "video", referencedColumnName = "videoNum")
	private Video video;
	
	@ManyToOne
	@JoinColumn(name = "user", referencedColumnName = "userNum")
	private User user;
}
