package io.awportfoiioapi.submission.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionPostRequest {
    
    private Long submissionId;
    
    private Long memberId;
    
    private Long portfolioId;
    
    private String response;
    
    
    private List<OptionFileRequest> optionFiles;
      
      public List<OptionFileRequest> getOptionFiles() {
          if (this.optionFiles == null) {
              this.optionFiles = new ArrayList<>();
          }
          return this.optionFiles;
      }
      
      @AllArgsConstructor
      @NoArgsConstructor
      @Data
      public static class OptionFileRequest {
          private Long optionsId;
          private Integer questionStep;
          private Integer questionOrder;
          private Long deleteFileId;
          private List<MultipartFile> files;
          
          
          public List<MultipartFile> getFiles() {
              if (this.files == null) {
                  this.files = new ArrayList<>();
              }
              return this.files;
          }
      }
}
