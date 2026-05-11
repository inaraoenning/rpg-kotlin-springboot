package crud.rpg.controller

import crud.rpg.entity.Personagem
import org.springframework.web.bind.annotation.*
import crud.rpg.repository.PersonagemRepository

@RestController
@RequestMapping("/personagem")
class PersonagemController(private val repository: PersonagemRepository) {

    @GetMapping
    fun listarTodos(): List<Personagem> = repository.findAll()

    @PostMapping
    fun criar(@RequestBody personagem: Personagem): Personagem = repository.save(personagem)

    @DeleteMapping("/{id}")
    fun deletar(@PathVariable id: Long) = repository.deleteById(id)
}
