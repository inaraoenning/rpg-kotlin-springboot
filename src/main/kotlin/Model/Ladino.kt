package Model

import Personagem
import jakarta.persistence.*

@Entity
@DiscriminatorValue("LADINO")
class Ladino(nome: String, forca: Int, velocidade: Int, vida: Int, var sagacidade: Int) :
    Personagem(nome = nome, forca = forca, velocidade = velocidade, vida = vida) {

    override fun usarPoder() {
        super.usarPoder()
        println("O Ladino está em modo furtivo!")
    }

    fun usarPoderAlvo(adversario: Personagem) {
        println("$nome ignora a força bruta e usa sagacidade!")

        // chama classe mae e passa sagacidade no valor do dano
        super.atacar(adversario, danoEspecial = sagacidade)
    }
}