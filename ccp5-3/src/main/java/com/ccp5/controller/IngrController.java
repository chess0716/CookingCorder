package com.ccp5.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ccp5.dto.BoardDTO;
import com.ccp5.dto.DataDTO;
import com.ccp5.dto.IngrBoard;
import com.ccp5.service.BoardService;
import com.ccp5.service.IngrListService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class IngrController {
    
    @Autowired
    private IngrListService ilService;
    
    @Autowired
    private BoardService boardService;

	/*
	 * 안드로이드 클라이언트에서 계속 id 값을/get_names?categoryId=all"로 보냄 
	 * 때문에 클라이언트엔 2byte의 빈배열만 전송됨
	 * 특수 값 대신 서버로 특정 파라미터를 전달하지 않는 방식으로 모든 데이터 요청을 단순화.
	 * 서버에서는 @RequestParam의 required 속성을 활용하여,
	 *  파라미터 유무에 따라 모든 데이터를 반환하거나 특정 카테고리 데이터만 반환하는 로직을 구현
	 */
    @GetMapping(path = "/get_names", produces = MediaType.APPLICATION_JSON_VALUE)// 서버가 미디어타입을 text/plain 으로 받아서 수정
    public ResponseEntity<String> getNamesByCategory(@RequestParam(value = "categoryId", required = false) String categoryId) {
        List<DataDTO> names;
        if (categoryId == null || categoryId.isEmpty() || "all".equals(categoryId)) {
            // 'categoryId'가 제공되지 않았거나 'all'인 경우, 모든 데이터를 조회
            names = ilService.findAllNames();
        } else {
            // 'categoryId'에 해당하는 데이터만 조회
            names = ilService.findNames(categoryId);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonNames = objectMapper.writeValueAsString(names);
            return ResponseEntity.ok(jsonNames);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing JSON");
        }
    }


    // 레시피 및 재료 등록
    @PostMapping("/submit_all_forms")
    public ResponseEntity<String> submitAllForms(@RequestBody List<IngrBoard> forms) {
        // 폼 데이터 처리
        for (IngrBoard form : forms) {
            // 각 폼에 대한 처리 로직 수행
            // ingredientService.save(form); // 예시: 폼 데이터를 서비스로 전달하여 저장
            ilService.insertIngr(form);
        }
        return ResponseEntity.ok("All forms submitted successfully");
    }
    
    @PostMapping("/submit_recipe")
    public ResponseEntity<String> submitRecipe(@RequestParam("title") String title,
                               @RequestParam("content") String content,
                               @RequestParam("image") MultipartFile file) {
        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setTitle(title);
        boardDTO.setContent(content);

        if (!file.isEmpty()) {
            try {
                String imageUrl = boardService.uploadAndResizeImage(file);
                boardDTO.setImageUrl(imageUrl);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while uploading image");
            }
        }

        boardService.insertBoard(boardDTO);

        return ResponseEntity.ok("Recipe submitted successfully");
    }

    
    // 레시피 및 재료 수정
    @PostMapping("/submit_all_forms-update")
    public ResponseEntity<Void> submitAllFormsUpdate(@RequestBody List<IngrBoard> ingredientForms) {
        boardService.updateIngredientForms(ingredientForms);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/submit_recipe_update")
    public ResponseEntity<Void> submitRecipeUpdate(@RequestBody BoardDTO recipeForm) {
        boardService.updateRecipeForm(recipeForm);
        return ResponseEntity.ok().build();
    }
    
    // 레시피 및 재료 삭제
    @DeleteMapping("/delete/{num}")
    public ResponseEntity<Integer> deleteRecipe(@PathVariable int num, @RequestBody Map<String, String> requestBody) {
        String title = requestBody.get("title");
        boardService.deleteRecipe(num, title);
        return ResponseEntity.ok(num);
    }
}
