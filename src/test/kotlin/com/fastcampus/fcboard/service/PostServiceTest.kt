package com.fastcampus.fcboard.service

import com.fastcampus.fcboard.domain.Comment
import com.fastcampus.fcboard.domain.Post
import com.fastcampus.fcboard.exception.PostNotDeletableException
import com.fastcampus.fcboard.exception.PostNotFoundException
import com.fastcampus.fcboard.exception.PostNotUpdatableException
import com.fastcampus.fcboard.repository.CommentRepository
import com.fastcampus.fcboard.repository.PostRepository
import com.fastcampus.fcboard.service.dto.PostCreateRequestDto
import com.fastcampus.fcboard.service.dto.PostSearchRequestDto
import com.fastcampus.fcboard.service.dto.PostUpdateRequestDto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class PostServiceTest(
    private val postService: PostService,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
) : BehaviorSpec({
    beforeSpec {
        postRepository.saveAll(
            listOf(
                Post(title = "title1", content = "content1", createdBy = "kane1"),
                Post(title = "title12", content = "content2", createdBy = "kane2"),
                Post(title = "title13", content = "content3", createdBy = "kane3"),
                Post(title = "title14", content = "content4", createdBy = "kane4"),
                Post(title = "title15", content = "content5", createdBy = "kane5"),
                Post(title = "title6", content = "content6", createdBy = "kane16"),
                Post(title = "title7", content = "content7", createdBy = "kane17"),
                Post(title = "title8", content = "content8", createdBy = "kane18"),
                Post(title = "title9", content = "content9", createdBy = "kane19"),
                Post(title = "title10", content = "content10", createdBy = "kane10")
            )
        )
    }
    given("게시글 생성 시") {
        When("게시글 인풋이 정상적으로 들어오면") {
            val postId = postService.createPost(
                PostCreateRequestDto(
                    title = "제목",
                    content = "내용",
                    createdBy = "kane"
                )
            )
            then("게시글이 정상적으로 생성됨을 확인한다") {
                postId shouldBeGreaterThan 0L
                val post = postRepository.findByIdOrNull(postId)
                post shouldNotBe null
                post?.title shouldBe "제목"
                post?.content shouldBe "내용"
                post?.createdBy shouldBe "kane"
            }
        }
    }
    given("게시글 수정시") {
        val saved = postRepository.save(Post(title = "title", content = "content", createdBy = "kane"))
        When("정상 수정시") {
            val updatedId = postService.updatePost(
                saved.id,
                PostUpdateRequestDto(
                    title = "update title",
                    content = "update content",
                    updatedBy = "kane"
                )
            )
            then("게시글이 정상적으로 수정됨을 확인한다") {
                saved.id shouldBe updatedId
                val updated = postRepository.findByIdOrNull(updatedId)
                updated shouldNotBe null
                updated?.title shouldBe "update title"
                updated?.content shouldBe "update content"
            }
        }
        When("게시글이 없을 때") {
            then("게시글을 찾을수 없다라는 예외가 발생한다.") {
                shouldThrow<PostNotFoundException> {
                    postService.updatePost(
                        9999L,
                        PostUpdateRequestDto(
                            title = "update title",
                            content = "update content",
                            updatedBy = "update kane"
                        )
                    )
                }
            }
        }
        When("작성자가 동일하지 않으면") {
            then("수정할 수 없는 게시물 입니다. 예외가 발생한다.") {
                shouldThrow<PostNotUpdatableException> {
                    postService.updatePost(
                        1L,
                        PostUpdateRequestDto(
                            title = "update title",
                            content = "update content",
                            updatedBy = "update kane"
                        )
                    )
                }
            }
        }
    }
    given("게시글 삭제시") {
        val saved = postRepository.save(Post(title = "title", content = "content", createdBy = "kane"))
        When("정상 삭제시") {
            val postId = postService.deletePost(saved.id, "kane")
            then("게시글이 정상적으로 삭제됨을 확인한다.") {
                postId shouldBe saved.id
                postRepository.findByIdOrNull(postId) shouldBe null
            }
        }
        When("작성자가 동일하지 않으면") {
            val saved2 = postRepository.save(Post(title = "title", content = "content", createdBy = "kane"))
            then("삭제할 수 없는 게시물 입니다. 예외가 발생한다.") {
                shouldThrow<PostNotDeletableException> { postService.deletePost(saved2.id, "kane2") }
            }
        }
    }
    given("게시글 상세조회시") {
        val saved = postRepository.save(Post(title = "title", content = "content", createdBy = "kane"))
        When("정상 조회시") {
            val post = postService.getPost(saved.id)
            then("게시글의 내용이 정상적으로 반환됨을 확인한다") {
                post.id shouldBe saved.id
                post.title shouldBe "title"
                post.content shouldBe "content"
                post.createdBy shouldBe "kane"
            }
        }
        When("게시글이 없을 때") {
            then("게시글을 찾을 수 없다. 예외가 발생한다.") {
                shouldThrow<PostNotFoundException> { postService.getPost(9999L) }
            }
        }
        When("댓글 추가시") {
            commentRepository.save(Comment(content = "댓글 내용1", post = saved, createdBy = "kane1"))
            commentRepository.save(Comment(content = "댓글 내용2", post = saved, createdBy = "kane2"))
            commentRepository.save(Comment(content = "댓글 내용3", post = saved, createdBy = "kane3"))
            val post = postService.getPost(saved.id)
            then("댓글이 함께 조회됨을 확인한다.") {
                post.comments.size shouldBe 3
                post.comments[0].content shouldBe "댓글 내용1"
                post.comments[1].content shouldBe "댓글 내용2"
                post.comments[2].content shouldBe "댓글 내용3"
                post.comments[0].createdBy shouldBe "kane1"
                post.comments[1].createdBy shouldBe "kane2"
                post.comments[2].createdBy shouldBe "kane3"
            }
        }
    }
    given("게시글 목록 조회시") {
        When("정상 조회시") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto())
            then("게시글 페이지가 반환된다") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBe 5
            }
        }
        When("타이틀로 검색") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(title = "title1"))
            then("타이틀에 해당하는 게시글이 반환된다.") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBe 5
                postPage.content[0].title shouldContain "title10"
                postPage.content[0].createdBy shouldBe "kane10"
            }
        }
        When("작성자로 검색") {
            val postPage = postService.findPageBy(PageRequest.of(0, 5), PostSearchRequestDto(createdBy = "kane10"))
            then("작성자에 해당하는 게시글이 반환된다.") {
                postPage.number shouldBe 0
                postPage.size shouldBe 5
                postPage.content.size shouldBe 1
                postPage.content[0].title shouldContain "title10"
                postPage.content[0].createdBy shouldBe "kane10"
            }
        }
    }
})
