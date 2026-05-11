import jakarta.persistence.*;

@Entity
@Table(name = "personagem")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// Cria uma coluna no banco para dizer o tipo do personagem.
@DiscriminatorColumn(name = "tipo_personagem", discriminatorType = DiscriminatorType.STRING)

class Personagem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val nome: String,
    var forca: Int,
    var velocidade: Int,
    var vida: Int
) {
     fun usarPoder(){
        println("O herói $nome usou poder!")
    }

    // calc o mais rápido e descontar a Força da Vida do adversário.
    fun atacar(adversario: Personagem, danoEspecial: Int? = null) {
        val danoFinal = danoEspecial ?: this.forca // Se houver dano especial

        if (this.velocidade >= adversario.velocidade) {
            adversario.vida -= danoFinal
            println("${nome} é foi mais rápido e atacou primeiro! causando $danoFinal")

            // Se o adversário for Guerreiro, sofrerDano dele
            if (adversario is Guerreiro) {
                adversario.sofrerDano(danoFinal)
            } else {
                adversario.vida -= danoFinal
                println("${adversario.nome} recebeu $danoFinal de dano! ❤️ Restante: ${adversario.vida}")
            }
            return // early return
        }

        println("${adversario.nome} esquivou do ataque!")

    }

    fun defender(adversario: Personagem) {
        // Calcula se a Velocidade é maior que a Força do adversário
        if (this.velocidade < adversario.forca) {
            print(" 💨 Você agiu rápido e esquivou do ataque! 💨 ")
            return
        }

        val dano = adversario.forca
        this.vida -= dano
        println(" -❤ $nome não conseguiu defender e sofreu $dano de dano.")

    }
}