package com.mftplus.spring_security.product.mapper;

import com.mftplus.spring_security.product.dto.ProductDto;
import com.mftplus.spring_security.product.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")  // Allows Spring to inject this mapper
public interface ProductMapper {

    ProductDto toDto(Product product);

    Product toEntity(ProductDto productDto);
}