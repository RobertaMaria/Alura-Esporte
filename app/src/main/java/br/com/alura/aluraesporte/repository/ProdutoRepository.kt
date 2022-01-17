package br.com.alura.aluraesporte.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.alura.aluraesporte.database.dao.ProdutoDAO
import br.com.alura.aluraesporte.model.Produto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.math.BigDecimal
import java.math.RoundingMode

private const val TAG = "produtoRepository"
private const val COLECAO_FIRESTORE_PRODUTOS = "produtos"

class ProdutoRepository(private val dao: ProdutoDAO, private val firestore: FirebaseFirestore) {

    //fun buscaTodos(): LiveData<List<Produto>> = dao.buscaTodos()

    //fun buscaPorId(id: Long): LiveData<Produto> = dao.buscaPorId(id)
    fun buscaPorId(id: String): LiveData<Produto> = MutableLiveData<Produto>().apply {
        firestore.collection(COLECAO_FIRESTORE_PRODUTOS).document(id)
            .addSnapshotListener{snapshot, exception ->
                snapshot?.let {
                    it.toObject<ProdutoDocumento>()?.paraProduto(it.id)?.let {
                        value = it
                    }
                }

            }
    }

    fun salva(produto: Produto): LiveData<Boolean> = MutableLiveData<Boolean>().apply {
        //val produto = Produto(nome = "Chuteira", preco = BigDecimal(129.99))
//        val produtoMapeado = mapOf<String, Any>(
//            "nome" to produto.nome,
//            "preco" to produto.preco.toDouble()
//        )

        val produtoDocumento = ProdutoDocumento(nome = produto.nome, preco = produto.preco.toDouble())

        val colecao = firestore.collection(COLECAO_FIRESTORE_PRODUTOS)
        val documento = produto.idFirestore?.let {
            colecao.document(it)
        }?: colecao.document()

        //Adiciona no modo online
//        firestore.collection(COLECAO_FIRESTORE_PRODUTOS).add(produtoDocumento)
//            .addOnSuccessListener {
//            value = true
//        }.addOnFailureListener{
//            value = false
//        }

        //Adiciona no modo offline
        //val documento = firestore.collection(COLECAO_FIRESTORE_PRODUTOS).document()
        documento.set(produtoDocumento)

        value = true


    }

//    fun buscaTodosFirestore(): LiveData<List<Produto>>{
//        val mutableLiveData = MutableLiveData<List<Produto>>()
//        firestore.collection(COLECAO_FIRESTORE_PRODUTOS).get().addOnSuccessListener {
//            it?.let {
//                val produtos = mutableListOf<Produto>()
//                for (documento in it.documents) {
//                    Log.i(TAG, "onCreate: produto encontrado ${documento.data}")
////                    documento.data?.let {dados ->
////                        val nome: String = dados["nome"] as String
////                        val preco: Double = dados["preco"] as Double
////
////                        val produto = Produto(nome = nome, preco = BigDecimal(preco))
////                        produtos.add(produto)
////
////                    }
//                    val produtoDocumento = documento.toObject<ProdutoDocumento>()
//                    produtoDocumento?.let {
//                        produtos.add(it.paraProduto())
//                    }
//                }
//                mutableLiveData.value = produtos
//            }
//        }
//        return mutableLiveData
//    }

    fun buscaTodosFirestoreEmTempoReal(): LiveData<List<Produto>>{
        val mutableLiveData = MutableLiveData<List<Produto>>()
        firestore.collection(COLECAO_FIRESTORE_PRODUTOS).addSnapshotListener { snapshot, exception ->
            snapshot?.let {
//                val produtos = mutableListOf<Produto>()
//                for (documento in it.documents) {
//                    Log.i(TAG, "onCreate: produto encontrado em tempo real ${documento.data}")
//                    val produtoDocumento = documento.toObject<ProdutoDocumento>()
//                    produtoDocumento?.let {
//                        produtos.add(it.paraProduto())
//                    }
//                }
//                mutableLiveData.value = produtos

                val produtos = it.documents.mapNotNull { documento ->
                    documento.toObject<ProdutoDocumento>()?.paraProduto(documento.id)
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
        fun paraProduto(id: String): Produto {
          return  Produto(nome = nome, preco = BigDecimal(preco).setScale(2, RoundingMode.HALF_EVEN), idFirestore = id)
        }

    }

}
