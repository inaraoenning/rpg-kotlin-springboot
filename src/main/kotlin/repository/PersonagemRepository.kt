package repository

import entity.Personagem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
@Repository
interface PersonagemRepository: JpaRepository<Personagem, Long>
