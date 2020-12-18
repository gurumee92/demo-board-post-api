package com.gurumee.demoboardpostapi.posts;

import com.gurumee.demoboardpostapi.errors.ErrorResponseDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Api(value = "Post API -> 추후 Product")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostRepository postRepository;

    @ApiOperation(value = "GET /api/posts", notes = "get post list")
    @GetMapping
    public ResponseEntity getPosts(@RequestParam(value="username", required = false) String username) {
        List<Post> posts;

        if (username == null) {
            posts = postRepository.findAll();
        } else {
            posts = postRepository.findByOwnerName(username);
        }

        List<PostResponseDto> responseDtoList = posts.stream().map(this::convertResponseDto).collect(Collectors.toList());
        return ResponseEntity.ok(responseDtoList);
    }

    @ApiOperation(value = "GET /api/posts/search", notes = "search post list")
    @GetMapping("/search")
    public ResponseEntity searchPosts(@RequestParam(value="keyword", required = false) String keyword) {
        List<Post> posts;

        if (keyword == null) {
            posts = postRepository.findAll();
        } else {
            posts = postRepository.findByTitleContainingOrContentContaining(keyword, keyword);
        }

        List<PostResponseDto> responseDtoList = posts.stream().map(this::convertResponseDto).collect(Collectors.toList());
        return ResponseEntity.ok(responseDtoList);
    }

    private PostResponseDto convertResponseDto(Post post) {
        return PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .owner_name(post.getOwnerName())
                .created_at(post.getCreatedAt())
                .updated_at(post.getUpdatedAt())
                .build();
    }

    @ApiOperation(value = "POST /api/posts/", notes = "create a post")
    @Authorization(value = "write")
    @PostMapping
    public ResponseEntity createPost(@RequestBody @Valid CreatePostRequestDto requestDto,
                                     @ApiIgnore Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String ownerName = authentication.getName();

        if (ownerName == null) {
            ErrorResponseDto errResponseDto = ErrorResponseDto.builder()
                    .message("Accees token is not exist.")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errResponseDto);
        }

        Post newPost = Post.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .ownerName(ownerName)
                .build();
        Post saved = postRepository.save(newPost);
        PostResponseDto responseDto = convertResponseDto(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }


    @ApiOperation(value = "GET /api/posts/:id", notes = "get a post")
    @GetMapping("/{id}")
    public ResponseEntity getPost(@PathVariable("id") Long id) {
        Optional<Post> postOrNull = postRepository.findById(id);

        if (postOrNull.isEmpty()) {
            ErrorResponseDto errResponseDto = ErrorResponseDto.builder()
                    .message("Post ID: " + id + " is not exist.")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errResponseDto);
        }

        Post post = postOrNull.get();
        PostResponseDto responseDto = convertResponseDto(post);
        return ResponseEntity.ok(responseDto);
    }

    @ApiOperation(value = "PUT /api/posts/id", notes = "update a post")
    @Authorization(value = "write")
    @PutMapping("/{id}")
    public ResponseEntity updatePost(@PathVariable("id") Long id,
                                     @RequestBody @Valid UpdatePostRequestDto requestDto,
                                     @ApiIgnore Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String ownerName = authentication.getName();

        if (ownerName == null) {
            ErrorResponseDto errResponseDto = ErrorResponseDto.builder()
                    .message("Accees token is not exist.")
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errResponseDto);
        }

        Optional<Post> postOrNull = postRepository.findById(id);

        if (postOrNull.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("post is not exist");
        }

        Post post = postOrNull.get();

        if (!post.getOwnerName().equals(ownerName)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("not authenticated: owner is different");
        }

        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());
        Post updated = postRepository.save(post);
        PostResponseDto responseDto = convertResponseDto(updated);
        return ResponseEntity.ok(responseDto);
    }

    @ApiOperation(value = "DELETE /api/posts/id", notes = "delete a post")
    @Authorization(value = "write")
    @DeleteMapping("/{id}")
    public ResponseEntity deletePost(@PathVariable("id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String ownerName = authentication.getName();

        if (ownerName == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("not authenticated: access token is invalidate");
        }

        Optional<Post> postOrNull = postRepository.findById(id);

        if (postOrNull.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("post is not exist");
        }

        Post post = postOrNull.get();

        if (!post.getOwnerName().equals(ownerName)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("not authenticated: owner is different");
        }

        PostResponseDto responseDto = convertResponseDto(post);
        postRepository.delete(post);
        return ResponseEntity.ok(responseDto);
    }

}
