package com.chapssal.comment;

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
}
