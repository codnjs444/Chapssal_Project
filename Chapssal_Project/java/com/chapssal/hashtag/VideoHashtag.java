package com.chapssal.hashtag;

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
@Table(name = "videohashtag")
@Getter
@Setter
public class VideoHashtag {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "videoHashtagNum")
	private int videoHashtagNum;
	
	@ManyToOne
	@JoinColumn(name = "video", referencedColumnName = "videoNum")
	private Video video;
	
	@ManyToOne
	@JoinColumn(name = "hashtag", referencedColumnName = "hashtagNum")
	private Hashtag hashtag;
}