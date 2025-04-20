package io.github.pangju666.framework.web.spec

import io.github.pangju666.framework.web.TestApplication
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ActiveProfiles("test")
@ContextConfiguration(classes = TestApplication.class, loader = SpringBootContextLoader.class)
class HttpExceptionFilterSpec extends Specification {
}