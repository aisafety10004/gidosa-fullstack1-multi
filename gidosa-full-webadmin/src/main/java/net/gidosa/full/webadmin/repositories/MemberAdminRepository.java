package net.gidosa.full.webadmin.repositories;

import net.gidosa.full.webadmin.models.MemberAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAdminRepository extends JpaRepository<MemberAdmin, Long> {
}
