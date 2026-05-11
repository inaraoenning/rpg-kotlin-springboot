package crud.rpg.entity
import jakarta.persistence.*

@Entity
@DiscriminatorValue("GUERREIRO")
class Guerreiro(nome: String,forca: Int,velocidade: Int,vida: Int, var defesa: Int,)
    : Personagem(nome = nome, forca = forca, velocidade = velocidade, vida = vida) {

    override fun usarPoder() {
        println("$nome, Ativou postura defensiva!")
        // Desconto dano na defesa aplicado no momento do impacto
    }

    // função para aplicar a regra do poder ao sofrer dano
    fun sofrerDano(valor: Int) {
        if (defesa > 0) {
            // Se a defesa for maior que o dano, a defesa absorve tudo
            if (defesa >= valor) {
                defesa -= valor
                println("$nome absorveu $valor de dano na armadura! 🛡️ Defesa Restante: $defesa")
            } else {
                // Se o dano for maior que a defesa, o que sobrar vai para a vida
                val danoRestante = valor - defesa
                println("$nome teve a defesa quebrada! Absorveu $defesa e sofreu $danoRestante na vida.")
                defesa = 0
                vida -= danoRestante
            }
            return
        }

        vida -= valor
        println("$nome recebeu $valor de dano direto na vida! ❤️ Restante: $vida")
    }
}
