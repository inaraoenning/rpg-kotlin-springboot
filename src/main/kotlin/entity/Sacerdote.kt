package entity

import jakarta.persistence.*

@Entity
@DiscriminatorValue("SACERDOTE")
class Sacerdote(nome:String, forca: Int, velocidade:Int, var vidaMaxima:Int, var penitencia: Int)
    : Personagem(nome = nome, forca = forca, velocidade = velocidade, vida = vidaMaxima)
{
    //Penitência para curar a si mesmo e causar dano baseado na vida perdida
    override fun usarPoder() {
        println("$nome usou 'Penitência'! 🙏 ...")
    }

    fun executarPenitencia(adversario:Personagem){
        val vidaPerdida = this.vidaMaxima - this.vida

        if(vidaPerdida <= 0){
            println("$nome tentou usar Penitência, mas está com a vida cheia!")
            return
        }

        // Lógica de Cura: Recupera metade da vida perdida
        val cura = vidaPerdida / 2
        this.vida += cura
        println("$nome se curou em $cura pontos! ❤️")

        // Lógica de Dano: Causa dano baseado na vida que ainda falta
        // Ex: Causa 100% da vida perdida como dano no inimigo
        println("A agonia de $nome é refletida em seu adversário!")
        adversario.receberDano(vidaPerdida)
    }
}