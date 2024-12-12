package com.api.momentup.api;

import com.api.momentup.domain.Post;
import com.api.momentup.dto.ApiResult;
import com.api.momentup.dto.CountDto;
import com.api.momentup.dto.ResultType;
import com.api.momentup.dto.comment.request.AddCommentLikeRequest;
import com.api.momentup.dto.comment.request.CommentWriteRequest;
import com.api.momentup.dto.post.request.LikeRequest;
import com.api.momentup.dto.post.request.PostDetailRequest;
import com.api.momentup.dto.post.request.PostWriteRequest;
import com.api.momentup.dto.post.response.PostDetailDto;
import com.api.momentup.dto.post.response.RecentPostsDto;
import com.api.momentup.exception.*;
import com.api.momentup.service.PostService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping()
    public ApiResult writePost(@RequestPart("post")PostWriteRequest request, @RequestPart("picture") MultipartFile postPicture) {
        try {
            Long postNumber = null;

            System.out.println("content : "+ request.getContent() + "  getUserNumber : "+ request.getUserNumber());
            if(request.getGroupNumber() == null) {
                postNumber  = postService.writePost(request.getContent(), postPicture, request.getUserNumber());
            } else {
                postNumber = postService.writeGroupPost(request.getContent(), postPicture, request.getUserNumber(), request.getGroupNumber());
            }

            return ApiResult.success(postNumber);
        } catch (UserNotFoundException | GroupNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (GroupNotJoinException e) {
            return ApiResult.error(ResultType.FAIL.getCode(), e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResult.error(ResultType.FAIL.getCode(), ResultType.FAIL.getMessage());
        }
    }

    // 좋아요 추가
    @PostMapping("/like/{postNumber}")
    public ApiResult addLike(@PathVariable Long postNumber, @RequestBody LikeRequest request) {
        try {
            Long likeNumber = postService.addLike(postNumber, request.getUserNumber());

            return ApiResult.success(likeNumber);
        } catch (Exception e) {
            return ApiResult.error(ResultType.FAIL.getCode(), e.getMessage());
        }
    }

    // 좋아요 취소
    @DeleteMapping("/like/cancel/{postNumber}")
    public ApiResult removeLike(@PathVariable Long postNumber, @RequestBody LikeRequest request) {
        postService.removeLike(postNumber, request.getUserNumber());

        return ApiResult.success(null);
    }

    // 좋아요 개수 조회
    @GetMapping("/like/count/{postNumber}")
    public ApiResult getLikeCount(@PathVariable Long postNumber) {
        return ApiResult.success(new CountDto(postService.getLikeCount(postNumber)));
    }

    @PostMapping("/comment")
    private ApiResult writeComment(@RequestBody @Valid CommentWriteRequest request) {
        try {
            Long saveComment = postService.createComment(request.getPostNumber(), request.getContent(), request.getWriteUserNumber());

            return ApiResult.success(saveComment);
        }  catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (PostNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }
    }

    @GetMapping("/recent/{userNumber}")
    public ApiResult getRecentPosts(@PathVariable Long userNumber) {
        List<Post> latestPostsByFollowedUsers = postService.getLatestPostsByFollowedUsers(userNumber);

        List<RecentPostsDto> result = latestPostsByFollowedUsers.stream()
                .map(p ->
                        new RecentPostsDto(p.getPostNumber(), p.getWriter().getUserId(),
                                p.getPostPicture().getPicturePath()))
                .toList();

        return ApiResult.success(result);
    }

    @GetMapping("/detail/{postNumber}")
    public ApiResult getPostDetail(@PathVariable Long postNumber, @RequestBody PostDetailRequest request) {
        try {
            PostDetailDto postDetail = postService.getPostDetail(postNumber, request.getOwnUserNumber());

            return ApiResult.success(postDetail);
        } catch (PostNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }

    }

    // 댓글 좋아요
    @PostMapping("/comment/like/{commentNumber}")
    public ApiResult addCommentLike(@PathVariable Long commentNumber, @RequestBody AddCommentLikeRequest request) {
        try {
            Long saveLike = postService.addCommentLike(commentNumber, request.getUserNumber());

            return ApiResult.success(saveLike);
        } catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (PostCommentNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (Exception e) {
            return ApiResult.error(ResultType.FAIL.getCode(), e.getMessage());
        }
    }

    @DeleteMapping("/comment/like/{commentNumber}")
    public ApiResult cancelCommentLike(@PathVariable Long commentNumber, @RequestBody AddCommentLikeRequest request) {
        try {
            postService.cancelCommentLike(commentNumber, request.getUserNumber());

            return ApiResult.success(null);
        } catch (UserNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        } catch (PostCommentNotFoundException e) {
            return ApiResult.error(ResultType.NOT_FOUND.getCode(), e.getMessage());
        }

    }
}
