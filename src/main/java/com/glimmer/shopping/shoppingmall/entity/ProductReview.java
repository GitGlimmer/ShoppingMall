package com.glimmer.shopping.shoppingmall.entity;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Document(collection = "product_reviews")
@Data
@Builder
public class ProductReview {
    @Id
    private String id;
    private Long productId;
    private Long userId;
    private Integer rating;
    private String comment;
    private LocalDateTime createTime;
}
