package gq.gatherforestyouth.api.community.domain

import gq.gatherforestyouth.api.community.dto.ReplayDto
import javax.persistence.*

@Entity
class Replay(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    var name: String? = null,
    @Column(columnDefinition = "longtext null")
    var content: String? = null,

    @ManyToOne
    @JoinColumn(name = "community_id")
    var community: Community? = null,
) {
    constructor(replayDto: ReplayDto, community: Community) : this() {
        replayDto.id?.let { this.id = it }
        replayDto.name?.let { this.name = it }
        replayDto.content?.let { this.content = it }
        this.community = community
    }


}
