package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.mappers.AuthMapper;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor

public class AuthService {
    private final AuthMapper authMapper;

    public String test1() {
        return authMapper.test1().toString();
    }
}
