package app.clangengineer.hanulapp.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import reactor.core.publisher.Hooks
import tech.jhipster.config.JHipsterConstants

@Configuration
@Profile("!" + JHipsterConstants.SPRING_PROFILE_PRODUCTION)
class ReactorConfiguration {
    init {
        Hooks.onOperatorDebug()
    }
}
