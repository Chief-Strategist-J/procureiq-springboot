package com.procureiq.springboot_app.features.fieldservice.repository;

import com.procureiq.springboot_app.features.fieldservice.entity.ServiceResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceResourceRepository extends JpaRepository<ServiceResource, Long> {

    @Query(value = "WITH required_skills AS (" +
            "    SELECT sr.skill_id, sr.min_skill_level " +
            "    FROM skill_requirements sr " +
            "    JOIN service_appointments sa ON sa.parent_record_type = sr.required_for_type " +
            "                                 AND sa.parent_record_id = sr.required_for_id " +
            "    WHERE sa.id = :appointmentId" +
            "), " +
            "territory_candidates AS (" +
            "    SELECT stm.service_resource_id " +
            "    FROM service_territory_members stm " +
            "    JOIN service_appointments sa ON sa.service_territory_id = stm.service_territory_id " +
            "    WHERE sa.id = :appointmentId" +
            ") " +
            "SELECT sr.* " +
            "FROM service_resources sr " +
            "JOIN territory_candidates tc ON tc.service_resource_id = sr.id " +
            "WHERE sr.is_active = true " +
            "  AND NOT EXISTS ( " +
            "        SELECT 1 FROM required_skills rq " +
            "        WHERE NOT EXISTS ( " +
            "            SELECT 1 FROM service_resource_skills rs " +
            "            WHERE rs.service_resource_id = sr.id " +
            "              AND rs.skill_id = rq.skill_id " +
            "              AND rs.skill_level >= rq.min_skill_level " +
            "              AND (rs.valid_to IS NULL OR rs.valid_to >= CURRENT_DATE) " +
            "        ) " +
            "  ) " +
            "  AND NOT EXISTS ( " +
            "        SELECT 1 FROM resource_absences ra " +
            "        JOIN service_appointments sa ON sa.id = :appointmentId " +
            "        WHERE ra.service_resource_id = sr.id " +
            "          AND ra.status = 'approved' " +
            "          AND (ra.start_time, ra.end_time) OVERLAPS (sa.scheduled_start, sa.scheduled_end) " +
            "  ) " +
            "  AND EXISTS ( " +
            "      SELECT 1 FROM shifts sh " +
            "      JOIN service_appointments sa ON sa.id = :appointmentId " +
            "      WHERE sh.service_resource_id = sr.id " +
            "        AND sh.service_territory_id = sa.service_territory_id " +
            "        AND sh.start_time <= sa.scheduled_start " +
            "        AND sh.end_time   >= sa.scheduled_end " +
            ")", nativeQuery = true)
    List<ServiceResource> findCandidatesForAppointment(@Param("appointmentId") Long appointmentId);
}

