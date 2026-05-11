package crud.rpg.controller

import entity.Personagem
import org.springframework.web.bind.annotation.*
import repository.PersonagemRepository

@RestController
@RequestMapping("/personagens")
class PersonagemController(private val repository: PersonagemRepository) {

    @GetMapping
    fun listarTodos(): List<Personagem> = repository.findAll()

    @PostMapping
    fun criar(@RequestBody personagem: Personagem): Personagem = repository.save(personagem)

    @DeleteMapping("/{id}")
    fun deletar(@PathVariable id: Long) = repository.deleteById(id)
}