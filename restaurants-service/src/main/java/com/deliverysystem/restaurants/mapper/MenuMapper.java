package com.deliverysystem.restaurants.mapper;

import com.deliverysystem.restaurants.controller.dto.MenuRequestDTO;
import com.deliverysystem.restaurants.controller.dto.MenuResponseDTO;
import com.deliverysystem.restaurants.model.Menu;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    Menu toEntity(MenuRequestDTO dto);
    MenuResponseDTO toResponse(Menu menu);
}
