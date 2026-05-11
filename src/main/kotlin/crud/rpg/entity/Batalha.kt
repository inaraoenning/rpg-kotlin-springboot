package crud.rpg.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "batalhas")
class Batalha(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    // Relacionamento com a tabela de Personagem
    @ManyToOne
    @JoinColumn(name = "personagem1_id", nullable = false)
    val personagem1: Personagem,

    @ManyToOne
    @JoinColumn(name = "personagem2_id", nullable = false)
    val personagem2: Personagem,

    // Registra quem venceu ao final
    var nomeVencedor: String? = null,

    // TEXT no Postgres permite logs longos sem limite de caracteres curto
    @Column(columnDefinition = "TEXT")
    var logDescritivo: String = "",

    // Informações úteis para relatórios
    val dataInicio: LocalDateTime = LocalDateTime.now(),

    var encerrada: Boolean = false
) {
    // Método auxiliar para adicionar eventos ao log da batalha
    fun adicionarLog(mensagem: String) {
        val timestamp = LocalDateTime.now().toLocalTime().toString().substring(0, 8)
        this.logDescritivo += "[$timestamp] $mensagem\n"
    }
}
