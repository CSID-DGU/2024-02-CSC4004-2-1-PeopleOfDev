package com.api.momentup.service;

import com.api.momentup.domain.*;
import com.api.momentup.dto.comment.response.CommentsDto;
import com.api.momentup.dto.post.response.PostDetailDto;
import com.api.momentup.exception.*;
import com.api.momentup.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.events.Comment;
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
                .orElseThrow(GroupNotFoundException::new);

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
                .orElseThrow(GroupNotFoundException::new);

        List<Groups> matchingGroups = findUser.getUserGroups().stream()
                .map(UserGroups::getGroups)
                .filter(group -> group.getGroupNumber().equals(groupNumber))
                .collect(Collectors.toList());

        if (matchingGroups.isEmpty()) {
            throw new GroupNotJoinException();
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


    @Transactional
    // 댓글 작성
    public Long createComment(Long postNumber, String commentContent, Long writeUserNumber) throws UserNotFoundException, PostNotFoundException {
        Post post = postJpaRepository.findById(postNumber).orElseThrow(PostNotFoundException::new);
        Users findUser = userJpaRepository.findById(writeUserNumber).orElseThrow(UserNotFoundException::new);

        PostComment comment = PostComment.createPostComment(post, commentContent, findUser);
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

        System.out.println("getPostNumber" + findPost.getPostNumber() + " userNumber : "+userNumber);
        // 5. 댓글 목록 조회
        List<PostComment> comments = postCommentJpaRepository.findByPost(findPost);

        List<CommentsDto> commentDtos = comments.stream()
                .map(comment -> new CommentsDto(
                        comment.getPostCommentNumber(),
                        comment.getCommentContent(),
                        comment.getWriteDate(),
                        comment.getWriter().getUserId(),
                        comment.getWriter().getUserProfile() != null
                                ? comment.getWriter().getUserProfile().getPicturePath()
                                : "",
                        (long) comment.getLikes().size(), // 좋아요 개수
                        comment.getLikes().stream()
                                .anyMatch(like -> like.getUser().getUserNumber().equals(userNumber)) // 사용자가 좋아요 눌렀는지 확인
                ))
                .toList();

        System.out.println("comments size : "+ comments.size());

        String picturePath = findPost.getPostPicture().getPicturePath();

        PostDetailDto response = new PostDetailDto();
        response.setWriterId(writerId);
        response.setWriterPicturePath(writerProfile);
        response.setContent(findPost.getContent());
        response.setPostPicturePath(picturePath);
        response.setLikeCount(likeCount);
        response.setLikeClick(isLiked);
        response.setComments(commentDtos);

        return response;
    }

    /**
     * 댓글 좋아요 추가
     */
    @Transactional
    public Long addCommentLike(Long postCommentNumber, Long userNumber) throws UserNotFoundException, PostCommentNotFoundException {
        Users user = userJpaRepository.findById(userNumber).orElseThrow(UserNotFoundException::new);

        PostComment postComment = postCommentJpaRepository.findById(postCommentNumber)
                .orElseThrow(PostCommentNotFoundException::new);

        // 중복 좋아요 방지
        if (postCommentLikeJpaRepository.existsByPostCommentAndUser(postComment, user)) {
            throw new IllegalArgumentException("이미 좋아요를 누른 댓글입니다.");
        }

        // 3. 좋아요 생성 및 연관관계 설정
        CommentLike commentLike = CommentLike.createCommentLike(user);
        postComment.addLike(commentLike);

        // 4. 저장
        postCommentLikeJpaRepository.save(commentLike);

        return commentLike.getCommentLikeNumber();
    }

    /**
     * 댓글 좋아요 취소
     */
    @Transactional
    public void cancelCommentLike(Long postCommentNumber, Long userNumber) throws PostCommentNotFoundException, UserNotFoundException {
        PostComment postComment = postCommentJpaRepository.findById(postCommentNumber)
                .orElseThrow(PostCommentNotFoundException::new);

        Users user = userJpaRepository.findById(userNumber)
                .orElseThrow(UserNotFoundException::new);

        if (!postCommentLikeJpaRepository.existsByPostCommentAndUser(postComment, user)) {
            throw new IllegalArgumentException("좋아요를 누르지 않은 댓글입니다.");
        }

        // 2. 해당 사용자의 좋아요 엔티티 찾기
        CommentLike commentLike = postComment.getLikes().stream()
                .filter(like -> like.getUser().getUserNumber().equals(userNumber))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("좋아요가 존재하지 않습니다."));

        postComment.getLikes().remove(commentLike);
        postCommentLikeJpaRepository.delete(commentLike);
    }

    public List<Post> getPopularPostsByGroup(Long groupNumber) throws Exception {
        List<Post> topLikedPosts = null;

        try {
            // 인기 게시물 최대 4개
            Pageable top4 = PageRequest.of(0, 4);
            topLikedPosts = postJpaRepository.findTopLikedPostsByGroup(groupNumber, top4);

            if (topLikedPosts.size() < 4) {
                // 부족한 경우 최신 게시물로 채우기
                List<Long> excludedPostNumbers = topLikedPosts.stream()
                        .map(Post::getPostNumber)
                        .toList();
                Pageable remainingPostsCount = PageRequest.of(0, 4 - topLikedPosts.size());
                List<Post> latestPosts = postJpaRepository.findLatestPostsByGroupExcluding(groupNumber, excludedPostNumbers, remainingPostsCount);

                topLikedPosts.addAll(latestPosts);
            }
        } catch (Exception e) {
            throw new Exception();
        }

        return topLikedPosts;
    }

    public List<Post> getLatestPostsByGroup(Long groupNumber) {
        return postJpaRepository.findLatestPostsByGroup(groupNumber);
    }

}
