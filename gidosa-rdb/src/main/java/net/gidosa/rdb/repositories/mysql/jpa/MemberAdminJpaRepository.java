package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAdminJpaRepository extends JpaRepository<MemberAdmin, Long> {
}
