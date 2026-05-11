package crud.rpg.service

import crud.rpg.entity.Batalha
import crud.rpg.entity.Ladino
import crud.rpg.entity.Personagem
import crud.rpg.entity.Sacerdote
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import crud.rpg.repository.BatalhaRepository
import crud.rpg.repository.PersonagemRepository
import java.lang.RuntimeException
import org.springframework.web.client.RestClient
import org.springframework.beans.factory.annotation.Value

@Service
class BatalhaService(
    private val personagemRepository: PersonagemRepository,
    private val batalhaRepository: BatalhaRepository,
    private val restClient: RestClient
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

    @Value("\${rival.url}")
    private lateinit var defaultUrlRival: String

    // Envia o ataque para o servidor do rival via HTTP POST
    fun enviarAtaqueParaRival(idBatalha: Long, dano: Int, atacanteNome: String, customUrl: String? = null) {
        val payload = mapOf(
            "idBatalha" to idBatalha,
            "dano" to dano,
            "atacanteNome" to atacanteNome
        )
        val urlFinal = customUrl ?: defaultUrlRival
        try {
            restClient.post()
                .uri("$urlFinal/rede/receber-ataque")
                .body(payload)
                .retrieve()
                .body(String::class.java)
            println("Ataque enviado ao rival ($urlFinal) com sucesso!")
        } catch (e: Exception) {
            println("Erro ao enviar ataque ao rival: ${e.message}")
        }
    }

    // Processa a mensagem de ataque recebida do rival
    @Transactional
    fun processarAtaqueRecebido(idBatalha: Long, dano: Int, atacanteNome: String): String {
        val batalha = batalhaRepository.findById(idBatalha).orElse(null) 
            ?: return "Batalha não encontrada"
        
        if (batalha.encerrada) return "A batalha já está encerrada"

        // Aplica o dano no nosso personagem (personagem1 é o nosso)
        batalha.personagem1.vida -= dano
        batalha.adicionarLog("${batalha.personagem1.nome} recebeu $dano de dano do $atacanteNome (Rival)!")
        
        verificarFimDeJogo(batalha)
        
        personagemRepository.save(batalha.personagem1)
        batalhaRepository.save(batalha)
        
        return "Dano recebido. Vida restante: ${batalha.personagem1.vida}"
    }
}
