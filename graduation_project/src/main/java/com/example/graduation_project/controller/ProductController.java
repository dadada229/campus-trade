package com.example.graduation_project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.graduation_project.common.Result;
import com.example.graduation_project.common.ResultGenerator;
import com.example.graduation_project.common.UserContext;
import com.example.graduation_project.dto.ProductPublishRequest;
import com.example.graduation_project.service.ProductService;
import com.example.graduation_project.vo.ProductVO;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public Result<ProductVO> publishProduct(@Valid @RequestBody ProductPublishRequest productRequest) {
        Long userId = UserContext.getUserId();
        log.info("User {} is publishing product: {}", userId, productRequest.getTitle());
        ProductVO productVO = productService.publishProduct(userId, productRequest);
        return ResultGenerator.success(productVO);
    }

    @PutMapping("/{id}")
    public Result<ProductVO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductPublishRequest productRequest) {
        Long userId = UserContext.getUserId();
        log.info("User {} is updating product: {}", userId, id);
        ProductVO productVO = productService.updateProduct(userId, id, productRequest);
        return ResultGenerator.success(productVO);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        log.info("User {} is deleting product: {}", userId, id);
        productService.deleteProduct(userId, id);
        return ResultGenerator.success();
    }

    @GetMapping("/{id}")
    public Result<ProductVO> getProductDetail(@PathVariable Long id) {
        Long userId = null;
        try {
            userId = UserContext.getUserId();
        } catch (Exception e) {
            userId = null;
        }
        ProductVO productVO = productService.getProductDetail(id, userId);
        return ResultGenerator.success(productVO);
    }

    @GetMapping
    public Result<IPage<ProductVO>> getProductList(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "latest") String sort,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        IPage<ProductVO> productList = productService.getProductList(categoryId, keyword, sort, page, size);
        return ResultGenerator.success(productList);
    }

    @GetMapping("/search")
    public Result<IPage<ProductVO>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        IPage<ProductVO> productList = productService.getProductList(null, keyword, sort, page, size);
        return ResultGenerator.success(productList);
    }

    @GetMapping("/user/my")
    public Result<IPage<ProductVO>> getMyProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Long userId = UserContext.getUserId();
        IPage<ProductVO> myProducts = productService.getMyProducts(userId, page, size);
        return ResultGenerator.success(myProducts);
    }

    @PutMapping("/{id}/off-shelf")
    public Result<Void> offShelfProduct(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        productService.offShelfProduct(userId, id);
        return ResultGenerator.success();
    }

    @PutMapping("/{id}/mark-sold")
    public Result<Void> markProductSold(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        productService.markProductSold(userId, id);
        return ResultGenerator.success();
    }

}
