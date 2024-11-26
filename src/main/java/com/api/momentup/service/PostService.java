package com.api.momentup.service;

import com.api.momentup.domain.*;
import com.api.momentup.dto.post.response.CommentsDto;
import com.api.momentup.dto.post.response.PostDetailDto;
import com.api.momentup.exception.GroupNotFoundException;
import com.api.momentup.exception.PostNotFoundException;
import com.api.momentup.exception.UserNotFoundException;
import com.api.momentup.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    private final PostJpaRepository postJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final PostLikeJpaRepository postLikeJpaRepository;
    private final PostCommentJpaRepository postCommentJpaRepository;
    private final GroupJpaRepository groupJpaRepository;
    private final PostCommentLikeJpaRepository postCommentLikeJpaRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public Long writePost(String content, MultipartFile file, Long userNumber) throws Exception {
        String usbDir = "post";
        Users findUser = userJpaRepository.findById(userNumber)
                .orElseThrow(()-> new GroupNotFoundException("Group not found"));

        Post post = Post.createPost(content, findUser);

        try {
            String originalFilename = file.getOriginalFilename();
            String filename = UUID.randomUUID() + "_" + originalFilename;

            // 파일 저장 경로 설정
            Path filePath = Paths.get(uploadDir, usbDir ,filename);
            Files.createDirectories(filePath.getParent()); // 디렉토리 생성
            Files.copy(file.getInputStream(), filePath); // 파일 저장

            String saveFilePath = "/uploaded/photos/"+ usbDir+ "/" + filename;

            PostPicture savePostPicture = PostPicture.createPostPicture(post, saveFilePath, originalFilename);

            post.setPostPicture(savePostPicture);

            postJpaRepository.save(post);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }

        return post.getPostNumber();
    }

    @Transactional
    public Long writeGroupPost(String content, MultipartFile file, Long userNumber, Long groupNumber) throws Exception {
        String usbDir = "post";
        Users findUser = userJpaRepository.findById(userNumber)
                .orElseThrow(()-> new GroupNotFoundException("Group not found"));

        List<Groups> matchingGroups = findUser.getUserGroups().stream()
                .map(UserGroups::getGroups)
                .filter(group -> group.getGroupNumber().equals(groupNumber))
                .collect(Collectors.toList());

        if (matchingGroups.isEmpty()) {
            throw new GroupNotFoundException("No groups found with number " + groupNumber);
        }

        Post post = Post.createPost(content, findUser, matchingGroups.get(0));

        try {
            String originalFilename = file.getOriginalFilename();
            String filename = UUID.randomUUID() + "_" + originalFilename;

            // 파일 저장 경로 설정
            Path filePath = Paths.get(uploadDir, usbDir ,filename);
            Files.createDirectories(filePath.getParent()); // 디렉토리 생성
            Files.copy(file.getInputStream(), filePath); // 파일 저장

            String saveFilePath = "/uploaded/photos/"+ usbDir+ "/" + filename;

            PostPicture savePostPicture = PostPicture.createPostPicture(post, saveFilePath, originalFilename);

            post.setPostPicture(savePostPicture);

            postJpaRepository.save(post);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }

        return post.getPostNumber();
    }

    @Transactional
    public Long addLike(Long postId, Long userNumber) {
        Post post = postJpaRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다."));
        Users user = userJpaRepository.findById(userNumber)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 이미 좋아요를 눌렀는지 확인
        boolean alreadyLiked = postLikeJpaRepository.findByPost_PostNumberAndUser_UserNumber(postId, userNumber).isPresent();
        if (alreadyLiked) {
            throw new IllegalStateException("이미 좋아요를 누른 상태입니다.");
        }

        // 좋아요 추가
        PostLike postLike = new PostLike(post, user);
        postLikeJpaRepository.save(postLike);

        return postLike.getLikeNumber();
    }

    // 좋아요 취소
    @Transactional
    public void removeLike(Long postId, Long userId) {
        PostLike postLike = postLikeJpaRepository.findByPost_PostNumberAndUser_UserNumber(postId, userId)
                .orElseThrow(() -> new IllegalArgumentException("좋아요를 누르지 않은 상태입니다."));
        postLikeJpaRepository.delete(postLike);
    }

    // 특정 게시물의 좋아요 개수 조회
    public int getLikeCount(Long postId) {
        return postLikeJpaRepository.countByPost_PostNumber(postId);
    }

    // 댓글 작성
    public Long createComment(String commentContent, Long postId) {
        Post post = postJpaRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
        PostComment comment = PostComment.createPostComment(commentContent, post);
        postCommentJpaRepository.save(comment);
        return comment.getPostCommentNumber();
    }

    // 특정 게시물의 댓글 조회
    public List<PostComment> getCommentsByPost(Long postId) throws PostNotFoundException {
        Post post = postJpaRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        return postCommentJpaRepository.findByPost(post);
    }


    // 팔로우 한 사람들의 최신 게시물 목록
    public List<Post> getLatestPostsByFollowedUsers(Long userNumber) {
        return postJpaRepository.findLatestPostsByFollowedUsers(userNumber);
    }

    public PostDetailDto getPostDetail(Long postNumber, Long userNumber) throws PostNotFoundException, UserNotFoundException {
        Post findPost = postJpaRepository.findById(postNumber)
                .orElseThrow(PostNotFoundException::new);

        Users findUser = userJpaRepository.findById(userNumber)
                .orElseThrow(UserNotFoundException::new);

        Users writer = findPost.getWriter();
        String writerId = writer.getUserId();
        String writerProfile = writer.getUserProfile() != null
                ? writer.getUserProfile().getPicturePath() // 프로필 사진 경로
                : "";

        int likeCount = postLikeJpaRepository.countByPost_PostNumber(postNumber);
        // 4. 좋아요 여부
        boolean isLiked = postLikeJpaRepository.existsByPostAndUser(findPost, findUser);

        // 5. 댓글 목록 조회
        List<CommentsDto> comments = postCommentJpaRepository.findCommentsWithLikeInfo(findPost.getPostNumber(), userNumber);

        String picturePath = findPost.getPostPicture().getPicturePath();

        PostDetailDto response = new PostDetailDto();
        response.setWriterId(writerId);
        response.setWriterPicturePath(writerProfile);
        response.setContent(findPost.getContent());
        response.setPostPicturePath(picturePath);
        response.setLikeCount(likeCount);
        response.setLikeClick(isLiked);
        response.setComments(comments);

        return response;
    }

    /**
     * 댓글 좋아요 추가
     */
    public void addCommentLike(Long postCommentNumber, Users user) {
        PostComment postComment = postCommentJpaRepository.findById(postCommentNumber)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // 중복 좋아요 방지
        if (postCommentLikeJpaRepository.existsByPostCommentAndUser(postComment, user)) {
            throw new IllegalArgumentException("이미 좋아요를 누른 댓글입니다.");
        }

        // 좋아요 추가
        CommentLike commentLike = new CommentLike(postComment, user);
        postCommentLikeJpaRepository.save(commentLike);
    }

    /**
     * 댓글 좋아요 취소
     */
    public void canceCommentlLike(Long postCommentNumber, Users user) {
        PostComment postComment = postCommentJpaRepository.findById(postCommentNumber)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!postCommentLikeJpaRepository.existsByPostCommentAndUser(postComment, user)) {
            throw new IllegalArgumentException("좋아요를 누르지 않은 댓글입니다.");
        }

        // 좋아요 취소
        postCommentLikeJpaRepository.deleteByPostCommentAndUser(postComment, user);
    }

}
