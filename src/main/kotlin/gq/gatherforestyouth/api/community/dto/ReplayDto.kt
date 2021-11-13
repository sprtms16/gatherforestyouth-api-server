package gq.gatherforestyouth.api.community.dto

import gq.gatherforestyouth.api.community.domain.Replay

class ReplayDto(
    var id: Long?= null,
    var name: String? = null,
    var content: String? = null
) {
    constructor(replay:Replay):this(
        id = replay.id,
        name = replay.name,
        content = replay.content
    )
}
