package gq.gatherforestyouth.api.community.domain

import gq.gatherforestyouth.api.community.dto.CommunityDto
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Community(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    var name: String? = null,
    var password: String? = null,

    @Column(columnDefinition = "longtext null")
    var content: String? = null,

    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "community", orphanRemoval = true)
    var replayList: MutableSet<Replay> = mutableSetOf(),
) {


    constructor(communityDto: CommunityDto) : this() {
        communityDto.id?.let { this.id = it }
        communityDto.name?.let { this.name = it }
        communityDto.content?.let { this.content = it }
        communityDto.password?.let { this.password = it }
        communityDto.createdAt.let { this.createdAt = it }
        communityDto.replayList?.let {
            this.replayList.clear()
            this.replayList.addAll(it.map { replayDto -> Replay(replayDto, this) })
        }
    }
}
