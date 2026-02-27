package com.mftplus.spring_security.product.mapper;

import com.mftplus.spring_security.product.dto.ProductDto;
import com.mftplus.spring_security.product.model.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "userFullName", source = "user.fullName") // ✅ دیگر ignore نیست
    ProductDto toDto(Product product);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    Product toEntity(ProductDto productDto);
}