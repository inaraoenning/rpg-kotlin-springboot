package entity
import jakarta.persistence.*

@Entity
@DiscriminatorValue("MAGO")
class Mago(nome: String, forca: Int, velocidade: Int, vida:Int, var magia : Int)
    :Personagem(nome= nome, forca = forca, velocidade = velocidade, vida = vida) {

    override fun usarPoder() {
        this.vida += magia
        this.forca += magia
        println("$nome usou Magia aumentando Força e Vida em $magia!")
    }
}