package gq.gatherforestyouth.api.community.repository;

import gq.gatherforestyouth.api.community.domain.Replay
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ReplayRepository : JpaRepository<Replay, Long>, JpaSpecificationExecutor<Replay> {
}
