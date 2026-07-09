package com.app.family;

import org.springframework.stereotype.Repository;
import com.app.family.types.FamilyRole;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class FamilyDao {

    private final JdbcTemplate jdbcTemplate;

    public FamilyDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean familyExists(String familyId) {
        String sql = "SELECT COUNT(*) FROM families WHERE family_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, UUID.fromString(familyId));
        return count != null && count > 0;
    }

    public boolean userIsInFamily(String userId, String familyId) {
        String sql = "SELECT COUNT(*) FROM user_families WHERE user_id = ? AND family_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, UUID.fromString(userId),
                UUID.fromString(familyId));
        return count != null && count > 0;
    }

    @Transactional
    public void requestJoin(String userId, String familyId, FamilyRole role) {
        String sql = "INSERT INTO join_family_requests (user_id, family_id, family_role) VALUES (?, ?, ?::family_role)";
        jdbcTemplate.update(sql, UUID.fromString(userId), UUID.fromString(familyId), role.name().toLowerCase());
    }

    @Transactional
    public void createFamily(String userId, FamilyRole role) {
        String sql = "INSERT INTO families (owner_id) VALUES (?) RETURNING family_id";
        UUID familyId = jdbcTemplate.queryForObject(sql, UUID.class, UUID.fromString(userId));
        String addUserSql = "INSERT INTO user_families (user_id, family_id, family_role) VALUES (?, ?, ?::family_role)";
        jdbcTemplate.update(addUserSql, UUID.fromString(userId), familyId, role.name().toLowerCase());
    }
}
