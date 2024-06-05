package com.chapssal.comment;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RCommentService {
    private final RCommentRepository rCommentRepository;

    @Transactional
    public RComment create(RComment rComment) {
        return rCommentRepository.save(rComment);
    }
    
    @Transactional(readOnly = true)
    public List<RComment> findByCommentNum(int commentNum) {
        return rCommentRepository.findByCommentCommentNum(commentNum);
    }
}
