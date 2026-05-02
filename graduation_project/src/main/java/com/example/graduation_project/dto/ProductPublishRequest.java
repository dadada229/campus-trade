package com.example.graduation_project.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductPublishRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer condition;

    private Integer categoryId;

    private List<String> images;

    private String tradePlace;

}
