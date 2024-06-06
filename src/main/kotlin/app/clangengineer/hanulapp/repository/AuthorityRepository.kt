package app.clangengineer.hanulapp.repository

import app.clangengineer.hanulapp.domain.Authority
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

/**
 * Spring Data MongoDB repository for the [Authority] entity.
 */

interface AuthorityRepository : ReactiveMongoRepository<Authority, String>
