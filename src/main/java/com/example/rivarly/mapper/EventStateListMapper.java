package com.example.rivarly.mapper;

import com.example.rivarly.dto.event.EventStateListResponse;

import com.example.rivarly.entity.EventStateList;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventStateListMapper {

    EventStateListResponse toDto(EventStateList eventStateList);

}
