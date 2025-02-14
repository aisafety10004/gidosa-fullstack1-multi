package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.repositories.mysql.mybatis.AuthMyBatisRepository;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthMyBatisRepository authMyBatisRepository;

    public String test1() {
        return authMyBatisRepository.test1().toString();
    }
}
