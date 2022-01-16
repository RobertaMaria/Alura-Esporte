package br.com.alura.aluraesporte.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.alura.aluraesporte.database.dao.ProdutoDAO
import br.com.alura.aluraesporte.model.Produto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.math.BigDecimal

private const val TAG = "produtoRepository"

class ProdutoRepository(private val dao: ProdutoDAO, private val firestore: FirebaseFirestore) {

    //fun buscaTodos(): LiveData<List<Produto>> = dao.buscaTodos()

    fun buscaPorId(id: Long): LiveData<Produto> = dao.buscaPorId(id)

    fun salva() {
        val produto = Produto(nome = "Chuteira", preco = BigDecimal(129.99))
        val produtoMapeado = mapOf<String, Any>(
            "nome" to produto.nome,
            "preco" to produto.preco.toDouble()
        )

        firestore.collection("produtos").add(produtoMapeado).addOnSuccessListener {
            it?.let {
                Log.i(TAG, "onCreate: produto salvo ${it.id}")
            }
        }
    }

    fun buscaTodosFirestore(): LiveData<List<Produto>>{
        val mutableLiveData = MutableLiveData<List<Produto>>()
        firestore.collection("produtos").get().addOnSuccessListener {
            it?.let {
                val produtos = mutableListOf<Produto>()
                for (documento in it) {
                    Log.i(TAG, "onCreate: produto encontrado ${documento.data}")
                    documento.data?.let {dados ->
                        val nome: String = dados["nome"] as String
                        val preco: Double = dados["preco"] as Double

                        var produto = Produto(nome = nome, preco = BigDecimal(preco))
                        produtos.add(produto)
                        mutableLiveData.value = produtos
                    }

                }
            }
        }
        return mutableLiveData
    }

}
