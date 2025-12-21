package io.awportfoiioapi.question.service.impl;

import io.awportfoiioapi.question.respotiroy.QuestionRepository;
import io.awportfoiioapi.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
}
