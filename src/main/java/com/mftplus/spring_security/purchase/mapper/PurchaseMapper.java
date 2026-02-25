package com.mftplus.spring_security.purchase.mapper;


import com.mftplus.spring_security.purchase.dto.PurchaseDto;
import com.mftplus.spring_security.purchase.model.entity.Purchase;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface PurchaseMapper {
    PurchaseDto toDto(Purchase purchase);
    Purchase toPurchase(PurchaseDto purchaseDto);
}
