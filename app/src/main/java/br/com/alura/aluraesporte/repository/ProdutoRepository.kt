package br.com.alura.aluraesporte.repository

import android.text.BoringLayout
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.alura.aluraesporte.database.dao.ProdutoDAO
import br.com.alura.aluraesporte.model.Produto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.math.BigDecimal

private const val TAG = "produtoRepository"

class ProdutoRepository(private val dao: ProdutoDAO, private val firestore: FirebaseFirestore) {

    //fun buscaTodos(): LiveData<List<Produto>> = dao.buscaTodos()

    fun buscaPorId(id: Long): LiveData<Produto> = dao.buscaPorId(id)

    fun salva(produto: Produto): LiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()

        //val produto = Produto(nome = "Chuteira", preco = BigDecimal(129.99))
//        val produtoMapeado = mapOf<String, Any>(
//            "nome" to produto.nome,
//            "preco" to produto.preco.toDouble()
//        )

        val produtoDocumento = ProdutoDocumento(nome = produto.nome, preco = produto.preco.toDouble())

        firestore.collection("produtos").add(produtoDocumento).addOnSuccessListener {
           mutableLiveData.value = true
        }.addOnFailureListener{
            mutableLiveData.value = false
        }

        return mutableLiveData
    }

    fun buscaTodosFirestore(): LiveData<List<Produto>>{
        val mutableLiveData = MutableLiveData<List<Produto>>()
        firestore.collection("produtos").get().addOnSuccessListener {
            it?.let {
                val produtos = mutableListOf<Produto>()
                for (documento in it.documents) {
                    Log.i(TAG, "onCreate: produto encontrado ${documento.data}")
//                    documento.data?.let {dados ->
//                        val nome: String = dados["nome"] as String
//                        val preco: Double = dados["preco"] as Double
//
//                        val produto = Produto(nome = nome, preco = BigDecimal(preco))
//                        produtos.add(produto)
//
//                    }
                    val produtoDocumento = documento.toObject<ProdutoDocumento>()
                    produtoDocumento?.let {
                        produtos.add(it.paraProduto())
                    }
                }
                mutableLiveData.value = produtos
            }
        }
        return mutableLiveData
    }

    private class ProdutoDocumento(
        val nome: String = "",
        val preco: Double = 0.0
    ) {
        fun paraProduto(): Produto {
          return  Produto(nome = nome, preco = BigDecimal(preco))
        }

    }

}
