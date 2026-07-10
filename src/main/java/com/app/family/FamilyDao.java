package com.app.family;

import org.springframework.stereotype.Repository;
import com.app.family.types.Family;
import com.app.family.types.FamilyRole;
import com.app.family.types.JoinRequest;
import com.app.family.types.TruncatedJoinRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
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

    public boolean requestExists(int requestId) {
        String sql = "SELECT COUNT(*) FROM join_family_requests WHERE request_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, requestId);
        return count != null && count > 0;
    }

    public String getRequestFamilyId(int requestId) {
        String sql = """
                SELECT family_id FROM join_family_requests WHERE request_id = ?
                """;
        return jdbcTemplate.queryForObject(sql, String.class, requestId);
    }

    public void acceptOrDenyRequest(int requestId, boolean accept) {
        String sql = """
                UPDATE join_family_requests
                SET accepted = ?, updated_at = now()
                WHERE request_id = ?;
                """;
        jdbcTemplate.update(sql, accept, requestId);
    }

    public TruncatedJoinRequest getRequest(int requestId) {
        String sql = """
                SELECT user_id, family_id, family_role FROM join_family_requests WHERE request_id = ?;
                """;
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new TruncatedJoinRequest(
            rs.getString("user_id"),
            rs.getString("family_id"),
            FamilyRole.valueOf(rs.getString("family_role").toUpperCase())
        ), requestId);
    }

    public void addUserToFamily(String userId, String familyId, FamilyRole role) {
        String sql = """
                INSERT INTO user_families (user_id, family_id, family_role)
                VALUES (?, ?, ?::family_role);
                """;
        jdbcTemplate.update(sql, UUID.fromString(userId), UUID.fromString(familyId), role.name().toLowerCase());
    }

    public String userFamilyContext(String userId, String familyId) {
        String sql = """
                SELECT * from pers_activities pa
                inner join activities a on pa.activity_id = a.id
                where pa.user_id = ? and pa.family_id = ?;
                """;
        String req = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getString("name"), UUID.fromString(userId), UUID.fromString(familyId));
        System.out.println(req);
        return req;
    }

    public List<JoinRequest> getJoinRequests(String familyId) {
        String sql = """
                SELECT
                    join_family_requests.request_id as requestId,
                    join_family_requests.user_id as userId,
                    join_family_requests.family_id as familyId,
                    join_family_requests.family_role as familyRole,
                    join_family_requests.created_at as createdAt,
                    a.raw_user_meta_data as fullName
                FROM join_family_requests
                JOIN auth.users as a ON join_family_requests.user_id = a.id
                WHERE join_family_requests.family_id = ? and accepted is null
                ORDER BY join_family_requests.created_at DESC;
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String roleStr = rs.getString("familyRole");
            FamilyRole role = "parent".equals(roleStr) ? FamilyRole.ADULT : FamilyRole.valueOf(roleStr.toUpperCase());
            return new JoinRequest(
                rs.getString("fullName"),
                rs.getInt("requestId"),
                rs.getString("userId"),
                rs.getString("familyId"),
                role,
                rs.getDate("createdAt")
            );
        }, UUID.fromString(familyId));
    }

    public List<Family> getOwnedFamilies(String userId) {
        String sql = """
                SELECT
                    *
                FROM families
                WHERE owner_id = ?
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Family(
            rs.getString("family_id"),
            rs.getString("family_name"),
            rs.getString("owner_id"),
            rs.getString("subscription_level"),
            rs.getDate("created_at"),
            rs.getDate("updated_at")
        ), UUID.fromString(userId));
    }

    public List<Family> getAuthFamilies(String userId) {
        String sql = """
                SELECT f.* FROM families f 
                inner join pers_activities pa on f.family_id = pa.family_id
                inner join activities a on a.id = pa.activity_id
                where pa.user_id = ? and a.name = 'auth_family_user' or a.name = 'family owner'
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Family(
            rs.getString("family_id"),
            rs.getString("family_name"),
            rs.getString("owner_id"),
            rs.getString("subscription_level"),
            rs.getDate("created_at"),
            rs.getDate("updated_at")
        ), UUID.fromString(userId));
    }
}
