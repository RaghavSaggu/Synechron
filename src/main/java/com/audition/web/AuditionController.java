package com.audition.web;

import com.audition.model.AuditionPost;
import com.audition.model.PostComments;
import com.audition.service.AuditionService;

import java.util.Collections;
import java.util.List;

import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuditionController {

    @Autowired
    AuditionService auditionService;

    // Endpoint to get all posts with optional filtering
    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPost> getPosts(@RequestParam(required = false) String filter) {
        // Filter logic based on the query param
        if (filter != null && !filter.isEmpty()) {
            return Collections.singletonList(auditionService.getPostById(filter));
        }
        return auditionService.getPosts();
    }

    // Endpoint to get a post by ID
    @RequestMapping(value = "/posts/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPost(@PathVariable("id") final String postId) {
        // Input validation
        if (postId == null || postId.isEmpty()) {
            throw new IllegalArgumentException("Post ID cannot be null or empty");
        }

        final AuditionPost auditionPost = auditionService.getPostById(postId);
        if (auditionPost == null) {
            throw new OpenApiResourceNotFoundException("Post not found with ID: " + postId);
        }

        return auditionPost;
    }

    // Endpoint to get comments for a specific post
    @RequestMapping(value = "/posts/{id}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<PostComments> getCommentsForPost(@PathVariable("id") final String postId) {
        // Input validation
        if (postId == null || postId.isEmpty()) {
            throw new IllegalArgumentException("Post ID cannot be null or empty");
        }

        List<PostComments> postComments = auditionService.getCommentsForPostFromCommentsEndpoint(postId);
        if(postComments == null || postComments.isEmpty()) {
            throw new OpenApiResourceNotFoundException("Comments not found for post ID: " + postId);
        }

        return postComments;
    }
}
