package`interface`

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import service.BatalhaService
import repository.PersonagemRepository

@Component
class MenuRPG(
    private val batalhaService: BatalhaService,
    private val personagemRepository: PersonagemRepository
) : CommandLineRunner {

    override fun run(vararg args: String) {
        println("⚔️ BEM-VINDO AO KOTLIN RPG ⚔️")

        // Loop principal do sistema
        while (true) {
            println("\n1. Criar Novo Personagem (CRUD)")
            println("2. Iniciar Batalha")
            println("3. Sair")
            print("Escolha: ")

            when (readlnOrNull()) {
                "1" -> println("Dica: Implemente aqui o salvamento de Guerreiro/Mago no banco!")
                "2" -> iniciarCombate()
                "3" -> return
                else -> println("Opção inválida!")
            }
        }
    }

    private fun iniciarCombate() {
        val personagens = personagemRepository.findAll()
        if (personagens.size < 2) {
            println("❌ Você precisa de pelo menos 2 personagens no banco para lutar!")
            return
        }

        // Listagem simples para escolha
        println("\n--- PERSONAGENS DISPONÍVEIS ---")
        personagens.forEach { println("ID: ${it.id} | Nome: ${it.nome} (${it.javaClass.simpleName})") }

        print("\nDigite o ID do Jogador 1: ")
        val id1 = readln().toLong()
        print("Digite o ID do Jogador 2: ")
        val id2 = readln().toLong()

        val batalha = batalhaService.iniciarNovaBatalha(id1, id2)
        var batalhaAtual = batalha

        // LOOP DE ROUNDS
        while (!batalhaAtual.encerrada) {
            println("\n--- ROUND EM ANDAMENTO ---")
            println("${batalhaAtual.personagem1.nome}: ${batalhaAtual.personagem1.vida} HP")
            println("${batalhaAtual.personagem2.nome}: ${batalhaAtual.personagem2.vida} HP")

            val acaoP1 = escolherAcao(batalhaAtual.personagem1)
            val acaoP2 = escolherAcao(batalhaAtual.personagem2)

            // Processa a lógica no Service (Iniciativa + Ações)
            batalhaAtual = batalhaService.processarRound(batalhaAtual.id, acaoP1, acaoP2)

            // Exibe o log do que aconteceu no round
            println("\n📜 LOG DO ROUND:")
            println(batalhaAtual.logDescritivo.split("\n").last { it.isNotBlank() })
        }

        println("\n🏆 O COMBATE TERMINOU! Vencedor: ${batalhaAtual.nomeVencedor}")
    }

    private fun escolherAcao(p: entity.Personagem): String {
        println("\nAção para ${p.nome}:")
        println("1. Atacar")
        println("2. Usar Poder Especial")
        println("3. Defender")
        print("Escolha: ")

        return when (readln()) {
            "1" -> "ATACAR"
            "2" -> "PODER"
            "3" -> "DEFENDER"
            else -> "ATACAR" // Default caso o usuário erre
        }
    }

}