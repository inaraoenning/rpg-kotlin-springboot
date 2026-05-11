package crud.rpg.repository

import crud.rpg.entity.Personagem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PersonagemRepository: JpaRepository<Personagem, Long> {
    fun nome(nome: String): MutableList<Personagem>
    fun deletePersonagemsByNome(nome: String): Personagem
}

