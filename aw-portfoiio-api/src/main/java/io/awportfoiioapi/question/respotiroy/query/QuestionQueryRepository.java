package io.awportfoiioapi.question.respotiroy.query;

import io.awportfoiioapi.question.entity.Question;

public interface QuestionQueryRepository {

    Boolean existsStep(Long portfolioId,Integer step);
    
    Boolean existsOrders(Long portfolioId, Integer step, Integer order);
    
    Question findByPortfolioStep(Long portfolioId, Integer step);
    
}
