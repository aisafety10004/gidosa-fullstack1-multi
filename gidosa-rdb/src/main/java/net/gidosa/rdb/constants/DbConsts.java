package net.gidosa.rdb.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DbConsts {
    public static final String RDS_MYSQL_ENTITY_MANAGER_UNIT_NAME = "rdsMysqlEntityManagerUnit";
    public static final String REPLICATION_WRITE_READ = "write";
    public static final String REPLICATION_WRITE1_READ = "write1";
    public static final String REPLICATION_MASTER = "master";
    public static final String REPLICATION_READ = "read";
    public static final String REPLICATION_READ1 = "read1";
    public static final String REPLICATION_READ2 = "read2";
    public static final String REPLICATION_SLAVE = "slave";
    public static final String RDS_POSTGRES_QUERY_DSL_FACTORY = "rdsJpaQueryFactory";
}
