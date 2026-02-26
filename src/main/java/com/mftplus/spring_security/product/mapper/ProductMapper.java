package com.mftplus.spring_security.product.mapper;

import com.mftplus.spring_security.product.dto.ProductDto;
import com.mftplus.spring_security.product.model.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // ⚠️ mapping اضافه شد
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "userFullName", ignore = true) // پر می‌شود در service
    ProductDto toDto(Product product);

    // ⚠️ mapping اضافه شد
    @Mapping(target = "user", ignore = true) // Set می‌شود در service
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    Product toEntity(ProductDto productDto);
}