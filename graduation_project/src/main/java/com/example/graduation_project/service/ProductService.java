package com.example.graduation_project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.graduation_project.dto.ProductPublishRequest;
import com.example.graduation_project.entity.Product;
import com.example.graduation_project.vo.ProductVO;

public interface ProductService extends IService<Product> {

    Product getProductById(Long productId);

    ProductVO publishProduct(Long userId, ProductPublishRequest request);

    ProductVO updateProduct(Long userId, Long productId, ProductPublishRequest request);

    ProductVO getProductDetail(Long productId, Long userId);

    IPage<ProductVO> getProductList(Long categoryId, String keyword, String sort, Integer page, Integer size);

    IPage<ProductVO> getMyProducts(Long userId, Integer page, Integer size);

    void offShelfProduct(Long userId, Long productId);

    void markProductSold(Long userId, Long productId);

    void deleteProduct(Long userId, Long productId);

}
