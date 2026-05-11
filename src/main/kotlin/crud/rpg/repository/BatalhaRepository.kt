package crud.rpg.repository
import crud.rpg.entity.Batalha
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BatalhaRepository: JpaRepository<Batalha, Long> {
}
