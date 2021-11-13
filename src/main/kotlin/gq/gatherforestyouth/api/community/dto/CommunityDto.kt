package gq.gatherforestyouth.api.community.dto

import gq.gatherforestyouth.api.community.domain.Community
import java.time.LocalDateTime

class CommunityDto(
    var id: Long? = null,
    var name: String? = null,
    var content: String? = null,
    var password: String?= null,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var replayList: List<ReplayDto>? = listOf(),
) {

    constructor(community: Community) : this(
        id = community.id,
        name = community.name,
        content = community.content,
        createdAt = community.createdAt,
        replayList = community.replayList.map { ReplayDto(it) }
    )

}
