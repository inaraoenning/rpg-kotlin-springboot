package crud.rpg.service

import crud.rpg.entity.Personagem
import org.springframework.stereotype.Service
import crud.rpg.repository.PersonagemRepository

@Service
class PersonagemService(
    val personagemRepository: PersonagemRepository
) {

    // Salvar
    fun salvar(personagem: Personagem): Personagem {
        return personagemRepository.save(personagem) // Jpa: se já tiver primary key atualiza os dados senão cria um novo
    }

    // Buscar
    fun buscarTodos():List<Personagem>{
        return personagemRepository.findAll()
    }
    fun buscarPorId(id: Long):Personagem{
     return personagemRepository.findById(id).get()
    }

    // Excluir
    fun excluirPersonagem(nome:String):Personagem{
        return personagemRepository.deletePersonagemsByNome(nome)
    }
}
