package gq.gatherforestyouth.api.community.service

import gq.gatherforestyouth.api.community.domain.Community
import gq.gatherforestyouth.api.community.domain.Replay
import gq.gatherforestyouth.api.community.dto.CommunityDto
import gq.gatherforestyouth.api.community.dto.ReplayDto
import gq.gatherforestyouth.api.community.repository.CommunityRepository
import gq.gatherforestyouth.api.community.repository.ReplayRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommunityService(
    val communityRepository: CommunityRepository,
    val replayRepository: ReplayRepository,
    val passwordEncoder: PasswordEncoder,
) {
    fun getCommunityList(pageable: Pageable): Page<CommunityDto> {
        return communityRepository.findAll(pageable).map { CommunityDto(it) }
    }

    @Transactional
    fun saveCommunity(communityDto: CommunityDto) {
        communityRepository.save(Community(communityDto.apply {
            this.password = passwordEncoder.encode(this.password)
        }))
    }

    @Transactional
    fun saveReplay(replayDto: ReplayDto, id: Long) {
        communityRepository.findById(id).ifPresent {
            it.replayList.add(replayRepository.save(Replay(replayDto, it)))
            communityRepository.save(it)
        }

    }

}
