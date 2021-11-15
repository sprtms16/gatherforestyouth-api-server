package gq.gatherforestyouth.api.community.controller

import gq.gatherforestyouth.api.community.dto.CommunityDto
import gq.gatherforestyouth.api.community.dto.ReplayDto
import gq.gatherforestyouth.api.community.service.CommunityService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.SortDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/community")
class CommunityController(
    val communityService: CommunityService,
) {

    @GetMapping
    fun getCommunityList(
        @SortDefault(value = ["id"], direction = Sort.Direction.DESC)
        pageable: Pageable,
    ): Page<CommunityDto> {
        return communityService.getCommunityList(pageable)
    }

    @PostMapping
    fun saveCommunity(
        @RequestBody communityDto: CommunityDto,
    ) {
        communityService.saveCommunity(communityDto)
    }

    @PostMapping("{id}/replay")
    fun saveReplay(
        @RequestBody replayDto: ReplayDto,
        @PathVariable id: Long,
    ) {
        communityService.saveReplay(replayDto, id)
    }
}
