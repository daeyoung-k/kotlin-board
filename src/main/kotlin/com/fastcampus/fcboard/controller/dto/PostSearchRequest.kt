package com.fastcampus.fcboard.controller.dto

import org.springframework.web.bind.annotation.RequestParam

data class PostSearchRequest(
    @RequestParam
    val title: String?,
    @RequestParam
    val createdBy: String?,
)

fun PostSearchRequest.toDto() = com.fastcampus.fcboard.service.dto.PostSearchRequestDto(
    title = title,
    createdBy = createdBy
)
