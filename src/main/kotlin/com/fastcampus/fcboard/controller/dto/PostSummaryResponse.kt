package com.fastcampus.fcboard.controller.dto

import com.fastcampus.fcboard.service.dto.PostSummaryResponseDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl

data class PostSummaryResponse(
    val id: Long,
    val title: String,
    val createdBy: String,
    val createdAt: String,
    val tag: String? = null,
    val likeCount: Long = 0,
)

fun Page<PostSummaryResponseDto>.toResponse() = PageImpl(
    this.content.map { it.toResponse() },
    this.pageable,
    this.totalElements
)

fun PostSummaryResponseDto.toResponse() = PostSummaryResponse(
    id = id,
    title = title,
    createdBy = createdBy,
    createdAt = createdAt,
    tag = firstTag
)
