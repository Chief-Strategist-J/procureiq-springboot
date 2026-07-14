package com.procureiq.springboot_app.features.githubactions.service;

import com.procureiq.springboot_app.features.githubactions.dto.response.GithubActionTemplateResponse;
import com.procureiq.springboot_app.features.githubactions.entity.GithubActionTemplate;
import com.procureiq.springboot_app.features.githubactions.repository.GithubActionTemplateRepository;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GithubActionTemplateService {

    private final GithubActionTemplateRepository githubActionTemplateRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("springboot-app", "1.0.0");

    public GithubActionTemplateService(GithubActionTemplateRepository githubActionTemplateRepository) {
        this.githubActionTemplateRepository = githubActionTemplateRepository;
    }

    private GithubActionTemplateResponse toResponse(GithubActionTemplate t) {
        return new GithubActionTemplateResponse(
                t.getId(),
                t.getName(),
                t.getCategory(),
                t.getDescription(),
                t.getCronExpression(),
                t.getEventType(),
                t.getYamlContent(),
                t.getCreatedAt(),
                t.getUpdatedAt());
    }

    @Transactional(readOnly = true)
    public List<GithubActionTemplateResponse> getAllTemplates() {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            return githubActionTemplateRepository.findAll().stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        });
    }

    @Transactional(readOnly = true)
    public GithubActionTemplateResponse getTemplate(Long id) {
        return com.procureiq.springboot_app.infra.config.TracingHelper.executeServiceWithTracing(() -> {
            GithubActionTemplate t = githubActionTemplateRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("GithubActionTemplate not found: " + id));
            return toResponse(t);
        });
    }
}
