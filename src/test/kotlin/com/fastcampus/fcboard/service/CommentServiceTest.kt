
package com.fastcampus.fcboard.service

import com.fastcampus.fcboard.domain.Comment
import com.fastcampus.fcboard.domain.Post
import com.fastcampus.fcboard.exception.CommentNotDeletableException
import com.fastcampus.fcboard.exception.CommentNotUpdatableException
import com.fastcampus.fcboard.exception.PostNotFoundException
import com.fastcampus.fcboard.repository.CommentRepository
import com.fastcampus.fcboard.repository.PostRepository
import com.fastcampus.fcboard.service.dto.CommentCreateRequestDto
import com.fastcampus.fcboard.service.dto.CommentUpdateRequestDto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class CommentServiceTest(
    private val commentService: CommentService,
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
) : BehaviorSpec({
    given("댓글 생성 시") {
        postRepository.save(
            Post(
                title = "게시글 제목",
                content = "게시글 내용",
                createdBy = "kane"
            )
        )
        When("댓글 인풋이 정상적으로 들어오면") {
            val commentId = commentService.createComment(
                1L,
                CommentCreateRequestDto(
                    content = "댓글 내용",
                    createdBy = "kane"
                )
            )
            then("댓글이 정상적으로 생성됨을 확인한다") {
                commentId shouldBeGreaterThan 0L
                val comment = commentRepository.findByIdOrNull(commentId)
                comment shouldNotBe null
                comment?.content shouldBe "댓글 내용"
                comment?.createdBy shouldBe "kane"
            }
        }
        When("게시글이 존재하지 않으면") {
            then("게시글 존재하지 않음 예외가 발생한다.") {
                shouldThrow<PostNotFoundException> {
                    commentService.createComment(
                        9999L,
                        CommentCreateRequestDto(
                            content = "댓글 내용",
                            createdBy = "kane"
                        )
                    )
                }
            }
        }
    }
    given("댓글 수정시") {
        val post = postRepository.save(
            Post(
                title = "게시글 제목",
                content = "게시글 내용",
                createdBy = "kane"
            )
        )
        val saved = commentRepository.save(
            Comment(
                content = "댓글 내용",
                createdBy = "kane",
                post = post
            )
        )
        When("인풋이 정상적으로 들어오면") {
            val updatedId = commentService.updateComment(
                saved.id,
                CommentUpdateRequestDto(
                    content = "수정된 댓글 내용",
                    updatedBy = "kane"
                )
            )
            then("댓글이 정상적으로 수정됨을 확인한다") {
                updatedId shouldBe saved.id
                val updated = commentRepository.findByIdOrNull(updatedId)
                updated shouldNotBe null
                updated?.content shouldBe "수정된 댓글 내용"
                updated?.updatedBy shouldBe "kane"
            }
        }
        When("작성자와 수정자가 다르면") {
            then("수정할 수 없는 게시물 예외가 발생한다.") {
                shouldThrow<CommentNotUpdatableException> {
                    commentService.updateComment(
                        saved.id,
                        CommentUpdateRequestDto(
                            content = "수정된 댓글 내용",
                            updatedBy = "수정된 kane"
                        )
                    )
                }
            }
        }
    }
    given("댓글 삭제시") {
        val post = postRepository.save(
            Post(
                title = "게시글 제목",
                content = "게시글 내용",
                createdBy = "kane"
            )
        )
        val saved1 = commentRepository.save(
            Comment(
                content = "댓글 내용1",
                createdBy = "kane1",
                post = post
            )
        )
        val saved2 = commentRepository.save(
            Comment(
                content = "댓글 내용1",
                createdBy = "kane2",
                post = post
            )
        )
        When("인풋이 정상적으로 들어오면") {
            val commentId = commentService.deleteComment(saved1.id, "kane1")
            then("댓글이 정상적으로 삭제됨을 확인한다") {
                commentId shouldBe saved1.id
                commentRepository.findByIdOrNull(commentId) shouldBe null
            }
        }
        When("작성자와 삭제자가 다르면") {
            then("삭제할 수 없는 댓글 예외가 발생한다.") {
                shouldThrow<CommentNotDeletableException> { commentService.deleteComment(saved2.id, "kane1") }
            }
        }
    }
})
