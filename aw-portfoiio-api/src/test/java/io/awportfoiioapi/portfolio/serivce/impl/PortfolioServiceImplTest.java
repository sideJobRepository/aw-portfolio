package io.awportfoiioapi.portfolio.serivce.impl;

import io.awportfoiioapi.RepositoryAndServiceTestSupport;
import io.awportfoiioapi.apiresponse.ApiResponse;
import io.awportfoiioapi.file.entity.CommonFile;
import io.awportfoiioapi.file.enums.CommonFileType;
import io.awportfoiioapi.portfolio.dto.request.PortfolioPostRequest;
import io.awportfoiioapi.portfolio.dto.request.PortfolioPutRequest;
import io.awportfoiioapi.portfolio.dto.response.PortfolioGetDetailResponse;
import io.awportfoiioapi.portfolio.dto.response.PortfolioResponse;
import io.awportfoiioapi.portfolio.dto.response.PortfoliosGetDetailResponse;
import io.awportfoiioapi.portfolio.dto.response.PortfoliosOneGetResponse;
import io.awportfoiioapi.portfolio.entity.Portfolio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioServiceImplTest extends RepositoryAndServiceTestSupport {

    @DisplayName("포트폴리오 업로드(파일포함 , 카테고리 포함)")
    @Test
    void test1() throws IOException {
        File file1 = new File("src/test/java/io/awportfoiioapi/image/이건 모자가 아니잖아.jpg");
        FileInputStream fis1 = new FileInputStream(file1);
        
        MockMultipartFile multipartFile1 = new MockMultipartFile(
                "portfolio", file1.getName(), "image/jpeg",fis1
        );
        PortfolioPostRequest request = new PortfolioPostRequest(
                1L,
                "포트폴리오 타이틀",
                "포트폴리오 설명",
                "포트폴리오 도메인",
                2,
                "포트폴리오 슬러그",
                multipartFile1,
                Boolean.TRUE
        );
        ApiResponse portfolio = portfolioService.createPortfolio(request);
        System.out.println("portfolio = " + portfolio);
    }
    @DisplayName("포트폴리오 업로드(파일포함")
    @Test
    void test2() throws IOException {
        File file1 = new File("src/test/java/io/awportfoiioapi/image/이건 모자가 아니잖아.jpg");
        FileInputStream fis1 = new FileInputStream(file1);
        
        MockMultipartFile multipartFile1 = new MockMultipartFile(
                "portfolio", file1.getName(), "image/jpeg",fis1
        );
        PortfolioPostRequest request = new PortfolioPostRequest(
                null,
                "포트폴리오 타이틀",
                "포트폴리오 설명",
                "포트폴리오 도메인",
                3,
                "포트폴리오 슬러그",
                multipartFile1,
                Boolean.TRUE
        );
        ApiResponse portfolio = portfolioService.createPortfolio(request);
        System.out.println("portfolio = " + portfolio);
    }
    
    @DisplayName("포트폴리오 업로드(카테고리 포함)")
    @Test
    void test3(){
        PortfolioPostRequest request = new PortfolioPostRequest(
                1L,
                "포트폴리오 타이틀",
                "포트폴리오 설명",
                "포트폴리오 도메인",
                4,
                "포트폴리오 슬러그",
                null,
                Boolean.TRUE
        );
        ApiResponse portfolio = portfolioService.createPortfolio(request);
        System.out.println("portfolio = " + portfolio);
    }
    
    
    @DisplayName("포트폴리오 업로드")
    @Test
    void test4(){
        PortfolioPostRequest request = new PortfolioPostRequest(
                null,
                "포트폴리오 타이틀",
                "포트폴리오 설명",
                "포트폴리오 도메인",
                5,
                "포트폴리오 슬러그",
                null,
                Boolean.TRUE
        );
        ApiResponse portfolio = portfolioService.createPortfolio(request);
        System.out.println("portfolio = " + portfolio);
    }
    @DisplayName("포트폴리오 수정(파일 유지 - thumbnail 값 유지/미변경)")
    @Test
    void test5() {
        PortfolioPutRequest req = baseRequest();
        req.setThumbnail(thumbnailKeep()); // 유지
        
        ApiResponse apiResponse = portfolioService.modifyPortfolio(req);
        System.out.println("apiResponse = " + apiResponse);
    }
    
    @DisplayName("포트폴리오 수정(파일 첨부 - 새로 업로드)")
    @Test
    void modify_upload_thumbnail() throws IOException {
        MockMultipartFile file = loadTestFile(
                "src/test/java/io/awportfoiioapi/image/참새작.png",
                "image/png"
        );
        
        PortfolioPutRequest req = baseRequest();
        req.setTitle("포트폴리오 수정(파일 첨부)");
        req.setThumbnail(thumbnailReplace(file));
        
        ApiResponse apiResponse = portfolioService.modifyPortfolio(req);
        System.out.println("apiResponse = " + apiResponse);
    }
    
    @DisplayName("포트폴리오 수정(파일 대체 - 기존 삭제 후 새 파일 업로드)")
    @Test
    void modify_replace_thumbnail() throws IOException {
        MockMultipartFile file = loadTestFile(
                "src/test/java/io/awportfoiioapi/image/이건 모자가 아니잖아.jpg",
                "image/jpeg"
        );
        
        PortfolioPutRequest req = baseRequest();
        req.setTitle("포트폴리오 수정(파일 대체)");
        req.setDomain("포트폴리오 수정 도메인32");
        req.setThumbnail(thumbnailReplace(file));
        
        ApiResponse apiResponse = portfolioService.modifyPortfolio(req);
        System.out.println("apiResponse = " + apiResponse);
    }
    
    @DisplayName("포트폴리오 수정(파일 없애기 - remove=true)")
    @Test
    void modify_remove_thumbnail() {
        PortfolioPutRequest req = baseRequest();
        req.setTitle("포트폴리오 수정(파일 없애기)");
        req.setThumbnail(thumbnailRemove()); // 삭제
        
        ApiResponse apiResponse = portfolioService.modifyPortfolio(req);
        System.out.println("apiResponse = " + apiResponse);
    }
    
    @DisplayName("포트폴리오 수정(썸네일 파라미터 자체를 null로 보냄 - 유지로 처리)")
    @Test
    void modify_thumbnail_null_means_keep() {
        PortfolioPutRequest req = baseRequest();
        req.setTitle("포트폴리오 수정(thumbnail=null)");
        req.setThumbnail(null); // 아예 안 보냄(유지로 간주)
        
        ApiResponse apiResponse = portfolioService.modifyPortfolio(req);
        System.out.println("apiResponse = " + apiResponse);
    }
    @DisplayName("포트폴리오 삭제(파일포함)")
    @Test
    void test10(){
        ApiResponse apiResponse = portfolioService.deletePortfolio(3L);
        System.out.println("apiResponse = " + apiResponse);
    }
    
    @DisplayName("포트폴리오 삭제(파일미포함)")
    @Test
    void test11(){
        ApiResponse apiResponse = portfolioService.deletePortfolio(6L);
        System.out.println("apiResponse = " + apiResponse);
    }
    
    @DisplayName("포트폴리오 조회(전체)")
    @Test
    void test12(){
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<PortfolioResponse> portfolioList = portfolioService.getPortfolioList(pageRequest);
        System.out.println("portfolioList = " + portfolioList);
    }
    
    @DisplayName("포트폴리오 조회(디테일)")
    @Test
    void test13(){
        PortfolioGetDetailResponse portfolioDetail = portfolioService.getPortfolioDetail(6L);
        System.out.println("portfolioDetail = " + portfolioDetail);
    }
    
    @DisplayName("포트폴리오 조회(유저화면에서)")
    @Test
    void test14(){
        List<PortfolioResponse> portfolioList = portfolioService.getPortfolioList(Boolean.TRUE,null);
        System.out.println("portfolioList = " + portfolioList);
    }
    
    @DisplayName("포트폴리오 상세조회(유저화면에서)")
    @Test
    void test15(){
        List<PortfoliosGetDetailResponse> portfolioDetailOptions = portfolioService.getPortfolioDetailOptions(7L);
        System.out.println("portfolioDetailOptions = " + portfolioDetailOptions);
    }
    
    @DisplayName("포트폴리오 단건조회")
    @Test
    void test16(){
        PortfoliosOneGetResponse portfolioOneDetail = portfolioService.getPortfolioOneDetail(7L);
        
        System.out.println("portfolioOneDetail = " + portfolioOneDetail);
    }
    
    private PortfolioPutRequest.ThumbnailRequest  thumbnailKeep() {
        // 썸네일 유지: thumbnail 객체 자체를 null로 보내도 되고,
        // 객체를 보내되 (file=null, remove=false)로 보내도 됨.
        // 여기선 "null"로 보내는 케이스도 따로 테스트할 거라 keep용 객체를 하나 제공.
        PortfolioPutRequest.ThumbnailRequest  t = new PortfolioPutRequest.ThumbnailRequest ();
        t.setFile(null);
        t.setRemove(false);
        return t;
    }
    
    private PortfolioPutRequest.ThumbnailRequest  thumbnailReplace(MultipartFile file) {
        PortfolioPutRequest.ThumbnailRequest  t = new PortfolioPutRequest.ThumbnailRequest ();
        t.setFile(file);
        t.setRemove(false);
        return t;
    }
    
    private PortfolioPutRequest.ThumbnailRequest  thumbnailRemove() {
        PortfolioPutRequest.ThumbnailRequest  t = new PortfolioPutRequest.ThumbnailRequest ();
        t.setFile(null);
        t.setRemove(true);
        return t;
    }
    
    private PortfolioPutRequest baseRequest() {
        PortfolioPutRequest req = new PortfolioPutRequest();
        req.setId(4L);
        req.setCategoryId(1L);
        req.setTitle("포트폴리오 수정");
        req.setDescription("포트폴리오 수정 설명");
        req.setDomain("포트폴리오 수정 도메인");
        req.setOrder(1);
        req.setSlug("포트폴리오 수정 슬러그");
        req.setIsActive(true);
        return req;
    }
    
    
    private MockMultipartFile loadTestFile(String path, String contentType) throws IOException {
        File file = new File(path);
        try (FileInputStream fis = new FileInputStream(file)) {
            // 첫 번째 파라미터("thumbnail")는 multipart 파트의 name이야.
            // 컨트롤러에서 @ModelAttribute로 바인딩할 때 필드명과 맞추는 게 좋아.
            return new MockMultipartFile("thumbnail", file.getName(), contentType, fis);
        }
    }
}