// package com.fastcampus.fcboard.service
//
// import io.kotest.core.spec.style.BehaviorSpec
// import org.springframework.boot.test.context.SpringBootTest
//
// @SpringBootTest
// class CommentServiceTest(
//    private val commentService: CommentService,
//    private val commentRepository: CommentRepository,
// ): BehaviorSpec({
//    given("댓글 생성 시") {
//        When("댓글 인풋이 정상적으로 들어오면") {
//            val commentId = commentService.createComment(
//                CommentCreateRequestDto(
//                    postId = 1L,
//                    content = "댓글 내용",
//                    createdBy = "kane"
//                )
//            )
//            then("댓글이 정상적으로 생성됨을 확인한다") {
//                commentId shouldBeGreaterThan 0L
//                val comment = commentRepository.findByIdOrNull(commentId)
//                comment shouldNotBe null
//                comment?.content shouldBe "댓글 내용"
//                comment?.createdBy shouldBe "kane"
//            }
//        }
//        When("게시글이 존재하지 않으면") {
//            then("게시글 존재하지 않음 예외가 발생한다.") {
//
//            }
//        }
//    }
// })
//
