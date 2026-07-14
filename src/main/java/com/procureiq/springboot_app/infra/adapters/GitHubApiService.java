package com.procureiq.springboot_app.infra.adapters;

import org.kohsuke.github.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class GitHubApiService {

    @org.springframework.beans.factory.annotation.Autowired
    private com.procureiq.springboot_app.infra.config.AppProperties appProperties;

    private GitHub gitHub;
    private boolean mockMode = false;

    
    private final Map<String, Map<String, Object>> mockRepos = new HashMap<>();
    private final List<Map<String, Object>> mockDispatches = new ArrayList<>();
    private final List<Map<String, Object>> mockWorkflowRuns = new ArrayList<>();
    private final Map<String, String> mockWorkflowFiles = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            String token = System.getenv("GITHUB_TOKEN");
            if (token == null || token.trim().isEmpty()) {
                token = appProperties.getGithubToken();
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

    
    public Map<String, Object> createWorkflowFile(
            String owner, String repoName,
            String workflowName, String yamlContent, String commitMsg) throws IOException {

        String filePath = ".github/workflows/" + workflowName + ".yml";

        if (mockMode) {
            String key = owner + "/" + repoName + "/" + filePath;
            mockWorkflowFiles.put(key, yamlContent);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("path", filePath);
            result.put("sha", "mock-sha-" + workflowName);
            result.put("htmlUrl", "https://github.com/" + owner + "/" + repoName + "/blob/main/" + filePath);
            result.put("message", commitMsg);
            result.put("mock", true);
            return result;
        }

        GHRepository repo = gitHub.getRepository(owner + "/" + repoName);
        byte[] contentBytes = yamlContent.getBytes(StandardCharsets.UTF_8);

        
        GHContentUpdateResponse response;
        try {
            GHContent existing = repo.getFileContent(filePath);
            response = existing.update(contentBytes, commitMsg);
        } catch (GHFileNotFoundException e) {
            response = repo.createContent()
                    .path(filePath)
                    .content(contentBytes)
                    .message(commitMsg)
                    .commit();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("path", filePath);
        result.put("sha", response.getContent() != null ? response.getContent().getSha() : null);
        result.put("htmlUrl", response.getContent() != null && response.getContent().getHtmlUrl() != null
                ? response.getContent().getHtmlUrl().toString() : null);
        result.put("message", commitMsg);
        result.put("mock", false);
        return result;
    }

    
    public void deleteWorkflowFile(String owner, String repoName,
                                   String workflowName, String commitMsg) throws IOException {
        String filePath = ".github/workflows/" + workflowName + ".yml";

        if (mockMode) {
            mockWorkflowFiles.remove(owner + "/" + repoName + "/" + filePath);
            return;
        }

        GHRepository repo = gitHub.getRepository(owner + "/" + repoName);
        GHContent file = repo.getFileContent(filePath);
        file.delete(commitMsg);
    }

    
    public Map<String, String> getMockWorkflowFiles() {
        return Collections.unmodifiableMap(mockWorkflowFiles);
    }
}
