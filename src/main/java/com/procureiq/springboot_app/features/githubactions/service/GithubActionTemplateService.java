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
        Span span = tracer.spanBuilder("GithubActionTemplateService.getAllTemplates").startSpan();
        try {
            return githubActionTemplateRepository.findAll().stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public GithubActionTemplateResponse getTemplate(Long id) {
        Span span = tracer.spanBuilder("GithubActionTemplateService.getTemplate").startSpan();
        try {
            GithubActionTemplate t = githubActionTemplateRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("GithubActionTemplate not found: " + id));
            return toResponse(t);
        } finally {
            span.end();
        }
    }
}
