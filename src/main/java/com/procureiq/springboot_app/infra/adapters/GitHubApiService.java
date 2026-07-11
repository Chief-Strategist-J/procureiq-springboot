package com.procureiq.springboot_app.infra.adapters;

import org.kohsuke.github.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class GitHubApiService {

    @Value("${github.token:}")
    private String githubToken;

    private GitHub gitHub;
    private boolean mockMode = false;

    // In-memory mock storage for testing/mock fallback
    private final Map<String, Map<String, Object>> mockRepos = new HashMap<>();
    private final List<Map<String, Object>> mockDispatches = new ArrayList<>();
    private final List<Map<String, Object>> mockWorkflowRuns = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            String token = System.getenv("GITHUB_TOKEN");
            if (token == null || token.trim().isEmpty()) {
                token = githubToken;
            }
            if (token == null || token.trim().isEmpty()) {
                mockMode = true;
                setupMockData();
                return;
            }
            gitHub = new GitHubBuilder().withOAuthToken(token).build();
            if (!gitHub.isCredentialValid()) {
                mockMode = true;
                setupMockData();
            }
        } catch (Exception e) {
            mockMode = true;
            setupMockData();
        }
    }

    private void setupMockData() {
        Map<String, Object> repo = new HashMap<>();
        repo.put("fullName", "owner/repo");
        repo.put("name", "repo");
        repo.put("description", "Mock repository for testing");
        repo.put("htmlUrl", "https://github.com/owner/repo");
        repo.put("stars", 42);
        repo.put("openIssues", 3);
        mockRepos.put("owner/repo", repo);

        Map<String, Object> run = new HashMap<>();
        run.put("id", 123456L);
        run.put("status", "completed");
        run.put("conclusion", "success");
        run.put("event", "repository_dispatch");
        run.put("htmlUrl", "https://github.com/owner/repo/actions/runs/123456");
        mockWorkflowRuns.add(run);
    }

    public boolean isMockMode() {
        return mockMode;
    }

    public Map<String, Object> getRepositoryDetails(String owner, String repoName) throws IOException {
        String fullName = owner + "/" + repoName;
        if (mockMode) {
            if (mockRepos.containsKey(fullName)) {
                return mockRepos.get(fullName);
            }
            Map<String, Object> repo = new HashMap<>();
            repo.put("fullName", fullName);
            repo.put("name", repoName);
            repo.put("description", "Mock repository " + fullName);
            repo.put("htmlUrl", "https://github.com/" + fullName);
            repo.put("stars", 100);
            repo.put("openIssues", 5);
            return repo;
        }

        GHRepository repo = gitHub.getRepository(fullName);
        Map<String, Object> details = new HashMap<>();
        details.put("fullName", repo.getFullName());
        details.put("name", repo.getName());
        details.put("description", repo.getDescription());
        details.put("htmlUrl", repo.getHtmlUrl() != null ? repo.getHtmlUrl().toString() : null);
        details.put("stars", repo.getStargazersCount());
        details.put("openIssues", repo.getOpenIssueCount());
        return details;
    }

    public void triggerRepositoryDispatch(String owner, String repoName, String eventType, Map<String, Object> clientPayload) throws IOException {
        if (mockMode) {
            Map<String, Object> dispatch = new HashMap<>();
            dispatch.put("owner", owner);
            dispatch.put("repo", repoName);
            dispatch.put("eventType", eventType);
            dispatch.put("clientPayload", clientPayload);
            dispatch.put("timestamp", System.currentTimeMillis());
            mockDispatches.add(dispatch);
            return;
        }

        GHRepository repo = gitHub.getRepository(owner + "/" + repoName);
        repo.dispatch(eventType, clientPayload);
    }

    public List<Map<String, Object>> getWorkflowRuns(String owner, String repoName) throws IOException {
        if (mockMode) {
            return mockWorkflowRuns;
        }

        GHRepository repo = gitHub.getRepository(owner + "/" + repoName);
        List<Map<String, Object>> runs = new ArrayList<>();
        for (GHWorkflowRun run : repo.queryWorkflowRuns().list()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", run.getId());
            map.put("status", run.getStatus() != null ? run.getStatus().name() : null);
            map.put("conclusion", run.getConclusion() != null ? run.getConclusion().name() : null);
            map.put("event", run.getEvent() != null ? run.getEvent().name() : null);
            map.put("htmlUrl", run.getHtmlUrl() != null ? run.getHtmlUrl().toString() : null);
            runs.add(map);
        }
        return runs;
    }

    public List<String> listRepositories() throws IOException {
        if (mockMode) {
            return new ArrayList<>(mockRepos.keySet());
        }
        List<String> list = new ArrayList<>();
        for (GHRepository repo : gitHub.getMyself().listRepositories().toList()) {
            list.add(repo.getFullName());
        }
        return list;
    }
}
