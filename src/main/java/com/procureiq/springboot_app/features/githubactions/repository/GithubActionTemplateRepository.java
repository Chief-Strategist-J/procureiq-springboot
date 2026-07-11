package com.procureiq.springboot_app.features.githubactions.repository;

import com.procureiq.springboot_app.features.githubactions.entity.GithubActionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GithubActionTemplateRepository extends JpaRepository<GithubActionTemplate, Long> {}
