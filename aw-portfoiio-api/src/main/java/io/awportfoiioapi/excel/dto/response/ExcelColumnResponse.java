package io.awportfoiioapi.excel.dto.response;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ExcelColumnResponse {

    private Long questionId;
    
    private Long optionsId;
    
    private Integer optionsOrders;
    
    private String optionsType;
    
    private String column;
    
    
    
    @QueryProjection
    public ExcelColumnResponse(Long questionId, Long optionsId, Integer optionsOrders, String optionsType, String column) {
        this.questionId = questionId;
        this.optionsId = optionsId;
        this.optionsOrders = optionsOrders;
        this.optionsType = optionsType;
        this.column = column;
    }
}
