package net.gidosa.rdb.supports;

import net.gidosa.rdb.test_fixtures.supports.SpringBootSupport;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest(classes = SpringBootSupport.class)
public abstract class SpringBootTestSupport {
}
