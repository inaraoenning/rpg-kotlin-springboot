package repository
import entity.Batalha
import entity.Personagem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BatalhaRepository: JpaRepository<Batalha, Long> {
}