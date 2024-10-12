package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionPost;
import java.util.ArrayList;
import java.util.List;

import com.audition.model.PostComments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuditionIntegrationClient {

    @Autowired
    private RestTemplate restTemplate;

    private final static String BASE_URL = "https://jsonplaceholder.typicode.com";
    private final static String POSTS_URL = BASE_URL + "/posts";

    public List<AuditionPost> getPosts() {
        try {
            AuditionPost[] posts = restTemplate.getForObject(POSTS_URL, AuditionPost[].class);
            if (posts != null) {
                return List.of(posts);
            }
        } catch (RestClientException e) {
            throw new SystemException("Resource Not Found", 404);
        }
        return new ArrayList<>();
    }

    public AuditionPost getPostById(final String id) {
        try {
            String url = String.format("%s/%s", POSTS_URL, id);
            return restTemplate.getForObject(url, AuditionPost.class);
        } catch (final HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Cannot find a Post with id " + id, "Resource Not Found", 404);
            } else {
                // Logging the original error message for better debugging
                throw new SystemException("Error fetching post: " + e.getResponseBodyAsString(), "Unknown Error", e.getStatusCode().value());
            }
        } catch (Exception e) {
            // Handle other exceptions as needed
            throw new SystemException("An unexpected error occurred: " + e.getMessage(), "Error", 500);
        }
    }

    public List<PostComments> getCommentsForPostFromPostEndpoint(String postId) {
        String url = String.format(POSTS_URL + "/%s/comments", postId);
        try {
            PostComments[] postComments = restTemplate.getForObject(url, PostComments[].class);
            return postComments != null ? List.of(postComments) : new ArrayList<>();
        } catch (RestClientException e) {
            throw new SystemException("Error fetching comments for post " + postId, "Error", 500);
        }
    }

    public List<PostComments> getCommentsForPostFromCommentsEndpoint(String postId) {
        String url = String.format(BASE_URL + "/comments?postId=%s", postId);
        try {
            PostComments[] postComments = restTemplate.getForObject(url, PostComments[].class);
            return postComments != null ? List.of(postComments) : new ArrayList<>();
        } catch (RestClientException e) {
            throw new SystemException("Error fetching comments for post " + postId, "Error", 500);
        }
    }
}
