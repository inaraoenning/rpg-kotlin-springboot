package service

import entity.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import repository.BatalhaRepository
import repository.PersonagemRepository
import java.lang.RuntimeException

@Service
class BatalhaService(
    private val personagemRepository: PersonagemRepository,
    private val batalhaRepository: BatalhaRepository
) {

    // 1. Inicia a batalha no banco
    @Transactional
    fun iniciarNovaBatalha(id1: Long, id2: Long): Batalha {
        val p1 = personagemRepository.findById(id1).get()
        val p2 = personagemRepository.findById(id2).get()

        val batalha = Batalha(personagem1 = p1, personagem2 = p2)
        batalha.adicionarLog("Início do combate: ${p1.nome} vs ${p2.nome}")
        return batalhaRepository.save(batalha)
    }

    // 2. Processa o Round com Iniciativa Aleatória
    @Transactional
    fun processarRound(batalhaId: Long, acaoP1: String, acaoP2: String): Batalha {
        val batalha = batalhaRepository.findById(batalhaId).orElseThrow { RuntimeException("Batalha não encontrada") }
        val p1 = batalha.personagem1
        val p2 = batalha.personagem2

        if (batalha.encerrada) return batalha

        // Rolar Iniciativa (Velocidade + Dado 1-20)
        val initP1 = p1.velocidade + (1..20).random()
        val initP2 = p2.velocidade + (1..20).random()

        batalha.adicionarLog("Iniciativa: ${p1.nome}($initP1) | ${p2.nome}($initP2)")

        // Define a ordem de quem age primeiro
        if (initP1 >= initP2) {
            executarAcao(p1, p2, acaoP1, batalha)
            if (p2.vida > 0) executarAcao(p2, p1, acaoP2, batalha)
        } else {
            executarAcao(p2, p1, acaoP2, batalha)
            if (p1.vida > 0) executarAcao(p1, p2, acaoP1, batalha)
        }

        // Verifica se alguém morreu
        verificarFimDeJogo(batalha)

        // Salva o estado atual dos personagens e da batalha no Postgres
        personagemRepository.saveAll(listOf(p1, p2))
        return batalhaRepository.save(batalha)
    }

    private fun executarAcao(ativo: Personagem, alvo: Personagem, acao: String, batalha: Batalha) {
        when (acao.uppercase()) {
            "ATACAR" -> {
                batalha.adicionarLog("${ativo.nome} atacou!")
                ativo.atacar(alvo)
            }
            "PODER" -> {
                batalha.adicionarLog("${ativo.nome} usou seu PODER ESPECIAL!")
                // Polimorfismo: o Kotlin chama o poder específico de cada classe
                when(ativo) {
                    is Sacerdote -> ativo.executarPenitencia(alvo)
                    is Ladino -> ativo.usarPoderAlvo(alvo)
                    else -> ativo.usarPoder()
                }
            }
            "DEFENDER" -> {
                batalha.adicionarLog("${ativo.nome} preparou defesa!")
                ativo.defender(alvo) // Usando sua lógica de esquiva/velocidade
            }
        }
    }

    private fun verificarFimDeJogo(batalha: Batalha) {
        if (batalha.personagem1.vida <= 0) {
            batalha.nomeVencedor = batalha.personagem2.nome
            batalha.encerrada = true
            batalha.adicionarLog("VITÓRIA DE ${batalha.personagem2.nome}!")
        } else if (batalha.personagem2.vida <= 0) {
            batalha.nomeVencedor = batalha.personagem1.nome
            batalha.encerrada = true
            batalha.adicionarLog("VITÓRIA DE ${batalha.personagem1.nome}!")
        }
    }
}