package gq.gatherforestyouth.api.community.repository

import gq.gatherforestyouth.api.community.domain.Community
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface CommunityRepository : JpaRepository<Community, Long>, JpaSpecificationExecutor<Community>
